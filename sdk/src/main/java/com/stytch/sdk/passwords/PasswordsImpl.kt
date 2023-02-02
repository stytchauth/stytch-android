package com.stytch.sdk.passwords

import com.stytch.sdk.AuthResponse
import com.stytch.sdk.BaseResponse
import com.stytch.sdk.PREFERENCES_CODE_VERIFIER
import com.stytch.sdk.PasswordsCreateResponse
import com.stytch.sdk.PasswordsStrengthCheckResponse
import com.stytch.sdk.StorageHelper
import com.stytch.sdk.StytchDispatchers
import com.stytch.sdk.StytchExceptions
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.sessions.SessionStorage
import com.stytch.sdk.sessions.launchSessionUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PasswordsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val storageHelper: StorageHelper,
    private val api: StytchApi.Passwords,
) : Passwords {

    override suspend fun authenticate(parameters: Passwords.AuthParameters): AuthResponse {
        val result: AuthResponse
        withContext(dispatchers.io) {
            result = api.authenticate(
                email = parameters.email,
                password = parameters.password,
                sessionDurationMinutes = parameters.sessionDurationMinutes
            ).apply {
                launchSessionUpdater(dispatchers, sessionStorage)
            }
        }
        return result
    }

    override fun authenticate(
        parameters: Passwords.AuthParameters,
        callback: (response: AuthResponse) -> Unit
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    override suspend fun create(parameters: Passwords.CreateParameters): PasswordsCreateResponse {
        val result: PasswordsCreateResponse

        withContext(dispatchers.io) {
            result = api.create(
                email = parameters.email,
                password = parameters.password,
                sessionDurationMinutes = parameters.sessionDurationMinutes
            ).apply {
                launchSessionUpdater(dispatchers, sessionStorage)
            }
        }

        return result
    }

    override fun create(
        parameters: Passwords.CreateParameters,
        callback: (response: PasswordsCreateResponse) -> Unit
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = create(parameters)
            callback(result)
        }
    }

    override suspend fun resetByEmailStart(parameters: Passwords.ResetByEmailStartParameters): BaseResponse {
        val result: BaseResponse

        withContext(dispatchers.io) {
            val challengeCodeMethod: String
            val challengeCode: String

            try {
                val challengePair = storageHelper.generateHashedCodeChallenge()
                challengeCodeMethod = challengePair.first
                challengeCode = challengePair.second
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchExceptions.Critical(ex))
                return@withContext
            }

            result = api.resetByEmailStart(
                email = parameters.email,
                codeChallenge = challengeCode,
                codeChallengeMethod = challengeCodeMethod,
                loginRedirectUrl = parameters.loginRedirectUrl,
                loginExpirationMinutes = parameters.loginExpirationMinutes?.toInt(),
                resetPasswordRedirectUrl = parameters.resetPasswordRedirectUrl,
                resetPasswordExpirationMinutes = parameters.resetPasswordExpirationMinutes?.toInt(),
            )
        }
        return result
    }

    override fun resetByEmailStart(
        parameters: Passwords.ResetByEmailStartParameters,
        callback: (response: BaseResponse) -> Unit
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = resetByEmailStart(parameters)
            callback(result)
        }
    }

    override suspend fun resetByEmail(parameters: Passwords.ResetByEmailParameters): AuthResponse {
        val result: AuthResponse

        // call backend endpoint
        withContext(dispatchers.io) {
            val codeVerifier: String
            try {
                codeVerifier = storageHelper.loadValue(PREFERENCES_CODE_VERIFIER)!!
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchExceptions.Critical(ex))
                return@withContext
            }

            result = api.resetByEmail(
                parameters.token,
                parameters.password,
                parameters.sessionDurationMinutes,
                codeVerifier
            ).apply {
                launchSessionUpdater(dispatchers, sessionStorage)
            }
        }

        return result
    }

    override fun resetByEmail(
        parameters: Passwords.ResetByEmailParameters,
        callback: (response: AuthResponse) -> Unit
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = resetByEmail(parameters)
            callback(result)
        }
    }

    override suspend fun strengthCheck(parameters: Passwords.StrengthCheckParameters): PasswordsStrengthCheckResponse {
        val result: PasswordsStrengthCheckResponse
        withContext(dispatchers.io) {
            result = api.strengthCheck(
                parameters.email,
                parameters.password
            )
        }
        return result
    }

    override fun strengthCheck(
        parameters: Passwords.StrengthCheckParameters,
        callback: (response: PasswordsStrengthCheckResponse) -> Unit
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = strengthCheck(parameters)
            callback(result)
        }
    }
}

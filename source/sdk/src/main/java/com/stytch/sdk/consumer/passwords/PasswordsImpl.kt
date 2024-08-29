package com.stytch.sdk.consumer.passwords

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchFailedToCreateCodeChallengeError
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.PasswordsCreateResponse
import com.stytch.sdk.consumer.PasswordsStrengthCheckResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PasswordsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.Passwords,
    private val pkcePairManager: PKCEPairManager,
) : Passwords {
    override suspend fun authenticate(parameters: Passwords.AuthParameters): AuthResponse {
        val result: AuthResponse
        withContext(dispatchers.io) {
            result =
                api
                    .authenticate(
                        email = parameters.email,
                        password = parameters.password,
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
                    ).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
        }
        return result
    }

    override fun authenticate(
        parameters: Passwords.AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    override suspend fun create(parameters: Passwords.CreateParameters): PasswordsCreateResponse {
        val result: PasswordsCreateResponse

        withContext(dispatchers.io) {
            result =
                api
                    .create(
                        email = parameters.email,
                        password = parameters.password,
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
                    ).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
        }

        return result
    }

    override fun create(
        parameters: Passwords.CreateParameters,
        callback: (response: PasswordsCreateResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = create(parameters)
            callback(result)
        }
    }

    override suspend fun resetByEmailStart(parameters: Passwords.ResetByEmailStartParameters): BaseResponse {
        val result: BaseResponse

        withContext(dispatchers.io) {
            val challengeCode: String

            try {
                val challengePair = pkcePairManager.generateAndReturnPKCECodePair()
                challengeCode = challengePair.codeChallenge
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchFailedToCreateCodeChallengeError(ex))
                return@withContext
            }

            result =
                api.resetByEmailStart(
                    email = parameters.email,
                    codeChallenge = challengeCode,
                    loginRedirectUrl = parameters.loginRedirectUrl,
                    loginExpirationMinutes = parameters.loginExpirationMinutes,
                    resetPasswordRedirectUrl = parameters.resetPasswordRedirectUrl,
                    resetPasswordExpirationMinutes = parameters.resetPasswordExpirationMinutes,
                    resetPasswordTemplateId = parameters.resetPasswordTemplateId,
                )
        }
        return result
    }

    override fun resetByEmailStart(
        parameters: Passwords.ResetByEmailStartParameters,
        callback: (response: BaseResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = resetByEmailStart(parameters)
            callback(result)
        }
    }

    override suspend fun resetByEmail(parameters: Passwords.ResetByEmailParameters): AuthResponse {
        val codeVerifier =
            pkcePairManager.getPKCECodePair()?.codeVerifier ?: return StytchResult.Error(StytchMissingPKCEError(null))
        return withContext(dispatchers.io) {
            api
                .resetByEmail(
                    parameters.token,
                    parameters.password,
                    parameters.sessionDurationMinutes,
                    codeVerifier,
                ).apply {
                    pkcePairManager.clearPKCECodePair()
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }
    }

    override fun resetByEmail(
        parameters: Passwords.ResetByEmailParameters,
        callback: (response: AuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = resetByEmail(parameters)
            callback(result)
        }
    }

    override suspend fun resetByExistingPassword(
        parameters: Passwords.ResetByExistingPasswordParameters,
    ): AuthResponse =
        withContext(dispatchers.io) {
            api
                .resetByExisting(
                    email = parameters.email,
                    existingPassword = parameters.existingPassword,
                    newPassword = parameters.newPassword,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }

    override fun resetByExistingPassword(
        parameters: Passwords.ResetByExistingPasswordParameters,
        callback: (AuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(resetByExistingPassword(parameters))
        }
    }

    override suspend fun resetBySession(parameters: Passwords.ResetBySessionParameters): AuthResponse =
        withContext(dispatchers.io) {
            api
                .resetBySession(
                    password = parameters.password,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }

    override fun resetBySession(
        parameters: Passwords.ResetBySessionParameters,
        callback: (AuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = resetBySession(parameters)
            callback(result)
        }
    }

    override suspend fun strengthCheck(parameters: Passwords.StrengthCheckParameters): PasswordsStrengthCheckResponse {
        val result: PasswordsStrengthCheckResponse
        withContext(dispatchers.io) {
            result =
                api.strengthCheck(
                    parameters.email,
                    parameters.password,
                )
        }
        return result
    }

    override fun strengthCheck(
        parameters: Passwords.StrengthCheckParameters,
        callback: (response: PasswordsStrengthCheckResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = strengthCheck(parameters)
            callback(result)
        }
    }
}

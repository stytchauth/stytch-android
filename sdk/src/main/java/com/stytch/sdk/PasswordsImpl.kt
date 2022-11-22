package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sessions.launchSessionUpdater
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PasswordsImpl internal constructor() : Passwords {

    override suspend fun authenticate(parameters: Passwords.AuthParameters): AuthResponse {
        val result: AuthResponse
        withContext(StytchClient.ioDispatcher) {

            result = StytchApi.Passwords.authenticate(
                email = parameters.email,
                password = parameters.password,
                sessionDurationMinutes = parameters.sessionDurationMinutes
            ).apply {
                launchSessionUpdater()
            }
        }
        return result
    }

    override fun authenticate(
        parameters: Passwords.AuthParameters,
        callback: (response: AuthResponse) -> Unit
    ) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    override suspend fun create(parameters: Passwords.CreateParameters): PasswordsCreateResponse {
        val result: PasswordsCreateResponse

        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.Passwords.create(
                email = parameters.email,
                password = parameters.password,
                sessionDurationMinutes = parameters.sessionDurationMinutes
            ).apply {
                launchSessionUpdater()
            }
        }

        return result
    }

    override fun create(
        parameters: Passwords.CreateParameters,
        callback: (response: PasswordsCreateResponse) -> Unit
    ) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = create(parameters)
            callback(result)
        }
    }

    override suspend fun resetByEmailStart(parameters: Passwords.ResetByEmailStartParameters): BaseResponse {
        val result: BaseResponse

        withContext(StytchClient.ioDispatcher) {

            val challengeCodeMethod: String
            val challengeCode: String

            try {
                val challengePair = StytchClient.storageHelper.generateHashedCodeChallenge()
                challengeCodeMethod = challengePair.first
                challengeCode = challengePair.second
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchExceptions.Critical(ex))
                return@withContext
            }

            result = StytchApi.Passwords.resetByEmailStart(
                email = parameters.email,
                codeChallenge = challengeCode,
                codeChallengeMethod = challengeCodeMethod,
                loginRedirectUrl = parameters.loginRedirectUrl,
                loginExpirationMinutes = parameters.loginExpirationMinutes,
                resetPasswordRedirectUrl = parameters.resetPasswordRedirectUrl,
                resetPasswordExpirationMinutes = parameters.resetPasswordExpirationMinutes,
            )
        }
        return result
    }

    override fun resetByEmailStart(
        parameters: Passwords.ResetByEmailStartParameters,
        callback: (response: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = resetByEmailStart(parameters)
            callback(result)
        }
    }

    override suspend fun resetByEmail(parameters: Passwords.ResetByEmailParameters): AuthResponse {
        val result: AuthResponse

        // call backend endpoint
        withContext(StytchClient.ioDispatcher) {

            val codeVerifier: String
            try {
                codeVerifier = StytchClient.storageHelper.loadValue(PREFERENCES_CODE_VERIFIER)!!
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchExceptions.Critical(ex))
                return@withContext
            }

            result = StytchApi.Passwords.resetByEmail(
                parameters.token,
                parameters.password,
                parameters.sessionDurationMinutes,
                codeVerifier
            ).apply {
                launchSessionUpdater()
            }
        }

        return result
    }

    override fun resetByEmail(
        parameters: Passwords.ResetByEmailParameters,
        callback: (response: AuthResponse) -> Unit
    ) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = resetByEmail(parameters)
            callback(result)
        }
    }

    override suspend fun strengthCheck(parameters: Passwords.StrengthCheckParameters): PasswordsStrengthCheckResponse {
        val result: PasswordsStrengthCheckResponse
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.Passwords.strengthCheck(
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
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = strengthCheck(parameters)
            callback(result)
        }
    }
}

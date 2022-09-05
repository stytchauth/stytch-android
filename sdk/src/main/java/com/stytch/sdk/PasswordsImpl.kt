package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PasswordsImpl internal constructor() : Passwords {

    override suspend fun authenticate(parameters: Passwords.AuthParameters): AuthResponse {
        val result: AuthResponse
        return withContext(StytchClient.ioDispatcher) {
            result = StytchApi.Passwords.authenticate(
                parameters.email,
                parameters.password,
                parameters.sessionDurationMinutes
            )

            result
        }
    }

    override fun authenticate(parameters: Passwords.AuthParameters, callback: (response: AuthResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    override suspend fun create(parameters: Passwords.CreateParameters): PasswordsCreateResponse {
        val result: PasswordsCreateResponse

        return withContext(StytchClient.ioDispatcher) {
            result = StytchApi.Passwords.create(
                email = parameters.email,
                password = parameters.password,
                sessionDurationMinutes = parameters.sessionDurationMinutes
            )

            result
        }
    }

    override fun create(parameters: Passwords.CreateParameters, callback: (response: PasswordsCreateResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = create(parameters)
            callback(result)
        }
    }

    override suspend fun resetByEmailStart(parameters: Passwords.ResetByEmailStartParameters): BaseResponse {
        val result: BaseResponse

        return withContext(StytchClient.ioDispatcher) {
            result = StytchApi.Passwords.resetByEmailStart(
                email = parameters.email,
                codeChallenge = parameters.codeChallenge,
                codeChallengeMethod = parameters.codeChallengeMethod,
                loginRedirectUrl = parameters.loginRedirectUrl,
                loginExpirationMinutes = parameters.loginExpirationMinutes,
                resetPasswordRedirectUrl = parameters.resetPasswordRedirectUrl,
                resetPasswordExpirationMinutes = parameters.resetPasswordExpirationMinutes,
            )

            result
        }
    }

    override fun resetByEmailStart(parameters: Passwords.ResetByEmailStartParameters, callback: (response: BaseResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = resetByEmailStart(parameters)
            callback(result)
        }
    }

    override suspend fun resetByEmail(parameters: Passwords.ResetByEmailParameters): AuthResponse {
        val result: AuthResponse

        return withContext(StytchClient.ioDispatcher) {
            result = StytchApi.Passwords.resetByEmail(
                parameters.token,
                parameters.password,
                parameters.sessionDurationMinutes,
                parameters.codeVerifier
            )

            result
        }
    }

    override fun resetByEmail(parameters: Passwords.ResetByEmailParameters, callback: (response: AuthResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = resetByEmail(parameters)
            callback(result)
        }
    }

    override suspend fun strengthCheck(parameters: Passwords.StrengthCheckParameters): PasswordsStrengthCheckResponse {
        val result: PasswordsStrengthCheckResponse
        return withContext(StytchClient.ioDispatcher) {
            result = StytchApi.Passwords.strengthCheck(
                parameters.email,
                parameters.password
            )

            result
        }
    }

    override fun strengthCheck(parameters: Passwords.StrengthCheckParameters, callback: (response: PasswordsStrengthCheckResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = strengthCheck(parameters)
            callback(result)
        }
    }
}

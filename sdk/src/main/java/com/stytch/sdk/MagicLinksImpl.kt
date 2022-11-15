package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sessions.launchSessionUpdater
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MagicLinksImpl internal constructor() : MagicLinks {

    override val email: MagicLinks.EmailMagicLinks = EmailMagicLinksImpl()

    override suspend fun authenticate(parameters: MagicLinks.AuthParameters): AuthResponse {
        var result: AuthResponse
        withContext(StytchClient.ioDispatcher) {
            val codeVerifier: String

            try {
                codeVerifier = StytchClient.storageHelper.loadValue(PREFERENCES_CODE_VERIFIER)!!
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchExceptions.Critical(ex))
                return@withContext
            }

            // call backend endpoint
            result = StytchApi.MagicLinks.Email.authenticate(
                parameters.token,
                parameters.sessionDurationMinutes,
                codeVerifier
            ).apply {
                launchSessionUpdater()
            }
        }

        return result
    }

    override fun authenticate(
        parameters: MagicLinks.AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    ) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(parameters)
//              change to main thread to call callback
            callback(result)
        }
    }

    private inner class EmailMagicLinksImpl : MagicLinks.EmailMagicLinks {

        override suspend fun loginOrCreate(
            parameters: MagicLinks.EmailMagicLinks.Parameters
        ): LoginOrCreateUserByEmailResponse {

            val result: LoginOrCreateUserByEmailResponse

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

                result = StytchApi.MagicLinks.Email.loginOrCreate(
                    email = parameters.email,
                    loginMagicLinkUrl = parameters.loginMagicLinkUrl,
                    challengeCode,
                    challengeCodeMethod
                )
            }

            return result
        }

        override fun loginOrCreate(
            parameters: MagicLinks.EmailMagicLinks.Parameters,
            callback: (response: LoginOrCreateUserByEmailResponse) -> Unit,
        ) {
//          call endpoint in IO thread
            GlobalScope.launch(StytchClient.uiDispatcher) {
                val result = loginOrCreate(parameters)
//              change to main thread to call callback
                callback(result)
            }
        }
    }
}

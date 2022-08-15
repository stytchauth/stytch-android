package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MagicLinksImpl internal constructor() : MagicLinks {

    override val email: MagicLinks.EmailMagicLinks = EmailMagicLinksImpl()

    override suspend fun authenticate(authParams: MagicLinks.AuthParameters): BaseResponse {
        val result: BaseResponse
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.MagicLinks.Email.authenticate(
                authParams.token,
                authParams.sessionDurationInMinutes,
                authParams.codeVerifier
            )
        }
        return result
    }

    override fun authenticate(
        authParams: MagicLinks.AuthParameters,
        callback: (response: BaseResponse) -> Unit,
    ) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(authParams)
//              change to main thread to call callback
            callback(result)
        }
    }

    private inner class EmailMagicLinksImpl : MagicLinks.EmailMagicLinks {

        override suspend fun loginOrCreate(parameters: MagicLinks.EmailMagicLinks.Parameters): LoginOrCreateUserByEmailResponse {
            val result: LoginOrCreateUserByEmailResponse

            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.MagicLinks.Email.loginOrCreateEmail(
                    email = parameters.email,
                    loginMagicLinkUrl = parameters.loginMagicLinkUrl,
                    codeChallenge = parameters.codeChallenge,
                    codeChallengeMethod = parameters.codeChallengeMethod
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
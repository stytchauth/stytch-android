package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MagicLinksImpl internal constructor() : MagicLinks {

    override val email: MagicLinks.EmailMagicLinks = EmailMagicLinksImpl()

    override suspend fun authenticate(authParams: MagicLinks.AuthParameters): BaseResponse {
        val result: BaseResponse
        val (_, challengeCode) = StytchClient.storageHelper.getHashedCodeChallenge(false)

        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.MagicLinks.Email.authenticate(
                authParams.token,
                authParams.sessionDurationMinutes,
                challengeCode
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
            val (challengeCodeMethod, challengeCode) = StytchClient.storageHelper.getHashedCodeChallenge(true)
            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.MagicLinks.Email.loginOrCreateEmail(
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
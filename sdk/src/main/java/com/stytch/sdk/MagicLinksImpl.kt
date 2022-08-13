package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MagicLinksImpl internal constructor() : MagicLinks {

    override val email: MagicLinks.EmailMagicLinks = EmailMagicLinksImpl()

    override suspend fun authenticate(token: String, sessionDurationInMinutes: UInt): BaseResponse {
        val result: BaseResponse
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.MagicLinks.Email.authenticate(token, sessionDurationInMinutes)
        }
        return result
    }

    override fun authenticate(
        token: String,
        sessionDurationInMinutes: UInt,
        callback: (response: BaseResponse) -> Unit,
    ) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(token, sessionDurationInMinutes)
//              change to main thread to call callback
            callback(result)
        }
    }

    private inner class EmailMagicLinksImpl : MagicLinks.EmailMagicLinks {

        override suspend fun loginOrCreate(parameters: MagicLinks.Parameters): LoginOrCreateUserByEmailResponse {
            val result: LoginOrCreateUserByEmailResponse

            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.MagicLinks.Email.loginOrCreateEmail(
                    email = parameters.email,
                    loginMagicLinkUrl = parameters.loginMagicLinkUrl
                )
            }

            return result
        }

        override fun loginOrCreate(
            parameters: MagicLinks.Parameters,
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
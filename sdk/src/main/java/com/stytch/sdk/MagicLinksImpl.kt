package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MagicLinksImpl internal constructor() : MagicLinks {

    /**
     * Wraps Stytch’s email magic link login_or_create endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
     * @param parameters required to receive magic link
     * @return LoginOrCreateUserByEmailResponse response from backend
     */
    override suspend fun loginOrCreate(parameters: MagicLinks.Parameters): LoginOrCreateUserByEmailResponse {
        val result: LoginOrCreateUserByEmailResponse

        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.MagicLinks.Email.loginOrCreateEmail(email = parameters.email,
                loginMagicLinkUrl = parameters.loginMagicLinkUrl,
                signupMagicLinkUrl = parameters.signupMagicLinkUrl,
                loginExpirationMinutes = parameters.loginExpirationInMinutes,
                signupExpirationMinutes = parameters.signupExpirationInMinutes)
        }

        return result
    }

    /**
     * Wraps Stytch’s email magic link login_or_create endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
     * @param parameters required to receive magic link
     * @param callback calls callback with LoginOrCreateUserByEmailResponse response from backend
     */
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

    /**
     * Wraps the magic link authenticate API endpoint which validates the magic link token passed in. If this method succeeds, the user will be logged in, granted an active session
     * @return LoginOrCreateUserByEmailResponse response from backend
     */
    override suspend fun authenticate(token: String, sessionExpirationMinutes: Int): BaseResponse {
        val result: BaseResponse
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.MagicLinks.Email.authenticate(token, sessionExpirationMinutes)
        }
        return result
    }

    override fun authenticate(
        token: String,
        sessionExpirationMinutes: Int,
        callback: (response: BaseResponse) -> Unit,
    ) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(token, sessionExpirationMinutes)
//              change to main thread to call callback
            callback(result)
        }
    }

}


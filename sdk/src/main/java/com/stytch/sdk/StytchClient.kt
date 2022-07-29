package com.stytch.sdk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

public typealias LoginOrCreateUserByEmailResponse = StytchResult<StytchResponseTypes.LoginOrCreateUserByEmailResponse>

/**
 * The entrypoint for all Stytch-related interaction.
 */
public object StytchClient {

    /**
     * Configures the StytchClient, setting the publicToken and hostUrl.
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @param hostUrl This is an https url which will be used as the domain for setting session-token cookies to be sent to your servers on subsequent requests
     */
    public fun configure(publicToken: String, hostUrl: String) {
        StytchApi.configure(publicToken, hostUrl)
    }

    //    TODO:("Magic Links")
    public object MagicLinks {
        /**
         * Wraps Stytch’s email magic link login_or_create endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive magic link
         * @return LoginOrCreateUserByEmailResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: Parameters): LoginOrCreateUserByEmailResponse {
            return StytchApi.MagicLinks.Email.loginOrCreateNew(
                email = parameters.email,
                loginMagicLinkUrl = parameters.loginMagicLinkUrl,
                signupMagicLinkUrl = parameters.signupMagicLinkUrl,
                loginExpirationMinutes = parameters.loginExpirationInMinutes,
                signupExpirationMinutes = parameters.signupExpirationInMinutes
            )
        }

        /**
         * Wraps Stytch’s email magic link login_or_create endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive magic link
         * @param callback calls callback with LoginOrCreateUserByEmailResponse response from backend
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: LoginOrCreateUserByEmailResponse) -> Unit,
        ) {
//          call endpoint in IO thread
            GlobalScope.launch(Dispatchers.IO){
                val result = loginOrCreate(parameters)
//              change to main thread to call callback
                withContext(Dispatchers.Main){
                    callback(result)
                }
            }
        }
//        fun authenticate(parameters:completion:)

        public data class Parameters(
            val email: String,
            val loginMagicLinkUrl: String? = null,
            val loginExpirationInMinutes: Int = 60,
            val signupMagicLinkUrl: String? = null,
            val signupExpirationInMinutes: Int = 60,
        )

        public class MagicLinksEmailLoginOrCreateResponse : StytchResult<StytchResponseTypes.LoginOrCreateUserByEmailResponse>()
    }

    //    TODO:("Sessions")
    public object Sessions {
//    fun revoke(completion:)
//    fun authenticate(parameters:completion:)
    }

    //    TODO:("OTP")
    public object OneTimePasscodes {
//    fun loginOrCreate(parameters:completion:)
//    fun authenticate(parameters:completion:)
    }

//    TODO("OAuth")
//    TODO("User Management")

}

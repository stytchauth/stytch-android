package com.stytch.sdk

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * The entrypoint for all Stytch-related interaction.
 */
public object StytchClient {

    /**
     * Configures the StytchClient, setting the publicToken and hostUrl.
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @param hostUrl This is an https url which will be used as the domain for setting session-token cookies to be sent to your servers on subsequent requests
     */
    public fun configure(publicToken: String, hostUrl: Uri) {
        TODO("implement")
    }

    //    TODO:("Magic Links")
    public object MagicLinks {
        public fun loginOrCreate(
            email: String,
            loginMagicLinkUrl: String,
            signupMagicLinkUrl: String,
            loginExpirationMinutes: Int? = null,
            signupExpirationMinutes: Int? = null,
            createUserAsPending: Boolean? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.LoginOrCreateUserByEmailResponse> {
            return runBlocking {
                StytchApi.MagicLinks.Email.loginOrCreate(
                    email = email,
                    loginMagicLinkUrl = loginMagicLinkUrl,
                    signupMagicLinkUrl = signupMagicLinkUrl,
                    loginExpirationMinutes = loginExpirationMinutes,
                    signupExpirationMinutes = signupExpirationMinutes,
                    createUserAsPending = createUserAsPending,
                    attributes = attributes,
                )
            }
        }

        public fun loginOrCreateAsync(
            email: String,
            loginMagicLinkUrl: String,
            signupMagicLinkUrl: String,
            loginExpirationMinutes: Int? = null,
            signupExpirationMinutes: Int? = null,
            createUserAsPending: Boolean? = null,
            attributes: StytchDataTypes.Attributes? = null,
            onSuccess: (result: StytchResult.Success<StytchResponseTypes.LoginOrCreateUserByEmailResponse>) -> Unit,
            onError: (errorCode: Int, errorResponse: StytchErrorResponse?) -> Unit,
            onNetworkError: (result: StytchResult<Nothing>) -> Unit,
        ) {
            GlobalScope.launch(Dispatchers.IO) {
                val result = StytchApi.MagicLinks.Email.loginOrCreate(
                    email = email,
                    loginMagicLinkUrl = loginMagicLinkUrl,
                    signupMagicLinkUrl = signupMagicLinkUrl,
                    loginExpirationMinutes = loginExpirationMinutes,
                    signupExpirationMinutes = signupExpirationMinutes,
                    createUserAsPending = createUserAsPending,
                    attributes = attributes,
                )
                when (result) {
                    is StytchResult.Success -> onSuccess(result)
                    is StytchResult.Error -> onError(result.errorCode, result.errorResponse)
                    is StytchResult.NetworkError -> onNetworkError(result)
                }
            }
        }
//        fun loginOrCreate(parameters:completion:),
//        fun authenticate(parameters:completion:)
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

package com.stytch.sdk.b2b.magicLinks

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.Constants

/**
 * MagicLinks interface that encompasses authentication functions as well as other related functionality
 */
public interface B2BMagicLinks {

    /**
     * Data class used for wrapping parameters used with MagicLinks authentication
     * @param token is the unique sequence of characters used to log in
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthParameters(
        val token: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Public variable that exposes an instance of EmailMagicLinks
     */
    public val email: EmailMagicLinks

    /**
     * Wraps the magic link authenticate API endpoint which validates the magic link token passed in. If this method succeeds, the user will be logged in, granted an active session
     * @param parameters required to authenticate
     * @return AuthResponse response from backend
     */
    public suspend fun authenticate(parameters: AuthParameters): AuthResponse

    /**
     * Wraps the magic link authenticate API endpoint which validates the magic link token passed in. If this method succeeds, the user will be logged in, granted an active session
     * @param parameters required to authenticate
     * @param callback calls callback with AuthResponse response from backend
     */
    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    /**
     * Provides all possible ways to call EmailMagicLinks endpoints
     */
    public interface EmailMagicLinks {

        /**
         * @param email is the account identifier for the account in the form of an Email address where you wish to receive a magic link to authenticate
         * @param organizationId is the member's organization ID
         * @param loginRedirectUrl is the url where you should be redirected for login
         * @param signupRedirectUrl is the url where you should be redirected for signup
         * @param loginTemplateId Use a custom template for login emails. By default, it will use your default email template. The template must be a template using our built-in customizations or a custom HTML email for Magic links - Login.
         * @param signupTemplateId Use a custom template for sign-up emails. By default, it will use your default email template. The template must be a template using our built-in customizations or a custom HTML email for Magic links - Sign-up.
         */
        public data class Parameters(
            val email: String,
            val organizationId: String,
            val loginRedirectUrl: String? = null,
            val signupRedirectUrl: String? = null,
            val loginTemplateId: String? = null,
            val signupTemplateId: String? = null,
        )

        /**
         * Wraps Stytch’s email magic link login_or_signup endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive magic link
         * @return BaseResponse response from backend
         */
        public suspend fun loginOrSignup(parameters: Parameters): BaseResponse

        /**
         * Wraps Stytch’s email magic link login_or_signup endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive magic link
         * @param callback calls callback with BaseResponse response from backend
         */
        public fun loginOrSignup(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )
    }
}

package com.stytch.sdk.consumer.magicLinks

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.Constants
import com.stytch.sdk.consumer.AuthResponse

/**
 * MagicLinks interface that encompasses authentication functions as well as other related functionality
 */
public interface MagicLinks {

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
    public suspend fun authenticate(
        parameters: AuthParameters,
    ): AuthResponse

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
         * @param loginMagicLinkUrl is the url where you should be redirected for login
         * @param signupMagicLinkUrl is the url where you should be redirected for signup
         * @param loginExpirationMinutes is the duration after which the login url should expire
         * @param signupExpirationMinutes is the duration after which the signup url should expire
         * @param loginTemplateId Use a custom template for login emails. By default, it will use your default email template. The template must be a template using our built-in customizations or a custom HTML email for Magic links - Login.
         * @param signupTemplateId Use a custom template for sign-up emails. By default, it will use your default email template. The template must be a template using our built-in customizations or a custom HTML email for Magic links - Sign-up.
         */
        public data class Parameters(
            val email: String,
            val loginMagicLinkUrl: String? = null,
            val signupMagicLinkUrl: String? = null,
            val loginExpirationMinutes: UInt? = null,
            val signupExpirationMinutes: UInt? = null,
            val loginTemplateId: String? = null,
            val signupTemplateId: String? = null,
        )

        /**
         * Wraps Stytch’s email magic link login_or_create endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive magic link
         * @return BaseResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: Parameters): BaseResponse

        /**
         * Wraps Stytch’s email magic link login_or_create endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive magic link
         * @param callback calls callback with BaseResponse response from backend
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )

        /**
         * Wraps Stytch’s email magic link send endpoint. Requests an email magic link for a user to authenticate.
         * @param parameters required to receive magic link
         * @return BaseResponse response from backend
         */
        public suspend fun send(parameters: Parameters): BaseResponse

        /**
         * Wraps Stytch’s email magic link send endpoint. Requests an email magic link for a user to authenticate.
         * @param parameters required to receive magic link
         * @param callback calls callback with BaseResponse response from backend
         */
        public fun send(parameters: Parameters, callback: (response: BaseResponse) -> Unit)
    }
}
package com.stytch.sdk.b2b.magicLinks

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.Constants

/**
 * The B2BMagicLinks interface provides methods for sending and authenticating users with Email Magic Links.
 *
 * Call the StytchB2BClient.magicLinks.email.loginOrSignup() method to request an email magic link for a user to log in
 * or create an account, based on if the email is associated with a user already. A new or pending user will receive a
 * signup magic link. An active user will receive a login magic link.
 *
 * If you have connected your deeplink handler with StytchB2BClient, the resulting magic link should be detected by
 * your application and automatically authenticated (via the StytchB2BClient.handle() method). See the instructions in
 * the top-level README for information on handling deeplink intents.
 *
 * If you are not using our deeplink handler, you must parse out the token from the deeplink yourself and pass it to
 * the StytchB2BClient.magicLinks.authenticate() method.
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
     * Public variable that exposes an instance of [EmailMagicLinks]
     */
    public val email: EmailMagicLinks

    /**
     * Authenticate a Member with a Magic Link. This endpoint requires a Magic Link token that is not expired or
     * previously used. Provide a value for session_duration_minutes to receive a Session. If the Member’s status is
     * pending, they will be updated to active.
     * @param parameters required to authenticate
     * @return [AuthResponse] response from backend
     */
    public suspend fun authenticate(parameters: AuthParameters): AuthResponse

    /**
     * Authenticate a Member with a Magic Link. This endpoint requires a Magic Link token that is not expired or
     * previously used. Provide a value for session_duration_minutes to receive a Session. If the Member’s status is
     * pending, they will be updated to active.
     * @param parameters required to authenticate
     * @param callback A callback that receives an [AuthResponse]
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
         * Data class used for wrapping parameters used with requesting an email magic link
         * @param email is the account identifier for the account in the form of an Email address where you wish to
         * receive a magic link to authenticate
         * @param organizationId is the member's organization ID
         * @param loginRedirectUrl is the url where you should be redirected for login
         * @param signupRedirectUrl is the url where you should be redirected for signup
         * @param loginTemplateId Use a custom template for login emails. By default, it will use your default email
         * template. The template must be a template using our built-in customizations or a custom HTML email for
         * Magic links - Login.
         * @param signupTemplateId Use a custom template for sign-up emails. By default, it will use your default email
         * template. The template must be a template using our built-in customizations or a custom HTML email for
         * Magic links - Sign-up.
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
         * Send either a login or signup magic link to a Member. A new or pending Member will receive a signup
         * Email Magic Link. An active Member will receive a login Email Magic Link.
         * @param parameters required to receive magic link
         * @return BaseResponse response from backend
         */
        public suspend fun loginOrSignup(parameters: Parameters): BaseResponse

        /**
         * Send either a login or signup magic link to a Member. A new or pending Member will receive a signup
         * Email Magic Link. An active Member will receive a login Email Magic Link.
         * @param parameters required to receive magic link
         * @param callback A callback that receives a [BaseResponse]
         */
        public fun loginOrSignup(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )
    }
}

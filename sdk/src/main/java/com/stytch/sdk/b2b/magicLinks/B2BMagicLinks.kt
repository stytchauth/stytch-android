package com.stytch.sdk.b2b.magicLinks

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.b2b.DiscoveryEMLAuthResponse
import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.Constants
import kotlinx.parcelize.RawValue

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
 *
 * Call the `StytchB2BClient.magicLinks.discovery.send()` method to send an invite email to a new Member to join an
 * Organization.
 *
 * Call the `StytchB2BClient.magicLinks.discovery.authenticate()` method to authenticate a Member with a Magic Link.
 */
public interface B2BMagicLinks {

    /**
     * Data class used for wrapping parameters used with MagicLinks authentication
     * @property token is the unique sequence of characters used to log in
     * @property sessionDurationMinutes indicates how long the session should last before it expires
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
     * @return [AuthResponse]
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
     * A data class used for wrapping parameters used for authenticating a discovery magic link
     * @property token the discovery magic links token
     */
    public data class DiscoveryAuthenticateParameters(
        val token: String,
    )

    /**
     * Authenticate a Member with a Discovery Magic Link. This endpoint requires a Magic Link token that is not
     * expired or previously used.
     * @param parameters required to authenticate
     * @return [DiscoveryEMLAuthResponse]
     */
    public suspend fun discoveryAuthenticate(parameters: DiscoveryAuthenticateParameters): DiscoveryEMLAuthResponse

    /**
     * Authenticate a Member with a Discovery Magic Link. This endpoint requires a Magic Link token that is not
     * expired or previously used.
     * @param parameters required to authenticate
     * @param callback a callback that receives a [DiscoveryEMLAuthResponse]
     */
    public fun discoveryAuthenticate(
        parameters: DiscoveryAuthenticateParameters,
        callback: (DiscoveryEMLAuthResponse) -> Unit
    )

    /**
     * Provides all possible ways to call EmailMagicLinks endpoints
     */
    public interface EmailMagicLinks {

        /**
         * Data class used for wrapping parameters used with requesting an email magic link
         * @property email is the account identifier for the account in the form of an Email address where you wish to
         * receive a magic link to authenticate
         * @property organizationId is the member's organization ID
         * @property loginRedirectUrl is the url where you should be redirected for login
         * @property signupRedirectUrl is the url where you should be redirected for signup
         * @property loginTemplateId Use a custom template for login emails. By default, it will use your default email
         * template. The template must be a template using our built-in customizations or a custom HTML email for
         * Magic links - Login.
         * @property signupTemplateId Use a custom template for sign-up emails. By default, it will use your default
         * email template. The template must be a template using our built-in customizations or a custom HTML email for
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
         * @return [BaseResponse]
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

        /**
         * A data class used for wrapping paramaters used for sending a discovery magic link
         * @property emailAddress is the account identifier for the account in the form of an Email address where you
         * wish to receive a magic link to authenticate
         * @property discoveryRedirectUrl is the url where you should be redirected for organization discovery
         * @property loginTemplateId Use a custom template for login emails. By default, it will use your default email
         * template. The template must be a template using our built-in customizations or a custom HTML email for
         * Magic links - Login.
         */
        public data class DiscoverySendParameters(
            val emailAddress: String,
            val discoveryRedirectUrl: String? = null,
            val loginTemplateId: String? = null,
        )

        /**
         * Send a discovery magic link to an email address
         * @param parameters required to send a discovery magic link
         * @return [BaseResponse]
         */
        public suspend fun discoverySend(parameters: DiscoverySendParameters): BaseResponse

        /**
         * Send a discovery magic link to an email address
         * @param parameters required to send a discovery magic link
         * @param callback a callback that receives a [BaseResponse]
         */
        public fun discoverySend(parameters: DiscoverySendParameters, callback: (BaseResponse) -> Unit)

        /**
         * A data class used for wrapping paramaters used for sending an invite magic link
         * @property emailAddress is the account identifier for the account in the form of an Email address where you
         * wish to receive a magic link to authenticate
         * @property inviteRedirectUrl The URL that the Member clicks from the invite Email Magic Link. This URL should
         * be an endpoint in the backend server that verifies the request by querying Stytch's authenticate endpoint and
         * finishes the invite flow. If this value is not passed, the default `invite_redirect_url` that you set in your
         * Dashboard is used. If you have not set a default `invite_redirect_url`, an error is returned.
         * @property inviteTemplateId Use a custom template for invite emails. By default, it will use your default
         * email template. The template must be a template using our built-in customizations or a custom HTML email for
         * Magic Links - Invite.
         * @property name The name of the Member.
         * @property untrustedMetadata A map containing application-specific metadata. Use it to store fields that a
         * member can be allowed to edit directly without backend validation - such as `display_theme` or
         * `preferred_locale`. See our [metadata reference](https://stytch.com/docs/api/metadata) for complete details.
         * @property locale The locale is used to determine which language to use in the email. Parameter is a
         * [IETF BCP 47 language tag](https://www.w3.org/International/articles/language-tags/), e.g. "en". Currently
         * supported languages are English ("en"), Spanish ("es"), and Brazilian Portuguese ("pt-br"); if no value is
         * provided, the copy defaults to English.
         * @property roles Roles to explicitly assign to this Member. See our
         * [RBAC guide](https://stytch.com/docs/b2b/guides/rbac/role-assignment) for more information about role
         * assignment.
         */
        public data class InviteParameters(
            val emailAddress: String,
            val inviteRedirectUrl: String? = null,
            val inviteTemplateId: String? = null,
            val name: String? = null,
            val untrustedMetadata: Map<String, Any?>? = null,
            val locale: String? = null,
            val roles: List<String>? = null,
        )

        /**
         * Send an invite email to a new Member to join an Organization. The Member will be created with an invited
         * status until they successfully authenticate. Sending invites to pending Members will update their status to
         * invited. Sending invites to already active Members will return an error.
         * @param parameters required to send an invite magic link
         * @return [MemberResponse]
         */
        public suspend fun invite(parameters: InviteParameters): MemberResponse

        /**
         * Send an invite email to a new Member to join an Organization. The Member will be created with an invited
         * status until they successfully authenticate. Sending invites to pending Members will update their status to
         * invited. Sending invites to already active Members will return an error.
         * @param parameters required to send a discovery magic link
         * @param callback a callback that receives a [MemberResponse]
         */
        public fun invite(parameters: InviteParameters, callback: (MemberResponse) -> Unit)
    }
}

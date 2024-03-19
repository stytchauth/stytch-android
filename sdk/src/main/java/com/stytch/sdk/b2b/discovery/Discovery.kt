package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.b2b.DiscoverOrganizationsResponse
import com.stytch.sdk.b2b.IntermediateSessionExchangeResponse
import com.stytch.sdk.b2b.OrganizationCreateResponse
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.AuthMethods
import com.stytch.sdk.b2b.network.models.EmailInvites
import com.stytch.sdk.b2b.network.models.EmailJitProvisioning
import com.stytch.sdk.b2b.network.models.SsoJitProvisioning
import com.stytch.sdk.common.Constants

/**
 * The Discovery interface provides methods for discovering a member's available organizations, creating organizations,
 * and exchanging sessions between organizations.
 * The Discovery product lets End Users discover and log in to Organizations they are a Member of, invited to, or
 * eligible to join.
 *
 * Unlike our other B2B products, Discovery allows End Users to authenticate without specifying an Organization in
 * advance. This is done via a Discovery Magic Link flow. After an End User is authenticated, an Intermediate Session
 * is returned along with a list of associated Organizations.
 *
 * The End User can then authenticate to the desired Organization by passing the Intermediate Session and
 * organization_id. End users can even create a new Organization instead of joining or logging in to an existing one.
 *
 * Call the `StytchB2BClient.discovery.organizations()` method to find a member's available organizations.
 *
 * Call the `StytchB2BClient.discovery.exchangeSession()` method to exchange a session between organizations.
 *
 * Call the `StytchB2BClient.discovery.create()` method to create a new organization.
 */
public interface Discovery {
    /**
     * Data class used for wrapping parameters used with Discovering organizations
     * @property intermediateSessionToken is the unique sequence of characters used to authenticate a member. If this
     * is not provided the existing session token will be used.
     */
    public data class DiscoverOrganizationsParameters(
        val intermediateSessionToken: String? = null,
    )

    /**
     * Discover a member's available organizations
     * @param parameters required for retrieving a member's available organizations
     * @return [DiscoverOrganizationsResponse]
     */
    public suspend fun listOrganizations(parameters: DiscoverOrganizationsParameters): DiscoverOrganizationsResponse

    /**
     * Discover a member's available organizations
     * @param parameters required for retrieving a member's available organizations
     * @param callback a callback that receives a [DiscoverOrganizationsResponse]
     */
    public fun listOrganizations(
        parameters: DiscoverOrganizationsParameters,
        callback: (DiscoverOrganizationsResponse) -> Unit,
    )

    /**
     * Data class used for wrapping parameters used with exchanging sessions between organizations.
     * @property intermediateSessionToken is the unique sequence of characters used to authenticate a member
     * @property organizationId is the organization ID of the desired organization
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class SessionExchangeParameters(
        val intermediateSessionToken: String,
        val organizationId: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Exchange an Intermediate Session for a fully realized Member Session in a desired Organization. This operation
     * consumes the Intermediate Session. This endpoint can be used to accept invites and create new members via domain
     * matching.
     * @param parameters required for exchanging a session between organizations
     * @return [IntermediateSessionExchangeResponse]
     */
    public suspend fun exchangeIntermediateSession(
        parameters: SessionExchangeParameters,
    ): IntermediateSessionExchangeResponse

    /**
     * Exchange an Intermediate Session for a fully realized Member Session in a desired Organization. This operation
     * consumes the Intermediate Session. This endpoint can be used to accept invites and create new members via domain
     * matching.
     * @param parameters required for exchanging a session between organizations
     * @param callback a callback that receives an [IntermediateSessionExchangeResponse]
     */
    public fun exchangeIntermediateSession(
        parameters: SessionExchangeParameters,
        callback: (IntermediateSessionExchangeResponse) -> Unit,
    )

    /**
     * A data class used for wrapping parameters used with creating organizations
     * @property intermediateSessionToken is the unique sequence of characters used to authenticate a member
     * @property organizationName is the name of the new organization
     * @property organizationSlug is the desired slug of the new organization
     * @property organizationLogoUrl is the optional URL of the new organization's logo
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     * @property ssoJitProvisioning The authentication setting that controls the JIT provisioning of Members when
     * authenticating via SSO. The accepted values are:
     * ALL_ALLOWED – new Members will be automatically provisioned upon successful authentication via any of the
     * Organization's sso_active_connections.
     * RESTRICTED – only new Members with SSO logins that comply with sso_jit_provisioning_allowed_connections can be
     * provisioned upon authentication.
     * NOT_ALLOWED – disable JIT provisioning via SSO.
     * @property emailAllowedDomains An array of email domains that allow invites or JIT provisioning for new Members.
     * This list is enforced when either email_invites or email_jit_provisioning is set to RESTRICTED.
     * @property emailJitProvisioning The authentication setting that controls how a new Member can be provisioned by
     * authenticating via Email Magic Link. The accepted values are:
     * RESTRICTED – only new Members with verified emails that comply with email_allowed_domains can be provisioned upon
     * authentication via Email Magic Link.
     * NOT_ALLOWED – disable JIT provisioning via Email Magic Link.
     * @property emailInvites The authentication setting that controls how a new Member can be invited to an
     * organization by email. The accepted values are:
     * ALL_ALLOWED – any new Member can be invited to join via email.
     * RESTRICTED – only new Members with verified emails that comply with email_allowed_domains can be invited via
     * email.
     * NOT_ALLOWED – disable email invites.
     * @property authMethods The setting that controls which authentication methods can be used by Members of an
     * Organization. The accepted values are:
     * ALL_ALLOWED – the default setting which allows all authentication methods to be used.
     * RESTRICTED - only methods that comply with allowed_auth_methods can be used for authentication. This setting does
     * not apply to Members with is_breakglass set to true.
     * @property allowedAuthMethods An array of allowed authentication methods. This list is enforced when auth_methods
     * is set to RESTRICTED. The list's accepted values are: sso , magic_link , and password .
     */
    public data class CreateOrganizationParameters(
        val intermediateSessionToken: String,
        val organizationName: String? = null,
        val organizationSlug: String? = null,
        val organizationLogoUrl: String? = null,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        val ssoJitProvisioning: SsoJitProvisioning? = null,
        val emailAllowedDomains: List<String>? = null,
        val emailJitProvisioning: EmailJitProvisioning? = null,
        val emailInvites: EmailInvites? = null,
        val authMethods: AuthMethods? = null,
        val allowedAuthMethods: List<AllowedAuthMethods>? = null,
    )

    /**
     * Create a new organization. If an end user does not want to join any already-existing organization, or has no
     * possible organizations to join, this endpoint can be used to create a new Organization and Member. This operation
     * consumes the Intermediate Session. This endpoint can also be used to start an initial session for the newly
     * created member and organization.
     * @param parameters required for creating an organization
     * @return [OrganizationCreateResponse]
     */
    public suspend fun createOrganization(parameters: CreateOrganizationParameters): OrganizationCreateResponse

    /**
     * Create a new organization. If an end user does not want to join any already-existing organization, or has no
     * possible organizations to join, this endpoint can be used to create a new Organization and Member. This operation
     * consumes the Intermediate Session. This endpoint can also be used to start an initial session for the newly
     * created member and organization.
     * @param parameters required for creating an organization
     * @param callback a callback that receives an [OrganizationCreateResponse]
     */
    public fun createOrganization(
        parameters: CreateOrganizationParameters,
        callback: (OrganizationCreateResponse) -> Unit,
    )
}

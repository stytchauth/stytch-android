package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.b2b.DiscoverOrganizationsResponse
import com.stytch.sdk.b2b.IntermediateSessionExchangeResponse
import com.stytch.sdk.b2b.OrganizationCreateResponse
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
        val intermediateSessionToken: String? = null
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
        callback: (DiscoverOrganizationsResponse) -> Unit
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
        parameters: SessionExchangeParameters
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
        callback: (IntermediateSessionExchangeResponse) -> Unit
    )

    /**
     * A data class used for wrapping parameters used with creating organizations
     * @property intermediateSessionToken is the unique sequence of characters used to authenticate a member
     * @property organizationName is the name of the new organization
     * @property organizationSlug is the desired slug of the new organization
     * @property organizationLogoUrl is the optional URL of the new organization's logo
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class CreateOrganizationParameters(
        val intermediateSessionToken: String,
        val organizationName: String? = null,
        val organizationSlug: String? = null,
        val organizationLogoUrl: String? = null,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
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
        callback: (OrganizationCreateResponse) -> Unit
    )
}

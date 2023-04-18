package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.b2b.DiscoverOrganizationsResponse
import com.stytch.sdk.b2b.IntermediateSessionExchangeResponse
import com.stytch.sdk.b2b.OrganizationCreateResponse
import com.stytch.sdk.common.Constants

/**
 *
 */
public interface Discovery {
    /**
     *
     */
    public data class DiscoverOrganizationsParameters(
        val intermediateSessionToken: String? = null
    )

    /**
     *
     */
    public suspend fun organizations(parameters: DiscoverOrganizationsParameters): DiscoverOrganizationsResponse

    /**
     *
     */
    public fun organizations(
        parameters: DiscoverOrganizationsParameters,
        callback: (DiscoverOrganizationsResponse) -> Unit
    )

    /**
     *
     */
    public data class SessionExchangeParameters(
        val intermediateSessionToken: String,
        val organizationId: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     *
     */
    public suspend fun exchangeSession(parameters: SessionExchangeParameters): IntermediateSessionExchangeResponse

    /**
     *
     */
    public fun exchangeSession(
        parameters: SessionExchangeParameters,
        callback: (IntermediateSessionExchangeResponse) -> Unit
    )

    /**
     *
     */
    public data class CreateOrganizationParameters(
        val intermediateSessionToken: String,
        val organizationName: String,
        val organizationSlug: String,
        val organizationLogoUrl: String? = null,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     *
     */
    public suspend fun create(parameters: CreateOrganizationParameters): OrganizationCreateResponse

    /**
     *
     */
    public fun create(
        parameters: CreateOrganizationParameters,
        callback: (OrganizationCreateResponse) -> Unit
    )
}

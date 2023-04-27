package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.common.Constants
import org.junit.Test

internal class DiscoveryTest {
    @Test
    fun `Discovery DiscoverOrganizationsParameters have correct default values`() {
        val params = Discovery.DiscoverOrganizationsParameters()
        val expected = Discovery.DiscoverOrganizationsParameters(intermediateSessionToken = null)
        assert(params == expected)
    }

    @Test
    fun `Discovery SessionExchangeParameters have correct default values`() {
        val params = Discovery.SessionExchangeParameters(
            intermediateSessionToken = "intermediate-session-token",
            organizationId = "organization-id"
        )
        val expected = Discovery.SessionExchangeParameters(
            intermediateSessionToken = "intermediate-session-token",
            organizationId = "organization-id",
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES
        )
        assert(params == expected)
    }

    @Test
    fun `Discovery CreateOrganizationParameters have correct default values`() {
        val params = Discovery.CreateOrganizationParameters(
            intermediateSessionToken = "intermediate-session-token",
        )
        val expected = Discovery.CreateOrganizationParameters(
            intermediateSessionToken = "intermediate-session-token",
            organizationName = null,
            organizationSlug = null,
            organizationLogoUrl = null,
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
        )
        assert(params == expected)
    }
}

package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import org.junit.Test

internal class DiscoveryTest {
    @Test
    fun `Discovery SessionExchangeParameters have correct default values`() {
        val params =
            Discovery.SessionExchangeParameters(
                organizationId = "organization-id",
            )
        val expected =
            Discovery.SessionExchangeParameters(
                organizationId = "organization-id",
                sessionDurationMinutes = DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }

    @Test
    fun `Discovery CreateOrganizationParameters have correct default values`() {
        val params = Discovery.CreateOrganizationParameters()
        val expected =
            Discovery.CreateOrganizationParameters(
                organizationName = null,
                organizationSlug = null,
                organizationLogoUrl = null,
                sessionDurationMinutes = DEFAULT_SESSION_TIME_MINUTES,
                ssoJitProvisioning = null,
                emailAllowedDomains = null,
                emailJitProvisioning = null,
                emailInvites = null,
                authMethods = null,
                allowedAuthMethods = null,
            )
        assert(params == expected)
    }
}

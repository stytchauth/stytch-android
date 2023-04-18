package com.stytch.sdk.b2b.magicLinks

import org.junit.Test

internal class B2BMagicLinksTest {
    @Test
    fun `MagicLinks EmailMagicLinks Parameters have correct default values`() {
        val params = B2BMagicLinks.EmailMagicLinks.Parameters(
            email = "emailaddress",
            organizationId = "organizationId"
        )
        val expected = B2BMagicLinks.EmailMagicLinks.Parameters(
            email = "emailaddress",
            organizationId = "organizationId",
            loginRedirectUrl = null,
            signupRedirectUrl = null,
            loginTemplateId = null,
            signupTemplateId = null,
        )
        assert(params == expected)
    }

    @Test
    fun `MagicLinks Discovery Send parameters have correct default values`() {
        val params = B2BMagicLinks.DiscoveryMagicLinks.SendParameters(
            emailAddress = "",
        )
        val expected = B2BMagicLinks.DiscoveryMagicLinks.SendParameters(
            emailAddress = "",
            discoveryRedirectUrl = null,
            loginTemplateId = null,
        )
        assert(params == expected)
    }
}

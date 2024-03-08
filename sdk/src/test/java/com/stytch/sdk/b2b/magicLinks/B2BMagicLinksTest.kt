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
    fun `MagicLinks EmailMagicLinks InviteParameters have correct default values`() {
        val params = B2BMagicLinks.EmailMagicLinks.InviteParameters(emailAddress = "emailaddress")
        val expected = B2BMagicLinks.EmailMagicLinks.InviteParameters(
            emailAddress = "emailaddress",
            inviteRedirectUrl = null,
            inviteTemplateId = null,
            name = null,
            untrustedMetadata = null,
            locale = null,
            roles = null
        )
        assert(params == expected)
    }

    @Test
    fun `MagicLinks Discovery Send parameters have correct default values`() {
        val params = B2BMagicLinks.EmailMagicLinks.DiscoverySendParameters(
            emailAddress = "",
        )
        val expected = B2BMagicLinks.EmailMagicLinks.DiscoverySendParameters(
            emailAddress = "",
            discoveryRedirectUrl = null,
            loginTemplateId = null,
        )
        assert(params == expected)
    }
}

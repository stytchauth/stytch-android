package com.stytch.sdk.magicLinks

import org.junit.Test

internal class MagicLinksTest {
    @Test
    fun `MagicLinks EmailMagicLinks LoginOrCreateParameters have correct default values`() {
        val params = MagicLinks.EmailMagicLinks.LoginOrCreateParameters("emailaddress")
        val expected = MagicLinks.EmailMagicLinks.LoginOrCreateParameters(
            email = "emailaddress",
            loginMagicLinkUrl = null,
            signupMagicLinkUrl = null,
            loginExpirationMinutes = null,
            signupExpirationMinutes = null,
            loginTemplateId = null,
            signupTemplateId = null,
        )
        assert(params == expected)
    }

    @Test
    fun `MagicLinks EmailMagicLinks SendParameters have correct default values`() {
        val params = MagicLinks.EmailMagicLinks.SendParameters("emailaddress")
        val expected = MagicLinks.EmailMagicLinks.SendParameters(
            email = "emailaddress",
            loginMagicLinkUrl = null,
            signupMagicLinkUrl = null,
            loginExpirationMinutes = null,
            signupExpirationMinutes = null,
            loginTemplateId = null,
            signupTemplateId = null,
        )
        assert(params == expected)
    }
}

package com.stytch.sdk.magicLinks

import org.junit.Test

internal class MagicLinksTest {
    @Test
    fun `MagicLinks EmailMagicLinks Parameters have correct default values`() {
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
}

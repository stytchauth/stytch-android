package com.stytch.sdk

import org.junit.Test

internal class MagicLinksTest {
    @Test
    fun `MagicLinks EmailMagicLinks Parameters have correct default values`() {
        val params = MagicLinks.EmailMagicLinks.Parameters("emailaddress")
        val expected = MagicLinks.EmailMagicLinks.Parameters(
            email = "emailaddress",
            loginMagicLinkUrl = null,
            signupMagicLinkUrl = null,
            loginExpirationMinutes = null,
            signupExpirationMinutes = null,
        )
        assert(params == expected)
    }
}

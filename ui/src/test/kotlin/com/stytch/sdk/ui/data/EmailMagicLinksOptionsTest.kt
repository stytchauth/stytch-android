package com.stytch.sdk.ui.data

import com.stytch.sdk.consumer.magicLinks.MagicLinks
import org.junit.Test

internal class EmailMagicLinksOptionsTest {
    @Test
    fun `EmailMagicLinksOptions toParameters produces expected output`() {
        val options = EmailMagicLinksOptions(
            loginRedirectURL = "login-redirect-url",
            loginExpirationMinutes = 30U,
            signupRedirectURL = "signup-redirect-url",
            signupExpirationMinutes = 20U,
            loginTemplateId = "login-template-id",
            signupTemplateId = "signup-template-id",
        )
        val expected = MagicLinks.EmailMagicLinks.Parameters(
            email = "my@email.com",
            loginMagicLinkUrl = options.loginRedirectURL,
            signupMagicLinkUrl = options.signupRedirectURL,
            loginExpirationMinutes = options.loginExpirationMinutes,
            signupExpirationMinutes = options.signupExpirationMinutes,
            loginTemplateId = options.loginTemplateId,
            signupTemplateId = options.signupTemplateId,
        )
        assert(options.toParameters("my@email.com") == expected)
    }
}
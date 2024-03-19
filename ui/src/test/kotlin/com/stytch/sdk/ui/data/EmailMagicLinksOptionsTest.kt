package com.stytch.sdk.ui.data

import com.stytch.sdk.consumer.magicLinks.MagicLinks
import org.junit.Test

internal class EmailMagicLinksOptionsTest {
    @Test
    fun `EmailMagicLinksOptions toParameters produces expected output`() {
        val options =
            EmailMagicLinksOptions(
                loginExpirationMinutes = 30,
                signupExpirationMinutes = 20,
                loginTemplateId = "login-template-id",
                signupTemplateId = "signup-template-id",
            )
        val expected =
            MagicLinks.EmailMagicLinks.Parameters(
                email = "my@email.com",
                loginMagicLinkUrl = "stytchui-publicToken://deeplink",
                signupMagicLinkUrl = "stytchui-publicToken://deeplink",
                loginExpirationMinutes = options.loginExpirationMinutes?.toUInt(),
                signupExpirationMinutes = options.signupExpirationMinutes?.toUInt(),
                loginTemplateId = options.loginTemplateId,
                signupTemplateId = options.signupTemplateId,
            )
        assert(options.toParameters("my@email.com", "publicToken") == expected)
    }
}

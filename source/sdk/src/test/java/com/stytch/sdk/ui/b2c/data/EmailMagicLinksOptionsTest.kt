package com.stytch.sdk.ui.b2c.data

import com.stytch.sdk.common.network.models.Locale
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
                loginMagicLinkUrl = "publicToken://b2c-ui",
                signupMagicLinkUrl = "publicToken://b2c-ui",
                loginExpirationMinutes = options.loginExpirationMinutes,
                signupExpirationMinutes = options.signupExpirationMinutes,
                loginTemplateId = options.loginTemplateId,
                signupTemplateId = options.signupTemplateId,
                locale = Locale.EN,
            )
        assert(options.toParameters("my@email.com", "publicToken", Locale.EN) == expected)
    }
}

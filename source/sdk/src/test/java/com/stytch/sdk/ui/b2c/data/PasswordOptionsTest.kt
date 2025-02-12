package com.stytch.sdk.ui.b2c.data

import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.consumer.passwords.Passwords
import org.junit.Test

internal class PasswordOptionsTest {
    @Test
    fun `PasswordOptions toResetByEmailStartParameters returns the expected output`() {
        val options =
            PasswordOptions(
                loginExpirationMinutes = 30,
                resetPasswordExpirationMinutes = 20,
                resetPasswordTemplateId = "reset-password-template-id",
            )
        val expected =
            Passwords.ResetByEmailStartParameters(
                email = "my@email.com",
                loginRedirectUrl = "stytchui-publicToken://deeplink",
                loginExpirationMinutes = options.loginExpirationMinutes,
                resetPasswordRedirectUrl = "stytchui-publicToken://deeplink",
                resetPasswordExpirationMinutes = options.resetPasswordExpirationMinutes,
                resetPasswordTemplateId = options.resetPasswordTemplateId,
                locale = Locale.EN,
            )
        assert(options.toResetByEmailStartParameters("my@email.com", "publicToken", Locale.EN) == expected)
    }
}

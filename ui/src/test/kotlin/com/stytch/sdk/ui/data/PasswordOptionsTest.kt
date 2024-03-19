package com.stytch.sdk.ui.data

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
                loginExpirationMinutes = options.loginExpirationMinutes?.toUInt(),
                resetPasswordRedirectUrl = "stytchui-publicToken://deeplink",
                resetPasswordExpirationMinutes = options.resetPasswordExpirationMinutes?.toUInt(),
                resetPasswordTemplateId = options.resetPasswordTemplateId,
            )
        assert(options.toResetByEmailStartParameters("my@email.com", "publicToken") == expected)
    }
}

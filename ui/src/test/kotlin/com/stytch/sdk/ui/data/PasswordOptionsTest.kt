package com.stytch.sdk.ui.data

import com.stytch.sdk.consumer.passwords.Passwords
import org.junit.Test

internal class PasswordOptionsTest {
    @Test
    fun `PasswordOptions toResetByEmailStartParameters returns the expected output`() {
        val options = PasswordOptions(
            loginExpirationMinutes = 30U,
            resetPasswordExpirationMinutes = 20U,
            resetPasswordTemplateId = "reset-password-template-id",
        )
        val expected = Passwords.ResetByEmailStartParameters(
            email = "my@email.com",
            loginRedirectUrl = "stytchui-publicToken://deeplink",
            loginExpirationMinutes = options.loginExpirationMinutes,
            resetPasswordRedirectUrl = "stytchui-publicToken://deeplink",
            resetPasswordExpirationMinutes = options.resetPasswordExpirationMinutes,
            resetPasswordTemplateId = options.resetPasswordTemplateId,
        )
        assert(options.toResetByEmailStartParameters("my@email.com", "publicToken") == expected)
    }
}

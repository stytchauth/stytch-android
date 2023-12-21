package com.stytch.sdk.ui.data

import com.stytch.sdk.consumer.passwords.Passwords
import org.junit.Test

internal class PasswordOptionsTest {
    @Test
    fun `PasswordOptions toResetByEmailStartParameters returns the expected output`() {
        val options = PasswordOptions(
            loginRedirectURL = "login-redirect-url",
            loginExpirationMinutes = 30U,
            resetPasswordRedirectURL = "reset-password-redirect-url",
            resetPasswordExpirationMinutes = 20U,
            resetPasswordTemplateId = "reset-password-template-id",
        )
        val expected = Passwords.ResetByEmailStartParameters(
            email = "my@email.com",
            loginRedirectUrl = options.loginRedirectURL,
            loginExpirationMinutes = options.loginExpirationMinutes,
            resetPasswordRedirectUrl = options.resetPasswordRedirectURL,
            resetPasswordExpirationMinutes = options.resetPasswordExpirationMinutes,
            resetPasswordTemplateId = options.resetPasswordTemplateId,
        )
        assert(options.toResetByEmailStartParameters("my@email.com") == expected)
    }
}

package com.stytch.sdk.b2b.passwords

import com.stytch.sdk.common.Constants
import org.junit.Test

internal class PasswordsTest {
    @Test
    fun `Passwords AuthParameters have correct default values`() {
        val params = Passwords.AuthParameters("organizationId", "emailAddress", "password")
        val expected =
            Passwords.AuthParameters(
                organizationId = "organizationId",
                emailAddress = "emailAddress",
                password = "password",
                sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }

    @Test
    fun `Passwords ResetByEmailStartParameters have correct default values`() {
        val params = Passwords.ResetByEmailStartParameters("organizationId", "emailAddress")
        val expected =
            Passwords.ResetByEmailStartParameters(
                organizationId = "organizationId",
                emailAddress = "emailAddress",
                loginRedirectUrl = null,
                resetPasswordRedirectUrl = null,
                resetPasswordExpirationMinutes = null,
                resetPasswordTemplateId = null,
            )
        assert(params == expected)
    }

    @Test
    fun `Passwords ResetByEmailParameters have correct default values`() {
        val params = Passwords.ResetByEmailParameters("token", "password")
        val expected =
            Passwords.ResetByEmailParameters(
                token = "token",
                password = "password",
                sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }

    @Test
    fun `Passwords ResetByExistingPasswordParameters have correct default values`() {
        val params =
            Passwords.ResetByExistingPasswordParameters(
                organizationId = "organizationId",
                emailAddress = "emailAddress",
                existingPassword = "existingPassword",
                newPassword = "newPassword",
            )
        val expected =
            Passwords.ResetByExistingPasswordParameters(
                organizationId = "organizationId",
                emailAddress = "emailAddress",
                existingPassword = "existingPassword",
                newPassword = "newPassword",
                sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }

    @Test
    fun `Passwords StrengthCheckParameters have correct default values`() {
        val params =
            Passwords.StrengthCheckParameters(
                password = "password",
            )
        val expected =
            Passwords.StrengthCheckParameters(
                email = null,
                password = "password",
            )
        assert(params == expected)
    }
}

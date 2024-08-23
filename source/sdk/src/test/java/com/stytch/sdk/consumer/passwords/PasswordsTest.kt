package com.stytch.sdk.consumer.passwords

import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import org.junit.Test

internal class PasswordsTest {
    @Test
    fun `Passwords AuthParameters have correct default values`() {
        val params = Passwords.AuthParameters("emailAddress", "password")
        val expected =
            Passwords.AuthParameters(
                email = "emailAddress",
                password = "password",
                sessionDurationMinutes = DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }

    @Test
    fun `Passwords CreateParameters have correct default values`() {
        val params = Passwords.CreateParameters("emailAddress", "password")
        val expected =
            Passwords.CreateParameters(
                email = "emailAddress",
                password = "password",
                sessionDurationMinutes = DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }

    @Test
    fun `Passwords ResetByEmailStartParameters have correct default values`() {
        val params = Passwords.ResetByEmailStartParameters("emailAddress")
        val expected =
            Passwords.ResetByEmailStartParameters(
                email = "emailAddress",
                loginRedirectUrl = null,
                loginExpirationMinutes = null,
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
                sessionDurationMinutes = DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }
}

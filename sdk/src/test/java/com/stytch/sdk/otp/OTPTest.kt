package com.stytch.sdk.otp

import com.stytch.sdk.Constants
import org.junit.Test

internal class OTPTest {
    @Test
    fun `OTP SmsOTP LoginOrCreateParameters have correct default values`() {
        val params = OTP.SmsOTP.LoginOrCreateParameters("phonenumber")
        val expected = OTP.SmsOTP.LoginOrCreateParameters(
            phoneNumber = "phonenumber",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
        )
        assert(params == expected)
    }

    @Test
    fun `OTP SmsOTP SendParameters have correct default values`() {
        val params = OTP.SmsOTP.SendParameters("phonenumber")
        val expected = OTP.SmsOTP.SendParameters(
            phoneNumber = "phonenumber",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
            locale = null,
            attributes = null,
            userId = null,
            sessionToken = null,
            sessionJwt = null,
        )
        assert(params == expected)
    }

    @Test
    fun `OTP WhatsAppOTP LoginOrCreateParameters have correct default values`() {
        val params = OTP.WhatsAppOTP.LoginOrCreateParameters("phonenumber")
        val expected = OTP.WhatsAppOTP.LoginOrCreateParameters(
            phoneNumber = "phonenumber",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
        )
        assert(params == expected)
    }

    @Test
    fun `OTP WhatsAppOTP SendParameters have correct default values`() {
        val params = OTP.WhatsAppOTP.SendParameters("phonenumber")
        val expected = OTP.WhatsAppOTP.SendParameters(
            phoneNumber = "phonenumber",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
            locale = null,
            attributes = null,
            userId = null,
            sessionToken = null,
            sessionJwt = null,
        )
        assert(params == expected)
    }

    @Test
    fun `OTP EmailOTP LoginOrCreateParameters have correct default values`() {
        val params = OTP.EmailOTP.LoginOrCreateParameters("emailAddress")
        val expected = OTP.EmailOTP.LoginOrCreateParameters(
            email = "emailAddress",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
            loginTemplateId = null,
            signupTemplateId = null
        )
        assert(params == expected)
    }

    @Test
    fun `OTP EmailOTP SendParameters have correct default values`() {
        val params = OTP.EmailOTP.SendParameters("emailAddress")
        val expected = OTP.EmailOTP.SendParameters(
            email = "emailAddress",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
            loginTemplateId = null,
            signupTemplateId = null,
            locale = null,
            attributes = null,
            userId = null,
            sessionToken = null,
            sessionJwt = null,
        )
        assert(params == expected)
    }
}

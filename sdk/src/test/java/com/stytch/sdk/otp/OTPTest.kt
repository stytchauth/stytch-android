package com.stytch.sdk.otp

import com.stytch.sdk.Constants
import org.junit.Test

internal class OTPTest {
    @Test
    fun `OTP SmsOTP Parameters have correct default values`() {
        val params = OTP.SmsOTP.LoginOrCreateParameters("phonenumber")
        val expected = OTP.SmsOTP.LoginOrCreateParameters(
            phoneNumber = "phonenumber",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
        )
        assert(params == expected)
    }

    @Test
    fun `OTP WhatsAppOTP Parameters have correct default values`() {
        val params = OTP.WhatsAppOTP.LoginOrCreateParameters("phonenumber")
        val expected = OTP.WhatsAppOTP.LoginOrCreateParameters(
            phoneNumber = "phonenumber",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
        )
        assert(params == expected)
    }

    @Test
    fun `OTP EmailOTP Parameters have correct default values`() {
        val params = OTP.EmailOTP.LoginOrCreateParameters("emailAddress")
        val expected = OTP.EmailOTP.LoginOrCreateParameters(
            email = "emailAddress",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
            loginTemplateId = null,
            signupTemplateId = null
        )
        assert(params == expected)
    }
}

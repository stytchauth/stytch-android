package com.stytch.sdk

import org.junit.Test

internal class OTPTest {
    @Test
    fun `OTP SmsOTP Parameters have correct default values`() {
        val params = OTP.SmsOTP.Parameters("phonenumber")
        val expected = OTP.SmsOTP.Parameters(
            phoneNumber = "phonenumber",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
        )
        assert(params == expected)
    }

    @Test
    fun `OTP WhatsAppOTP Parameters have correct default values`() {
        val params = OTP.WhatsAppOTP.Parameters("phonenumber")
        val expected = OTP.WhatsAppOTP.Parameters(
            phoneNumber = "phonenumber",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
        )
        assert(params == expected)
    }

    @Test
    fun `OTP EmailOTP Parameters have correct default values`() {
        val params = OTP.EmailOTP.Parameters("emailAddress")
        val expected = OTP.EmailOTP.Parameters(
            email = "emailAddress",
            expirationMinutes = Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
        )
        assert(params == expected)
    }
}

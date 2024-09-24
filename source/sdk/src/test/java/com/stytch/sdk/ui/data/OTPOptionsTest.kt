package com.stytch.sdk.ui.data

import com.stytch.sdk.consumer.otp.OTP
import org.junit.Test

internal class OTPOptionsTest {
    private val defaultOTPOptions =
        OTPOptions(
            expirationMinutes = 30,
            loginTemplateId = "login-template-id",
            signupTemplateId = "signup-template-id",
        )

    @Test
    fun `OTPOptions toEmailOtpParameters produces expected output`() {
        val expected =
            OTP.EmailOTP.Parameters(
                email = "my@email.com",
                expirationMinutes = defaultOTPOptions.expirationMinutes,
                loginTemplateId = defaultOTPOptions.loginTemplateId,
                signupTemplateId = defaultOTPOptions.signupTemplateId,
            )
        assert(defaultOTPOptions.toEmailOtpParameters("my@email.com") == expected)
    }

    @Test
    fun `OTPOptions toSMSOtpParameters produces expected output`() {
        val expected =
            OTP.SmsOTP.Parameters(
                phoneNumber = "123-456-7890",
                expirationMinutes = defaultOTPOptions.expirationMinutes,
                enableAutofill = true,
            )
        assert(defaultOTPOptions.toSMSOtpParameters("123-456-7890") == expected)
    }

    @Test
    fun `OTPOptions toWhatsAppOtpParameters produces expected output`() {
        val expected =
            OTP.WhatsAppOTP.Parameters(
                phoneNumber = "123-456-7890",
                expirationMinutes = defaultOTPOptions.expirationMinutes,
            )
        assert(defaultOTPOptions.toWhatsAppOtpParameters("123-456-7890") == expected)
    }
}

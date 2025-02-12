package com.stytch.sdk.ui.b2c.data

import com.stytch.sdk.common.network.models.Locale
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
                locale = Locale.EN,
            )
        assert(defaultOTPOptions.toEmailOtpParameters("my@email.com", Locale.EN) == expected)
    }

    @Test
    fun `OTPOptions toSMSOtpParameters produces expected output`() {
        val expected =
            OTP.SmsOTP.Parameters(
                phoneNumber = "123-456-7890",
                expirationMinutes = defaultOTPOptions.expirationMinutes,
                enableAutofill = true,
                locale = Locale.EN,
            )
        assert(defaultOTPOptions.toSMSOtpParameters("123-456-7890", Locale.EN) == expected)
    }

    @Test
    fun `OTPOptions toWhatsAppOtpParameters produces expected output`() {
        val expected =
            OTP.WhatsAppOTP.Parameters(
                phoneNumber = "123-456-7890",
                expirationMinutes = defaultOTPOptions.expirationMinutes,
                locale = Locale.EN,
            )
        assert(defaultOTPOptions.toWhatsAppOtpParameters("123-456-7890", Locale.EN) == expected)
    }
}

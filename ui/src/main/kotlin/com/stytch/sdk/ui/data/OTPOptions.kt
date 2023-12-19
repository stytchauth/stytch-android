package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.common.Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES
import com.stytch.sdk.consumer.otp.OTP
import kotlinx.parcelize.Parcelize

@Parcelize
public data class OTPOptions(
    val methods: List<OTPMethods> = emptyList(),
    val expirationMinutes: UInt = DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
    val loginTemplateId: String? = null,
    val signupTemplateId: String? = null,
) : Parcelable {
    internal fun toEmailOtpParameters(emailAddress: String) = OTP.EmailOTP.Parameters(
        email = emailAddress,
        expirationMinutes = expirationMinutes,
        loginTemplateId = loginTemplateId,
        signupTemplateId = signupTemplateId,
    )

    internal fun toSMSOtpParameters(phoneNumber: String) = OTP.SmsOTP.Parameters(
        phoneNumber = phoneNumber,
        expirationMinutes = expirationMinutes,
    )

    internal fun toWhatsAppOtpParameters(phoneNumber: String) = OTP.WhatsAppOTP.Parameters(
        phoneNumber = phoneNumber,
        expirationMinutes = expirationMinutes,
    )
}

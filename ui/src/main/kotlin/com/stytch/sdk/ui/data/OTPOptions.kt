package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES
import com.stytch.sdk.consumer.otp.OTP
import kotlinx.parcelize.Parcelize

/**
 * A data class representing options for configuring the OTP product
 * @property methods A list of [OTPMethods] that you would like to enable
 * @property expirationMinutes The number of minutes that an OTP code is valid for. Defaults to 10
 * @property loginTemplateId The ID of an OTP template (defined in the Stytch Dashboard) for login requests
 * @property signupTemplateId The ID of an OTP template (defined in the Stytch Dashboard) for signup requests
 */
@Parcelize
@JsonClass(generateAdapter = true)
public data class OTPOptions(
    val methods: List<OTPMethods> = emptyList(),
    val expirationMinutes: Int = DEFAULT_OTP_EXPIRATION_TIME_MINUTES.toInt(),
    val loginTemplateId: String? = null,
    val signupTemplateId: String? = null,
) : Parcelable {
    internal fun toEmailOtpParameters(emailAddress: String) = OTP.EmailOTP.Parameters(
        email = emailAddress,
        expirationMinutes = expirationMinutes.toUInt(),
        loginTemplateId = loginTemplateId,
        signupTemplateId = signupTemplateId,
    )

    internal fun toSMSOtpParameters(phoneNumber: String) = OTP.SmsOTP.Parameters(
        phoneNumber = phoneNumber,
        expirationMinutes = expirationMinutes.toUInt(),
    )

    internal fun toWhatsAppOtpParameters(phoneNumber: String) = OTP.WhatsAppOTP.Parameters(
        phoneNumber = phoneNumber,
        expirationMinutes = expirationMinutes.toUInt(),
    )
}

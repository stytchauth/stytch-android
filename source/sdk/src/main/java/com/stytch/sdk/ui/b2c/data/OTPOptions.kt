package com.stytch.sdk.ui.b2c.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.DEFAULT_OTP_EXPIRATION_TIME_MINUTES
import com.stytch.sdk.common.network.models.Locale
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
@Keep
@JsonClass(generateAdapter = true)
public data class OTPOptions
    @JvmOverloads
    constructor(
        val methods: List<OTPMethods> = emptyList(),
        val expirationMinutes: Int = DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
        val loginTemplateId: String? = null,
        val signupTemplateId: String? = null,
    ) : Parcelable {
        internal fun toEmailOtpParameters(
            emailAddress: String,
            locale: Locale,
        ) = OTP.EmailOTP.Parameters(
            email = emailAddress,
            expirationMinutes = expirationMinutes,
            loginTemplateId = loginTemplateId,
            signupTemplateId = signupTemplateId,
            locale = locale,
        )

        internal fun toSMSOtpParameters(
            phoneNumber: String,
            locale: Locale,
        ) = OTP.SmsOTP.Parameters(
            phoneNumber = phoneNumber,
            expirationMinutes = expirationMinutes,
            enableAutofill = true,
            locale = locale,
        )

        internal fun toWhatsAppOtpParameters(
            phoneNumber: String,
            locale: Locale,
        ) = OTP.WhatsAppOTP.Parameters(
            phoneNumber = phoneNumber,
            expirationMinutes = expirationMinutes,
            locale = locale,
        )
    }

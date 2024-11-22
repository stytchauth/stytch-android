package com.stytch.sdk.ui.b2c.data

import android.os.Parcelable
import com.stytch.sdk.consumer.otp.OTP
import kotlinx.parcelize.Parcelize

@Parcelize
internal sealed class OTPDetails : Parcelable {
    data class EmailOTP(val parameters: OTP.EmailOTP.Parameters, val methodId: String) : OTPDetails()

    data class SmsOTP(val parameters: OTP.SmsOTP.Parameters, val methodId: String) : OTPDetails()

    data class WhatsAppOTP(val parameters: OTP.WhatsAppOTP.Parameters, val methodId: String) : OTPDetails()
}

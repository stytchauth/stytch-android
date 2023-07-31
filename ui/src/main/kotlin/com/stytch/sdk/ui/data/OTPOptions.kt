package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.common.Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES
import kotlinx.parcelize.Parcelize

@Parcelize
public data class OTPOptions(
    val methods: List<OTPMethods>,
    val expirationMinutes: UInt = DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
    val loginTemplateId: String? = null,
    val signupTemplateId: String? = null,
) : Parcelable
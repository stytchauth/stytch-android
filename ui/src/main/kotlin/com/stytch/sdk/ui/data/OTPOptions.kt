package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class OTPOptions(
    val methods: List<OTPMethods>,
    val expirationMinutes: Int,
    val loginTemplateId: String? = null,
    val signupTemplateId: String? = null,
) : Parcelable
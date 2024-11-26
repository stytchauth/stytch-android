package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class TOTPEnrollmentState(
    val secret: String,
    val qrCode: String,
    val recoveryCodes: List<String>,
) : Parcelable

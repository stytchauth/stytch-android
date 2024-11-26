package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class MFATOTPState(
    val isCreating: Boolean = false,
    val enrollmentState: TOTPEnrollmentState? = null,
) : Parcelable

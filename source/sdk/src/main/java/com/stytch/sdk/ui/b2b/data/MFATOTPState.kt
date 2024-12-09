package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import com.stytch.sdk.common.errors.StytchError
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class MFATOTPState(
    val isCreating: Boolean = false,
    val isEnrolling: Boolean = false,
    val error: StytchError? = null,
    val enrollmentState: TOTPEnrollmentState? = null,
) : Parcelable

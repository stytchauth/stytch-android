package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
internal data class MFASMSState(
    val isEnrolling: Boolean = false,
    val didSend: Boolean = false,
    val codeExpiration: Date? = null,
    val formattedDestination: String? = null,
) : Parcelable

package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
internal data class MFASMSState(
    val isSending: Boolean = false,
    val codeExpiration: Date? = null,
    val formattedDestination: String? = null,
) : Parcelable

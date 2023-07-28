package com.stytch.sdk.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class SessionOptions(
    val sessionDurationMinutes: Int,
) : Parcelable

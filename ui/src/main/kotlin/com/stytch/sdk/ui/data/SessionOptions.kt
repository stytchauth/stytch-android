package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.common.Constants.DEFAULT_SESSION_TIME_MINUTES
import kotlinx.parcelize.Parcelize

@Parcelize
public data class SessionOptions(
    val sessionDurationMinutes: UInt = DEFAULT_SESSION_TIME_MINUTES,
) : Parcelable

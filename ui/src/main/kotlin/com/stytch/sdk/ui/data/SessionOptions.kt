package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.common.Constants.DEFAULT_SESSION_TIME_MINUTES
import kotlinx.parcelize.Parcelize

/**
 * A data class representing global session configuration options
 * @property sessionDurationMinutes The number of minutes that a granted session should be active. Defaults to 30
 */
@Parcelize
public data class SessionOptions(
    val sessionDurationMinutes: UInt = DEFAULT_SESSION_TIME_MINUTES,
) : Parcelable

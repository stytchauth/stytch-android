package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.Constants.DEFAULT_SESSION_TIME_MINUTES
import kotlinx.parcelize.Parcelize

/**
 * A data class representing global session configuration options
 * @property sessionDurationMinutes The number of minutes that a granted session should be active. Defaults to 30
 */
@Parcelize
@JsonClass(generateAdapter = true)
public data class SessionOptions(
    val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES.toInt(),
) : Parcelable

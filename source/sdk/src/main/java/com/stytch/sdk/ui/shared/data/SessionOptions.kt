package com.stytch.sdk.ui.shared.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import kotlinx.parcelize.Parcelize

/**
 * A data class representing global session configuration options
 * @property sessionDurationMinutes The number of minutes that a granted session should be active. Defaults to 30
 */
@Parcelize
@JsonClass(generateAdapter = true)
@JacocoExcludeGenerated
public data class SessionOptions
    @JvmOverloads
    constructor(
        val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
    ) : Parcelable

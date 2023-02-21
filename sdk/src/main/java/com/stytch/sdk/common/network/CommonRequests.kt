package com.stytch.sdk.common.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal object CommonRequests {
    object Sessions {
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int?,
        )
    }
}

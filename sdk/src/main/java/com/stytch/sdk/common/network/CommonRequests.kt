package com.stytch.sdk.common.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal object CommonRequests {
    object MagicLinks {
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val token: String,
            @Json(name = "code_verifier")
            val codeVerifier: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }
    object Sessions {
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int?,
        )
    }
}

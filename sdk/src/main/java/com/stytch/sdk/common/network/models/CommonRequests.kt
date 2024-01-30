package com.stytch.sdk.common.network.models

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

    object Events {
        @JsonClass(generateAdapter = true)
        data class Event(
            val telemetry: EventTelemetry,
            val event: EventEvent,
        )

        @JsonClass(generateAdapter = true)
        data class EventTelemetry(
            @Json(name = "event_id")
            val eventId: String,
            @Json(name = "app_session_id")
            val appSessionId: String,
            @Json(name = "persistent_id")
            val persistentId: String,
            @Json(name = "client_sent_at")
            val clientSentAt: String,
            val timezone: String,
            val app: VersionIdentifier,
            val os: VersionIdentifier,
            val sdk: VersionIdentifier,
            val device: DeviceIdentifier,
        )

        @JsonClass(generateAdapter = true)
        data class VersionIdentifier(
            val identifier: String,
            val version: String? = null
        )

        @JsonClass(generateAdapter = true)
        data class DeviceIdentifier(
            val model: String? = null,
            @Json(name = "screen_size")
            val screenSize: String? = null,
        )

        @JsonClass(generateAdapter = true)
        data class EventEvent(
            @Json(name = "public_token")
            val publicToken: String,
            @Json(name = "event_name")
            val eventName: String,
            val details: Map<String, Any>? = null,
            @Json(name = "error_description")
            val errorDescription: String? = null,
        )
    }
}

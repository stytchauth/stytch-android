package com.stytch.sdk.common.network.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.utils.IEnumValue

internal object CommonRequests {
    object Sessions {
        @Keep
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int?,
        )
    }

    object Events {
        @Keep
        @JsonClass(generateAdapter = true)
        data class Event(
            val telemetry: EventTelemetry,
            val event: EventEvent,
        )

        @Keep
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

        @Keep
        @JsonClass(generateAdapter = true)
        data class VersionIdentifier(
            val identifier: String,
            val version: String? = null,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class DeviceIdentifier(
            val model: String? = null,
            @Json(name = "screen_size")
            val screenSize: String? = null,
        )

        @Keep
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

@Keep
@JsonClass(generateAdapter = false)
public enum class Locale(
    override val jsonName: String,
) : IEnumValue {
    EN("en"),
    ES("es"),
    PT_BR("pt-br"),
}

package com.stytch.sdk.b2b.events

internal interface Events {
    fun logEvent(
        eventName: String,
        details: Map<String, Any>? = null,
        error: Exception? = null,
    )
}

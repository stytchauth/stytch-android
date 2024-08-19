package com.stytch.sdk.common.events

internal interface Events {
    fun logEvent(
        eventName: String,
        details: Map<String, Any>? = null,
        error: Exception? = null,
    )
}

package com.stytch.sdk.consumer.events

public interface Events {
    public fun logEvent(eventName: String, details: Map<String, Any>? = null, error: Exception? = null)
}

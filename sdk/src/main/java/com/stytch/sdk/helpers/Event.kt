package com.stytch.sdk.helpers

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
internal open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getEventNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

    override fun toString(): String {
        return "Event(${content.toString()})"
    }
}
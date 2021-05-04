package com.stytch.sdk

public class StytchEvent private constructor(val type: String, val created: Boolean, val userId: String) {

    companion object {
        private const val USER_EVENT = "user_event"

        fun userCreatedEvent(userId: String): StytchEvent {
            return StytchEvent(USER_EVENT, true, userId)
        }

        fun userFoundEvent(userId: String): StytchEvent {
            return StytchEvent(USER_EVENT, false, userId)
        }
    }
}

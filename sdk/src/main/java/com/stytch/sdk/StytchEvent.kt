package com.stytch.sdk

public class StytchEvent private constructor(
    public val type: String,
    public val created: Boolean,
    public val userId: String,
) {

    public companion object {
        private const val USER_EVENT = "user_event"

        public fun userCreatedEvent(userId: String): StytchEvent {
            return StytchEvent(USER_EVENT, true, userId)
        }

        public fun userFoundEvent(userId: String): StytchEvent {
            return StytchEvent(USER_EVENT, false, userId)
        }
    }
}

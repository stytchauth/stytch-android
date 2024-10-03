package com.stytch.sdk.consumer.sessions

import com.stytch.sdk.consumer.network.models.SessionData
import java.util.Date

public sealed interface StytchSession {
    public data object Unavailable : StytchSession

    public data class Available(
        public val lastValidatedAt: Date,
        public val sessionData: SessionData,
    ) : StytchSession
}

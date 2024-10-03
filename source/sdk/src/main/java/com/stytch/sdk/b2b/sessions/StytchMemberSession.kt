package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.b2b.network.models.B2BSessionData
import java.util.Date

public sealed interface StytchMemberSession {
    public data object Unavailable : StytchMemberSession

    public data class Available(
        public val lastValidatedAt: Date,
        public val memberSessionData: B2BSessionData,
    ) : StytchMemberSession
}

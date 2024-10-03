package com.stytch.sdk.b2b.member

import com.stytch.sdk.b2b.network.models.MemberData
import java.util.Date

public sealed interface StytchMember {
    public data object Unavailable : StytchMember

    public data class Available(
        public val lastValidatedAt: Date,
        public val memberData: MemberData,
    ) : StytchMember
}

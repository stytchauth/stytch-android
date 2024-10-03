package com.stytch.sdk.consumer.userManagement

import com.stytch.sdk.consumer.network.models.UserData
import java.util.Date

public sealed interface StytchUser {
    public data object Unavailable : StytchUser

    public data class Available(
        public val lastValidatedAt: Date,
        public val userData: UserData,
    ) : StytchUser
}

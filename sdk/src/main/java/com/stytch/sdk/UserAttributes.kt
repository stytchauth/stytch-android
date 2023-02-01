package com.stytch.sdk

import com.stytch.sdk.network.StytchRequests

public data class UserAttributes(
    val ipAddress: String?,
    val userAgent: String?
) {
    internal fun toApiAttributes(): StytchRequests.Attributes = StytchRequests.Attributes(
        ipAddress = ipAddress,
        userAgent = userAgent
    )
}

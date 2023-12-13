package com.stytch.sdk.common.errors

/**
 * An error class that occurs when Stytch SDK cannot reach the API
 */
public data class StytchAPIUnreachableError(
    public override val description: String,
    public val exception: Throwable? = null,
) : StytchError(
    name = "StytchAPIUnreachableError",
    description = description,
)

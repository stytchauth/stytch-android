package com.stytch.sdk.common.errors

/**
 * An error class that occurs when Stytch SDK cannot reach the API
 */
public data class StytchAPIUnreachableError(
    public override val message: String,
    public val exception: Throwable? = null,
) : StytchError(message = message)

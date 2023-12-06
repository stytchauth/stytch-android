package com.stytch.sdk.common.errors

/**
 * An error class that occurs when Stytch SDK cannot reach the API
 */
public data class StytchAPIUnreachableError(
    public override val description: String,
    public override val url: String? = null,
    public override val exception: Throwable?
) : StytchError(
    name = "StytchAPIUnreachableError",
    description = description,
    url = url,
    exception = exception,
)

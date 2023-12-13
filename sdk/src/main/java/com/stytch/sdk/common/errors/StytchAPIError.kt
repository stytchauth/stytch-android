package com.stytch.sdk.common.errors

/**
 * An error class representing non-schema error that occurs in Stytch API
 */
public data class StytchAPIError(
    public val requestId: String? = null,
    public override val name: String,
    public override val description: String,
    public val url: String? = null,
) : StytchError(
    name = name,
    description = description,
)

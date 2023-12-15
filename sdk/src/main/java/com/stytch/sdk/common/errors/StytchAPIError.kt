package com.stytch.sdk.common.errors

/**
 * An error class representing non-schema error that occurs in Stytch API
 */
public data class StytchAPIError(
    public val requestId: String? = null,
    public val errorType: String,
    public override val message: String,
    public val url: String? = null,
) : StytchError(message = message)

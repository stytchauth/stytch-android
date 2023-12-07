package com.stytch.sdk.common.errors

/**
 * A base error class for all errors returned by the Stytch SDK
 */
public sealed class StytchError(
    public val name: String,
    public val description: String,
    public val url: String? = null,
    public val exception: Throwable? = null,
) : Exception()

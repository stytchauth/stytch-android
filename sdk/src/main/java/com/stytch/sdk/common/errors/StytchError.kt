package com.stytch.sdk.common.errors

/**
 * A base error class for all errors returned by the Stytch SDK
 */
public sealed class StytchError(
    public open val name: String,
    public open val description: String,
) : Exception()

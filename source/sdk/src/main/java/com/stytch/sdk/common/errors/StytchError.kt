package com.stytch.sdk.common.errors

/**
 * A base error class for all errors returned by the Stytch SDK
 * @property message a string explaining what went wrong
 */
public sealed class StytchError(
    public override val message: String,
) : Exception()

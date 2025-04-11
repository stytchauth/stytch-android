package com.stytch.sdk.common.errors

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated

/**
 * A base error class for all errors returned by the Stytch SDK
 * @property message a string explaining what went wrong
 */
@JacocoExcludeGenerated
public sealed class StytchError(
    public override val message: String,
) : Exception()

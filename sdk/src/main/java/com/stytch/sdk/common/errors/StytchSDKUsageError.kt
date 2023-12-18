package com.stytch.sdk.common.errors

/**
 * An error that occurs when an SDK method is called with invalid input
 */
public data class StytchSDKUsageError(
    public override val message: String,
) : StytchError(message = message)

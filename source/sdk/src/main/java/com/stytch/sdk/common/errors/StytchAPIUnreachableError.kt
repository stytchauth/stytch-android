package com.stytch.sdk.common.errors

/**
 * An error that occurs when the Stytch SDK cannot reach the API. This may mean that there is a network error, or an
 * error occurred while creating the request
 * @property message a string explaining what went wrong
 * @property exception an optional [Throwable] that caused this error to occur
 */

public data class StytchAPIUnreachableError(
    public override val message: String,
    public val exception: Throwable? = null,
) : StytchError(message = message)

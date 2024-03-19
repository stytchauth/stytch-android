package com.stytch.sdk.common.errors

/**
 * A base class representing SDK specific errors or exceptions that may occur. This class should not be used directly,
 * rather we should be creating implementations for each of the known/expected errors we return.
 */
public sealed class StytchUIError(
    message: String,
    public open val exception: Throwable? = null,
) : StytchError(message = message)

/**
 * Thrown when the UI Activity failed
 */
public data class StytchUIActivityFailed(public val code: Int) : StytchUIError(
    message = "Activity failed with resultCode = $code",
)

/**
 * Thrown when there was an error parsing the activity intent
 */
public object StytchUINoDataFromIntent : StytchUIError(
    message = "Failed to retrieve data from intent",
)

/**
 * Thrown when your Stytch UI configuration is invalid
 */
public data class StytchUIInvalidConfiguration(
    public override val message: String,
) : StytchUIError(message = message)

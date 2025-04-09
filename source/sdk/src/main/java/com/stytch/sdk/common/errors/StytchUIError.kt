package com.stytch.sdk.common.errors

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated

/**
 * A base class representing SDK specific errors or exceptions that may occur. This class should not be used directly,
 * rather we should be creating implementations for each of the known/expected errors we return.
 * @property exception an optional [Throwable] that caused this error to occur
 */
public sealed class StytchUIError(
    message: String,
    public open val exception: Throwable? = null,
) : StytchError(message = message)

/**
 * Thrown when the UI Activity failed
 * @property code the Activity result code returned when the UI activity failed
 */
@JacocoExcludeGenerated
public data class StytchUIActivityFailed(
    val code: Int,
) : StytchUIError(message = "Activity failed with resultCode = $code")

/**
 * Thrown when there was an error parsing the activity intent
 */

public data object StytchUINoDataFromIntent : StytchUIError(
    message = "Failed to retrieve data from intent",
)

/**
 * Thrown when your Stytch UI configuration is invalid
 * @property message a string describing what is incorrect about the UI configuration that was provided
 */
@JacocoExcludeGenerated
public data class StytchUIInvalidConfiguration(
    public override val message: String,
) : StytchUIError(message = message)

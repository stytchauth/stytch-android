package com.stytch.sdk

import com.stytch.sdk.network.StytchErrorType

/**
 * Provides a wrapper for responses from Stytch API responses
 */
public sealed class StytchResult<out T> {
    /**
     * Data class that can hold a successful response from a Stytch endpoint
     * @param value is the value of the response
     */
    public data class Success<out T>(val value: T) : StytchResult<T>()

    /**
     * Data class that can hold a StytchException
     * @param exception provides information about what went wrong during an API call
     */
    public data class Error(val exception: StytchExceptions) : StytchResult<Nothing>()
}

/**
 * Converts a string to an error type supported by Stytch
 */
public fun String.toStytchErrorType(): StytchErrorType? {
    StytchErrorType.values().forEach {
        if (this == it.stringValue) return it
    }
    return null
}

internal fun <T> StytchResult<T>.getValueOrThrow(): T = when (this) {
    is StytchResult.Success -> value
    is StytchResult.Error -> throw exception
}

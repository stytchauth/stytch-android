package com.stytch.sdk.common

import android.os.Parcelable
import com.stytch.sdk.common.errors.StytchError
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Provides a wrapper for responses from the Stytch API
 */
@Parcelize
public sealed class StytchResult<out T> : Parcelable {
    /**
     * Data class that can hold a successful response from a Stytch endpoint
     * @property value is the value of the response
     */
    public data class Success<out T>(val value: @RawValue T) : StytchResult<T>()

    /**
     * Data class that can hold a StytchException
     * @property exception provides information about what went wrong during an API call
     */
    public data class Error(val exception: StytchError) : StytchResult<Nothing>()
}

internal fun <T> StytchResult<T>.getValueOrThrow(): T =
    when (this) {
        is StytchResult.Success -> value
        is StytchResult.Error -> throw exception
    }

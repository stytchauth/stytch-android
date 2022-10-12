package com.stytch.sdk

import com.stytch.sdk.network.StytchErrorType
import com.stytch.sdk.network.responseData.StytchErrorResponse

public sealed class StytchResult<out T> {

    /**
     * Data class that can hold a successful response from a Stytch endpoint
     * @param value is the value of the response
     */
    public data class Success<out T>(val value: T) : StytchResult<T>()

    /**
     * Data class that can hold a network error that happened while calling a Stytch endpoint
     */
    public object NetworkError : StytchResult<Nothing>()

    /**
     * Data class that can hold an error for when there was an attempt to call an endpoint without configuring the SDK first
     */
    public object SdkNotConfigured : StytchResult<Nothing>()

    /**
     * Data class that can hold a generic error from the server
     * @param errorCode is the error code of the failed request
     * @param errorResponse is the error response in human-readable language
     */
    public data class Error(val errorCode: Int, val errorResponse: StytchErrorResponse?) : StytchResult<Nothing>() {
        public val errorType: StytchErrorType? by lazy {
            errorResponse?.errorType?.toStytchErrorType()
        }
    }
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
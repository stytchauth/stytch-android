package com.stytch.sdk

import com.stytch.sdk.network.StytchErrorType
import com.stytch.sdk.network.responseData.StytchErrorResponse

public sealed class StytchResult<out T> {
    public data class Success<out T>(val value: T) : StytchResult<T>()
    public data class Error(val exception: StytchExceptions) : StytchResult<Nothing>() {}
}

public fun String.toStytchErrorType(): StytchErrorType? {
    StytchErrorType.values().forEach {
        if (this == it.stringValue) return it
    }
    return null
}
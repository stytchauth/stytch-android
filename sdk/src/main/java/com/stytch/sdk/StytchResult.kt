package com.stytch.sdk

import com.stytch.sdk.network.StytchErrorType
import com.stytch.sdk.network.responseData.StytchErrorResponse

public sealed class StytchResult<out T> {
    public data class Success<out T>(val value: T) : StytchResult<T>()
    public object NetworkError : StytchResult<Nothing>()
    public object SdkNotConfigured : StytchResult<Nothing>()
    public data class Error(val errorCode: Int, val errorResponse: StytchErrorResponse?) : StytchResult<Nothing>() {
        public val errorType: StytchErrorType? by lazy {
            errorResponse?.errorType?.toStytchErrorType()
        }
    }
}

public fun String.toStytchErrorType(): StytchErrorType? {
    StytchErrorType.values().forEach {
        if (this == it.stringValue) return it
    }
    return null
}
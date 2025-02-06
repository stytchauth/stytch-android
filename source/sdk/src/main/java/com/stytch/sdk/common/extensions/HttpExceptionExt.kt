package com.stytch.sdk.common.extensions

import com.squareup.moshi.Moshi
import com.stytch.sdk.common.StytchLog
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchAPIErrorType
import com.stytch.sdk.common.errors.StytchAPISchemaError
import com.stytch.sdk.common.errors.StytchAPIUnreachableError
import com.stytch.sdk.common.errors.StytchError
import com.stytch.sdk.common.network.models.StytchErrorResponse
import com.stytch.sdk.common.network.models.StytchSchemaError
import retrofit2.HttpException

internal fun HttpException.toStytchError(): StytchError {
    val errorCode = code()
    val source = response()?.errorBody()?.string()
    val parsedErrorResponse =
        try {
            // if we can parse this out to a StytchErrorResponse, it's an API error
            source?.let {
                Moshi
                    .Builder()
                    .build()
                    .adapter(StytchErrorResponse::class.java)
                    .fromJson(it)
            }
        } catch (t: Throwable) {
            try {
                // if we can parse this out to a StytchSchemaError, it's a schema error
                source?.let {
                    Moshi
                        .Builder()
                        .build()
                        .adapter(StytchSchemaError::class.java)
                        .fromJson(it)
                }
            } catch (t: Throwable) {
                // Can't parse anything, assume it's a network error
                null
            }
        }
    StytchLog.w("http error code: $errorCode, errorResponse: $parsedErrorResponse")
    return when (parsedErrorResponse) {
        is StytchErrorResponse ->
            StytchAPIError(
                errorType = StytchAPIErrorType.fromString(parsedErrorResponse.errorType),
                message = parsedErrorResponse.errorMessage ?: "",
                url = parsedErrorResponse.errorUrl,
                requestId = parsedErrorResponse.requestId,
                statusCode = parsedErrorResponse.statusCode,
            )
        is StytchSchemaError ->
            StytchAPISchemaError(
                message = "Request does not match expected schema: ${parsedErrorResponse.body}",
            )
        else ->
            StytchAPIUnreachableError(
                message = message ?: "Invalid or no response from server",
                exception = this,
            )
    }
}

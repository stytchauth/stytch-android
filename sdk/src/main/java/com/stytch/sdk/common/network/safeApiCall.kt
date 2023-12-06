package com.stytch.sdk.common.network // ktlint-disable filename

import com.squareup.moshi.Moshi
import com.stytch.sdk.common.StytchLog
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchAPISchemaError
import com.stytch.sdk.common.errors.StytchAPIUnreachableError
import com.stytch.sdk.common.errors.StytchError
import com.stytch.sdk.common.network.models.StytchErrorResponse
import com.stytch.sdk.common.network.models.StytchSchemaError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

internal suspend fun <T1, T : StytchDataResponse<T1>> safeApiCall(
    assertInitialized: () -> Unit,
    apiCall: suspend () -> T
): StytchResult<T1> = withContext(Dispatchers.IO) {
    assertInitialized()
    try {
        StytchResult.Success(apiCall().data)
    } catch (throwable: Throwable) {
        val stytchError = when (throwable) {
            is StytchError -> throwable
            is HttpException -> throwable.toStytchError()
            else -> {
                throwable.printStackTrace()
                StytchLog.w("Network Error")
                StytchAPIUnreachableError(
                    description = throwable.message ?: "Invalid or no response from server",
                    exception = throwable
                )
            }
        }
        StytchResult.Error(stytchError)
    }
}

private fun HttpException.toStytchError(): StytchError {
    val errorCode = code()
    val source = response()?.errorBody()?.source()
    val parsedErrorResponse = try {
        // if we can parse this out to a StytchErrorResponse, it's an API error
        source?.let {
            Moshi.Builder().build().adapter(StytchErrorResponse::class.java).fromJson(it)
        }
    } catch (t: Throwable) {
        // if we can parse this out to a StytchSchemaError, it's a schema error
        source?.let {
            Moshi.Builder().build().adapter(StytchSchemaError::class.java).fromJson(it)
        }
    } catch (t: Throwable) {
        // Can't parse anything, assume it's a network error
        null
    }
    StytchLog.w("http error code: $errorCode, errorResponse: $parsedErrorResponse")
    return when (parsedErrorResponse) {
        is StytchErrorResponse -> StytchAPIError(
            name = parsedErrorResponse.errorType,
            description = parsedErrorResponse.errorMessage ?: "",
            url = parsedErrorResponse.errorUrl,
            requestId = parsedErrorResponse.requestId
        )
        is StytchSchemaError -> StytchAPISchemaError(
            description = "Request does not match expected schema: ${parsedErrorResponse.body}"
        )
        else -> StytchAPIUnreachableError(
            description = message ?: "Invalid or no response from server",
            exception = this
        )
    }
}

package com.stytch.sdk.common.network

import com.squareup.moshi.Moshi
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchLog
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.StytchErrorResponse
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
        when (throwable) {
            is HttpException -> {
                val errorCode = throwable.code()
                val stytchErrorResponse = try {
                    throwable.response()?.errorBody()?.source()?.let {
                        Moshi.Builder().build().adapter(StytchErrorResponse::class.java).fromJson(it)
                    }
                } catch (t: Throwable) {
                    null
                }
                StytchLog.w("http error code: $errorCode, errorResponse: $stytchErrorResponse")
                StytchResult.Error(StytchExceptions.Response(stytchErrorResponse))
            }
            is StytchExceptions -> {
                StytchResult.Error(throwable)
            }
            else -> {
                throwable.printStackTrace()
                StytchLog.w("Network Error")
                StytchResult.Error(StytchExceptions.Connection(throwable))
            }
        }
    }
}

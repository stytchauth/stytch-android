package com.stytch.sdk.common.network

import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.extensions.toStytchError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun <T1, T : StytchDataResponse<T1>> safeApiCall(
    assertInitialized: () -> Unit,
    apiCall: suspend () -> T,
): StytchResult<T1> =
    withContext(Dispatchers.IO) {
        assertInitialized()
        try {
            StytchResult.Success(apiCall().data)
        } catch (throwable: Throwable) {
            StytchResult.Error(throwable.toStytchError())
        }
    }

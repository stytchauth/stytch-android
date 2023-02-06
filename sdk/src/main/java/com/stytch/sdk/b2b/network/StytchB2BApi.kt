package com.stytch.sdk.b2b.network

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.StytchAuthHeaderInterceptor
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.safeApiCall
import java.lang.RuntimeException

internal object StytchB2BApi {
    internal lateinit var publicToken: String
    private lateinit var deviceInfo: DeviceInfo

    // save reference for changing auth header
    // make sure api is configured before accessing this variable
    @Suppress("MaxLineLength")
    @VisibleForTesting
    internal val authHeaderInterceptor: StytchAuthHeaderInterceptor by lazy {
        if (!isInitialized) {
            throw StytchExceptions.Critical(
                RuntimeException(
                    "StytchApi not configured. You must call 'StytchApi.configure(...)' before using any functionality of the StytchApi." // ktlint-disable max-line-length
                )
            )
        }
        StytchAuthHeaderInterceptor(
            deviceInfo,
            publicToken,
        ) { StytchB2BClient.sessionStorage.sessionToken }
    }

    internal fun configure(publicToken: String, deviceInfo: DeviceInfo) {
        this.publicToken = publicToken
        this.deviceInfo = deviceInfo
    }

    internal val isInitialized: Boolean
        get() {
            return ::publicToken.isInitialized && ::deviceInfo.isInitialized
        }

    internal val isTestToken: Boolean
        get() {
            StytchB2BClient.assertInitialized()
            return publicToken.contains("public-token-test")
        }

    @VisibleForTesting
    internal val apiService: StytchB2BApiService by lazy {
        StytchB2BClient.assertInitialized()
        ApiService.createApiService(
            Constants.WEB_URL,
            authHeaderInterceptor,
            { StytchB2BClient.sessionStorage.revoke() },
            StytchB2BApiService::class.java
        )
    }

    internal suspend fun <T1, T : StytchDataResponse<T1>> safeB2BApiCall(
        apiCall: suspend () -> T
    ): StytchResult<T1> = safeApiCall({ StytchB2BClient.assertInitialized() }) {
        apiCall()
    }
}

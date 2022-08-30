package com.stytch.sdk.network

import android.util.Base64
import com.stytch.sdk.Constants
import com.stytch.sdk.DeviceInfo
import com.stytch.sdk.InfoHeaderModel
import com.stytch.sdk.StytchClient
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds auth headers to all requests
 * @param deviceInfo - deviceInfo of the device that use sdk
 * @param publicToken - public token for Authorization header
 */
internal class StytchAuthHeaderInterceptor(
    var deviceInfo: DeviceInfo,
    var publicToken: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authHeader = Base64.encodeToString("$publicToken:${ StytchClient.sessionStorage.sessionToken ?: publicToken}".toByteArray(), Base64.NO_WRAP)
        val infoHeader = Base64.encodeToString(InfoHeaderModel(
            sdk = InfoHeaderModel.Item(Constants.AUTH_HEADER_SDK_NAME, Constants.AUTH_HEADER_SDK_VERSION),
            app = InfoHeaderModel.Item(deviceInfo.applicationPackageName ?: "", deviceInfo.applicationVersion ?: ""),
            os = InfoHeaderModel.Item(deviceInfo.osName ?: "", deviceInfo.osVersion ?: ""),
            device = InfoHeaderModel.Item(deviceInfo.deviceName ?: "", deviceInfo.screenSize ?: "", "model", "screen_size")
        ).json.toByteArray(), Base64.NO_WRAP)

        return chain.proceed(
            chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic $authHeader")
                .addHeader("X-SDK-Client", infoHeader)
                .build()
        )
    }
}
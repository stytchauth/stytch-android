package com.stytch.sdk.common.network

import com.google.crypto.tink.subtle.Base64
import com.stytch.sdk.BuildConfig
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.DeviceInfo
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
    val getSessionToken: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authHeader = Base64.encodeToString(
            "$publicToken:${ getSessionToken() ?: publicToken}".toByteArray(),
            Base64.NO_WRAP
        )
        val infoHeader = Base64.encodeToString(
            InfoHeaderModel(
                sdk = InfoHeaderModel.Item(
                    Constants.AUTH_HEADER_SDK_NAME,
                    BuildConfig.STYTCH_SDK_VERSION
                ),
                app = InfoHeaderModel.Item(
                    deviceInfo.applicationPackageName ?: "",
                    deviceInfo.applicationVersion ?: ""
                ),
                os = InfoHeaderModel.Item(
                    deviceInfo.osName ?: "",
                    deviceInfo.osVersion ?: ""
                ),
                device = InfoHeaderModel.Item(
                    deviceInfo.deviceName ?: "",
                    deviceInfo.screenSize ?: "",
                    "model",
                    "screen_size"
                )
            ).json.toByteArray(),
            Base64.NO_WRAP
        )

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
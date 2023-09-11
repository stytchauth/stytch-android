package com.stytch.sdk.common.network

import com.stytch.sdk.common.dfp.CaptchaProvider
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.extensions.requiresCaptcha
import com.stytch.sdk.common.extensions.toNewRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

internal const val DFP_TELEMETRY_ID_KEY = "dfp_telemetry_id"
internal const val CAPTCHA_TOKEN_KEY = "captcha_token"
internal class StytchDFPInterceptor(
    private val dfpProvider: DFPProvider,
    private val captchaProvider: CaptchaProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method == "GET" || request.method == "DELETE") return chain.proceed(request)
        val response = chain.proceed(
            request.toNewRequest(
                mapOf(
                    DFP_TELEMETRY_ID_KEY to runBlocking { dfpProvider.getTelemetryId() }
                )
            )
        )
        return if (response.requiresCaptcha()) {
            response.close()
            chain.proceed(
                request.toNewRequest(
                    mapOf(
                        DFP_TELEMETRY_ID_KEY to runBlocking { dfpProvider.getTelemetryId() },
                        CAPTCHA_TOKEN_KEY to runBlocking { captchaProvider.executeRecaptcha() }
                    )
                )
            )
        } else {
            response
        }
    }
}

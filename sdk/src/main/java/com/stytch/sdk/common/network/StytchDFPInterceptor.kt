package com.stytch.sdk.common.network

import com.stytch.sdk.common.dfp.DFPProvider
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject

private const val DFP_TELEMETRY_ID_KEY = "dfp_telemetry_id"
private const val CAPTCHA_TOKEN_KEY = "captcha_token"
private const val HTTP_UNAUTHORIZED = 403

internal class StytchDFPInterceptor(
    private val dfpProvider: DFPProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method == "GET" || request.method == "DELETE") return chain.proceed(request)
        val response = chain.proceed(request.addDfpTelemetryIdToRequest())
        return if (response.requiresCaptcha()) {
            response.close()
            chain.proceed(request.addDfpTelemetryIdAndCaptchaTokenToRequest())
        } else {
            response
        }
    }

    private fun Response.requiresCaptcha(): Boolean {
        return code == HTTP_UNAUTHORIZED && message.contains("Captcha required")
    }

    private fun Request.addDfpTelemetryIdToRequest(): Request {
        val dfpTelemetryId = "dfp-telemetry-id"
        dfpProvider.getTelemetryId()
        return toNewRequest(mapOf(DFP_TELEMETRY_ID_KEY to dfpTelemetryId))
    }

    private fun Request.addDfpTelemetryIdAndCaptchaTokenToRequest(): Request {
        // TODO: fetch the telemetry ID (SDK-1127)
        val dfpTelemetryId = "dfp-telemetry-id-2"
        // TODO: fetch the captcha token (SDK-1129)
        val dfpCaptchaToken = "dfp-captcha-token"
        return toNewRequest(
            mapOf(
                DFP_TELEMETRY_ID_KEY to dfpTelemetryId,
                CAPTCHA_TOKEN_KEY to dfpCaptchaToken
            )
        )
    }

    private fun Request.toNewRequest(params: Map<String, String>): Request {
        val bodyAsString = body.asJsonString()
        val updatedBody = JSONObject(bodyAsString).apply {
            params.forEach {
                put(it.key, it.value)
            }
        }.toString()
        val newBody = updatedBody.toRequestBody(body?.contentType())
        return newBuilder().method(method, newBody).build()
    }

    private fun RequestBody?.asJsonString(): String {
        if (this == null) return "{}"
        val buffer = Buffer()
        writeTo(buffer)
        var bodyAsString: String
        buffer.use {
            bodyAsString = it.readUtf8()
        }
        if (bodyAsString.isBlank()) {
            bodyAsString = "{}"
        }
        return bodyAsString
    }
}

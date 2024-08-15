package com.stytch.sdk.common.extensions

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.junit.Test

internal class RequestExtTest {
    @Test
    fun `toNewRequest adds appropriate items to body`() {
        val originalBody = """{"a": true}""".toRequestBody("application/json".toMediaTypeOrNull())
        val originalRequest = Request.Builder().url("http://stytch.com/").post(originalBody).build()
        val newParams =
            mapOf(
                "telemetry_id" to "telemetry-id",
                "captcha_token" to "captcha-token",
            )
        val newRequest = originalRequest.toNewRequestWithParams(newParams)
        val newRequestBodyAsJson = JSONObject(newRequest.body.asJsonString())
        assert(newRequestBodyAsJson.getBoolean("a"))
        assert(newRequestBodyAsJson.getString("telemetry_id") == "telemetry-id")
        assert(newRequestBodyAsJson.getString("captcha_token") == "captcha-token")
    }
}

package com.stytch.sdk.common.extensions

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Test

internal class RequestBodyExtTest {
    @Test
    fun `null requestBody returns empty JSON string`() {
        val rb: RequestBody? = null
        assert(rb.asJsonString() == "{}")
    }

    @Test
    fun `blank requestBody returns empty JSON string`() {
        val rb = "".toRequestBody("application/json".toMediaTypeOrNull())
        assert(rb.asJsonString() == "{}")
    }

    @Test
    fun `a body is read into a string`() {
        val rb = """{"a": true}""".toRequestBody("application/json".toMediaTypeOrNull())
        assert(rb.asJsonString() == """{"a": true}""")
    }
}

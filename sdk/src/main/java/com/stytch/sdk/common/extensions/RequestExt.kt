package com.stytch.sdk.common.extensions

import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

internal fun Request.toNewRequestWithParams(params: Map<String, String>): Request {
    val bodyAsString = body.asJsonString()
    val updatedBody =
        JSONObject(bodyAsString).apply {
            params.forEach {
                put(it.key, it.value)
            }
        }.toString()
    val newBody = updatedBody.toRequestBody(body?.contentType())
    return newBuilder().method(method, newBody).build()
}

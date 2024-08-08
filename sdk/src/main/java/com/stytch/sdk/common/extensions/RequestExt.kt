package com.stytch.sdk.common.extensions

import com.stytch.sdk.common.annotations.DFPPAEnabled
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Invocation

internal fun Request.toNewRequestWithParams(params: Map<String, String>): Request {
    val bodyAsString = body.asJsonString()
    val updatedBody =
        JSONObject(bodyAsString)
            .apply {
                params.forEach {
                    put(it.key, it.value)
                }
            }.toString()
    val newBody = updatedBody.toRequestBody(body?.contentType())
    return newBuilder().method(method, newBody).build()
}

internal fun Request.isDFPPAEnabled(): Boolean =
    tag(Invocation::class.java)?.method()?.getAnnotation(DFPPAEnabled::class.java) != null

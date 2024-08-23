package com.stytch.sdk.common.extensions

import okhttp3.RequestBody
import okio.Buffer

internal fun RequestBody?.asJsonString(): String {
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

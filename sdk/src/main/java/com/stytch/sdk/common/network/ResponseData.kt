package com.stytch.sdk.common.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class BasicData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
)

@JsonClass(generateAdapter = true)
public data class StytchErrorResponse(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "error_type")
    val errorType: String,
    @Json(name = "error_message")
    val errorMessage: String?,
    @Json(name = "error_url")
    val errorUrl: String,
)

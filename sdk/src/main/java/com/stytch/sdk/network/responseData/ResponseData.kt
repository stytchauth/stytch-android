package com.stytch.sdk.network.responseData

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class BasicData(
    val request_id: String,
    val status_code: Int
)

@JsonClass(generateAdapter = true)
public data class StytchErrorResponse(
    val status_code: Int,
    val request_id: String,
    val error_type: String,
    val error_message: String?,
    val error_url: String,
)
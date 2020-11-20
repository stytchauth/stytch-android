package com.stytch.sdk.api.responses

class BasicErrorResponse(
    val status: Int,
    val message: String?,
    val error_type: String?,
    val error_message: String?,
    val error_url: String?

) {
}
package com.stytch.sdk.api.responses

class CreateUserResponse(
    val request_id: String,
    val user_id: String,
    val email_id: String,
): BasicResponse() {
}
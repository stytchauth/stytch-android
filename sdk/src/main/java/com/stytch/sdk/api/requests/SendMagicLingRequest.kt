package com.stytch.sdk.api.requests

class SendMagicLingRequest(
    val email: String,
    val magic_link_url: String,
    val expiration_minutes: Long
): BasicRequest(){
}
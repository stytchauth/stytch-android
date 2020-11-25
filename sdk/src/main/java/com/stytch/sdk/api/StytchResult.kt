package com.stytch.sdk.api

data class StytchResult(
    val userId: String,
    val requestId: String
){
    override fun toString(): String {
        return "StytchResult(userId='$userId', requestId='$requestId')"
    }
}
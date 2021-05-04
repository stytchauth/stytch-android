package com.stytch.sdk.api

public data class StytchResult(
    public val userId: String,
    public val requestId: String,
){
    override fun toString(): String {
        return "StytchResult(userId='$userId', requestId='$requestId')"
    }
}

package com.stytch.sdk

public interface Sessions {

    public data class AuthParams(
        val token: String,
        val sessionJwt:String,
        val sessionDurationInMinutes: UInt
    )

    public fun authenticate(authParams: AuthParams)

    public fun revoke()
}
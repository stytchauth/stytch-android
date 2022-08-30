package com.stytch.sdk

public interface Sessions {
    public data class AuthParams(
        val sessionToken: String?,
        val sessionJwt:String?,
        val sessionDurationMinutes: UInt? = null
    )

    public suspend fun authenticate(authParams: AuthParams): AuthResponse
    public fun authenticate(authParams: AuthParams, callback: (AuthResponse)->Unit)

    public suspend fun revoke(): BaseResponse
    public fun revoke(callback: (BaseResponse)->Unit)

    public fun updateSession(sessionToken: String?, sessionJwt: String?)
}
package com.stytch.sdk

public interface Sessions {
    public data class AuthParams(
        val sessionToken: String?,
        val sessionJwt:String?,
        val sessionDurationMinutes: UInt
    )

    public suspend fun authenticate(authParams: AuthParams): BaseResponse
    public fun authenticate(authParams: AuthParams, callback: (BaseResponse)->Unit)

    public suspend fun revoke(): BaseResponse
    public fun revoke(callback: (BaseResponse)->Unit)

    public fun updateSession(sessionToken: String?, sessionJwt: String?)
}
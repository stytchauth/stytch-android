package com.stytch.sdk

public interface Sessions {

    /**
     * @throws StytchExceptions.Critical if failed to decrypt data
     */
    public val sessionToken: String?

    /**
     * @throws StytchExceptions.Critical if failed to decrypt data
     */
    public val sessionJwt: String?

    /**
     * Data class used for wrapping parameters used with Sessions authentication
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthParams(
        val sessionDurationMinutes: UInt? = null,
    )

    /**
     * Wraps the sessions authenticate API endpoint which creates a session using the provided duration
     * @param authParams required to authenticate
     * @return AuthResponse response from backend
     */
    public suspend fun authenticate(authParams: AuthParams): AuthResponse

    /**
     * Wraps the sessions authenticate API endpoint which creates a session using the provided duration
     * @param authParams required to authenticate
     * @param callback calls callback with AuthResponse response from backend
     */
    public fun authenticate(authParams: AuthParams, callback: (AuthResponse) -> Unit)

    /**
     * Wraps the sessions revoke API endpoint which revokes a session
     * @return BaseResponse response from backend
     */
    public suspend fun revoke(): BaseResponse

    /**
     * Wraps the sessions revoke API endpoint which revokes a session
     * @param callback calls callback with BaseResponse response from backend
     */
    public fun revoke(callback: (BaseResponse) -> Unit)

    /**
     * Updates the current session with a sessionToken and/or sessionJwt
     * @param sessionToken
     * @param sessionJwt
     */
    public fun updateSession(sessionToken: String?, sessionJwt: String?)
}

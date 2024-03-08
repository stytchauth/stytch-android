package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.b2b.network.models.B2BSessionData
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.errors.StytchFailedToDecryptDataError

/**
 * The B2BSessions interface provides methods for authenticating, updating, or revoking sessions, and properties to
 * retrieve the existing session token (opaque or JWT).
 */
public interface B2BSessions {

    /**
     * @throws StytchFailedToDecryptDataError if failed to decrypt data
     */
    public val sessionToken: String?

    /**
     * @throws StytchFailedToDecryptDataError if failed to decrypt data
     */
    public val sessionJwt: String?

    /**
     * Data class used for wrapping parameters used with Sessions authentication
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthParams(
        val sessionDurationMinutes: UInt? = null,
    )

    /**
     * Data class used for wrapping parameters used with Sessions revocation
     * @property forceClear if true, we will clear the local session regardless of any network errors
     */
    public data class RevokeParams(
        val forceClear: Boolean = false,
    )

    /**
     * Authenticates a Session and updates its lifetime by the specified session_duration_minutes.
     * If the session_duration_minutes is not specified, a Session will not be extended
     * @param authParams required to authenticate
     * @return [AuthResponse]
     */
    public suspend fun authenticate(authParams: AuthParams): AuthResponse

    /**
     * Authenticates a Session and updates its lifetime by the specified session_duration_minutes.
     * If the session_duration_minutes is not specified, a Session will not be extended
     * @param authParams required to authenticate
     * @param callback a callback that receives an [AuthResponse]
     */
    public fun authenticate(authParams: AuthParams, callback: (AuthResponse) -> Unit)

    /**
     * Revoke a Session and immediately invalidate all its tokens.
     * @param params required for revoking a session
     * @return [BaseResponse]
     */
    public suspend fun revoke(params: RevokeParams = RevokeParams()): BaseResponse

    /**
     * Revoke a Session and immediately invalidate all its tokens.
     * @param params required for revoking a session
     * @param callback a callback that receives a [BaseResponse]
     */
    public fun revoke(params: RevokeParams = RevokeParams(), callback: (BaseResponse) -> Unit)

    /**
     * Updates the current session with a sessionToken and/or sessionJwt
     * @param sessionToken
     * @param sessionJwt
     */
    public fun updateSession(sessionToken: String?, sessionJwt: String?)

    /**
     * Get session from memory without network call
     * @return locally stored [B2BSessionData]
     */
    public fun getSync(): B2BSessionData?
}

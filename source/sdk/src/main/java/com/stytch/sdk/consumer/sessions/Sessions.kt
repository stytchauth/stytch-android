package com.stytch.sdk.consumer.sessions

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.errors.StytchFailedToDecryptDataError
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.network.models.SessionData
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.CompletableFuture

/**
 * The Sessions interface provides methods for authenticating, updating, or revoking sessions, and properties to
 * retrieve the existing session token (opaque or JWT).
 */
public interface Sessions {
    /**
     * Exposes a flow of session data
     */
    public suspend fun onChange(): StateFlow<StytchSession>

    /**
     * Assign a callback that will be called when the session data changes
     */

    public fun onChange(callback: (StytchSession) -> Unit)

    /**
     * @throws StytchFailedToDecryptDataError if failed to decrypt data
     */
    public val sessionToken: String?

    /**
     * @throws StytchFailedToDecryptDataError if failed to decrypt data
     */
    public val sessionJwt: String?

    /**
     * Get the locally persisted session
     */
    public fun getSync(): SessionData?

    /**
     * Data class used for wrapping parameters used with Sessions authentication
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthParams
        @JvmOverloads
        constructor(
            val sessionDurationMinutes: Int? = null,
        )

    /**
     * Data class used for wrapping parameters used with Sessions revocation
     * @property forceClear if true, we will clear the local session regardless of any network errors
     */
    public data class RevokeParams
        @JvmOverloads
        constructor(
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
    public fun authenticate(
        authParams: AuthParams,
        callback: (AuthResponse) -> Unit,
    )

    /**
     * Authenticates a Session and updates its lifetime by the specified session_duration_minutes.
     * If the session_duration_minutes is not specified, a Session will not be extended
     * @param authParams required to authenticate
     * @return [AuthResponse]
     */
    public fun authenticateCompletable(authParams: AuthParams): CompletableFuture<AuthResponse>

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
    public fun revoke(
        params: RevokeParams = RevokeParams(),
        callback: (BaseResponse) -> Unit,
    )

    /**
     * Revoke a Session and immediately invalidate all its tokens.
     * @param params required for revoking a session
     * @return [BaseResponse]
     */
    public fun revokeCompletable(params: RevokeParams = RevokeParams()): CompletableFuture<BaseResponse>

    /**
     * Updates the current session with a sessionToken and sessionJwt
     * @param sessionToken
     * @param sessionJwt
     */
    public fun updateSession(
        sessionToken: String,
        sessionJwt: String,
    )
}

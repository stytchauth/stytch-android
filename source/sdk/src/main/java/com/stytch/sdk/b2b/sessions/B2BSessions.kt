package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.b2b.SessionExchangeResponse
import com.stytch.sdk.b2b.SessionsAuthenticateResponse
import com.stytch.sdk.b2b.network.models.B2BSessionData
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.errors.StytchFailedToDecryptDataError
import com.stytch.sdk.common.network.models.Locale
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.CompletableFuture

/**
 * The B2BSessions interface provides methods for authenticating, updating, or revoking sessions, and properties to
 * retrieve the existing session token (opaque or JWT).
 */
public interface B2BSessions {
    /**
     * Exposes a flow of session data
     */
    public val onChange: StateFlow<StytchObjectInfo<B2BSessionData>>

    /**
     * Assign a callback that will be called when the session data changes
     */

    public fun onChange(callback: (StytchObjectInfo<B2BSessionData>) -> Unit)

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
    @JacocoExcludeGenerated
    public data class AuthParams
        @JvmOverloads
        constructor(
            val sessionDurationMinutes: Int? = null,
        )

    /**
     * Data class used for wrapping parameters used with Sessions revocation
     * @property forceClear if true, we will clear the local session regardless of any network errors
     */
    @JacocoExcludeGenerated
    public data class RevokeParams
        @JvmOverloads
        constructor(
            val forceClear: Boolean = false,
        )

    /**
     * Authenticates a Session and updates its lifetime by the specified session_duration_minutes.
     * If the session_duration_minutes is not specified, a Session will not be extended
     * @param authParams required to authenticate
     * @return [SessionsAuthenticateResponse]
     */
    public suspend fun authenticate(authParams: AuthParams): SessionsAuthenticateResponse

    /**
     * Authenticates a Session and updates its lifetime by the specified session_duration_minutes.
     * If the session_duration_minutes is not specified, a Session will not be extended
     * @param authParams required to authenticate
     * @param callback a callback that receives an [SessionsAuthenticateResponse]
     */
    public fun authenticate(
        authParams: AuthParams,
        callback: (SessionsAuthenticateResponse) -> Unit,
    )

    /**
     * Authenticates a Session and updates its lifetime by the specified session_duration_minutes.
     * If the session_duration_minutes is not specified, a Session will not be extended
     * @param authParams required to authenticate
     * @return [SessionsAuthenticateResponse]
     */
    public fun authenticateCompletable(authParams: AuthParams): CompletableFuture<SessionsAuthenticateResponse>

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

    /**
     * Get session from memory without network call
     * @return locally stored [B2BSessionData]
     */
    public fun getSync(): B2BSessionData?

    /**
     * Data class used for wrapping parameters used with Sessions exchange
     * @property organizationId The ID of the organization that the new session should belong to.
     * @property locale The locale will be used if an OTP code is sent to the member's phone number as part of a
     * secondary authentication requirement.
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    @JacocoExcludeGenerated
    public data class ExchangeParameters
        @JvmOverloads
        constructor(
            val organizationId: String,
            val sessionDurationMinutes: Int,
            val locale: Locale? = null,
        )

    /**
     * Exchanges an existing session for one in a different organization
     * @param parameters required for exchanging a session between organizations
     * @return [SessionExchangeResponse]
     */
    public suspend fun exchange(parameters: ExchangeParameters): SessionExchangeResponse

    /**
     * Exchanges an existing session for one in a different organization
     * @param parameters required for exchanging a session between organizations
     * @param callback a callback that receives a [SessionExchangeResponse]
     * @return [SessionExchangeResponse]
     */
    public fun exchange(
        parameters: ExchangeParameters,
        callback: (SessionExchangeResponse) -> Unit,
    )

    /**
     * Exchanges an existing session for one in a different organization
     * @param parameters required for exchanging a session between organizations
     * @return [SessionExchangeResponse]
     */
    public fun exchangeCompletable(parameters: ExchangeParameters): CompletableFuture<SessionExchangeResponse>
}

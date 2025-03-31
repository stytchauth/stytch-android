package com.stytch.sdk.b2b.recoveryCodes

import com.stytch.sdk.b2b.RecoveryCodesGetResponse
import com.stytch.sdk.b2b.RecoveryCodesRecoverResponse
import com.stytch.sdk.b2b.RecoveryCodesRotateResponse
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import java.util.concurrent.CompletableFuture

/**
 * The RecoveryCodes interface provides methods for getting, rotating, and using recovery codes for a member
 */
public interface RecoveryCodes {
    /**
     * Get the recovery codes for an authenticated member
     * @return [RecoveryCodesGetResponse]
     */
    public suspend fun get(): RecoveryCodesGetResponse

    /**
     * Get the recovery codes for an authenticated member
     * @param callback a callback that receives a [RecoveryCodesGetResponse]
     */
    public fun get(callback: (RecoveryCodesGetResponse) -> Unit)

    /**
     * Get the recovery codes for an authenticated member
     * @return [RecoveryCodesGetResponse]
     */
    public fun getCompletable(): CompletableFuture<RecoveryCodesGetResponse>

    /**
     * Rotate the recovery codes for an authenticated member
     * @return [RecoveryCodesRotateResponse]
     */
    public suspend fun rotate(): RecoveryCodesRotateResponse

    /**
     * Rotate the recovery codes for an authenticated member
     * @param callback a callback that receives a [RecoveryCodesRotateResponse]
     */
    public fun rotate(callback: (RecoveryCodesRotateResponse) -> Unit)

    /**
     * Rotate the recovery codes for an authenticated member
     * @return [RecoveryCodesRotateResponse]
     */
    public fun rotateCompletable(): CompletableFuture<RecoveryCodesRotateResponse>

    /**
     * A data class wrapping the parameters needed to consume a recovery code
     * @property organizationId The ID of the organization the member belongs
     * @property memberId The ID of the member to create the TOTP for
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     * @property recoveryCode The recovery code to authenticate.
     */
    @JacocoExcludeGenerated
    public data class RecoverParameters(
        val organizationId: String,
        val memberId: String,
        val sessionDurationMinutes: Int,
        val recoveryCode: String,
    )

    /**
     * Consume a recovery code for a member
     * @param parameters the parameters needed to consume a recovery code
     * @return [RecoveryCodesRecoverResponse]
     */
    public suspend fun recover(parameters: RecoverParameters): RecoveryCodesRecoverResponse

    /**
     * Consume a recovery code for a member
     * @param parameters the parameters needed to consume a recovery code
     * @param callback a callback that receives a [RecoveryCodesRecoverResponse]
     */
    public fun recover(
        parameters: RecoverParameters,
        callback: (RecoveryCodesRecoverResponse) -> Unit,
    )

    /**
     * Consume a recovery code for a member
     * @param parameters the parameters needed to consume a recovery code
     * @return [RecoveryCodesRecoverResponse]
     */
    public fun recoverCompletable(parameters: RecoverParameters): CompletableFuture<RecoveryCodesRecoverResponse>
}

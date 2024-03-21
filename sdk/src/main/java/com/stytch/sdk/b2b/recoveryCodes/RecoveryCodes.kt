package com.stytch.sdk.b2b.recoveryCodes

import com.stytch.sdk.b2b.RecoveryCodesGetResponse
import com.stytch.sdk.b2b.RecoveryCodesRecoverResponse
import com.stytch.sdk.b2b.RecoveryCodesRotateResponse

/**
 * TODO
 */
public interface RecoveryCodes {
    /**
     * TODO
     */
    public suspend fun get(): RecoveryCodesGetResponse

    /**
     * TODO
     */
    public fun get(callback: (RecoveryCodesGetResponse) -> Unit)

    /**
     * TODO
     */
    public suspend fun rotate(): RecoveryCodesRotateResponse

    /**
     * TODO
     */
    public fun rotate(callback: (RecoveryCodesRotateResponse) -> Unit)

    /**
     * TODO
     */
    public data class RecoverParameters(
        val organizationId: String,
        val memberId: String,
        val sessionDurationMinutes: Int,
        val recoveryCode: String,
    )

    /**
     * TODO
     */
    public suspend fun recover(parameters: RecoverParameters): RecoveryCodesRecoverResponse

    /**
     * TODO
     */
    public fun recover(
        parameters: RecoverParameters,
        callback: (RecoveryCodesRecoverResponse) -> Unit,
    )
}

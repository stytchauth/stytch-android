package com.stytch.sdk.consumer.totp

import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.consumer.TOTPAuthenticateResponse
import com.stytch.sdk.consumer.TOTPCreateResponse
import com.stytch.sdk.consumer.TOTPRecoverResponse
import com.stytch.sdk.consumer.TOTPRecoveryCodesResponse
import java.util.concurrent.CompletableFuture

/**
 * The TOTP interface provides methods for creating and authenticating TOTP codes; retrieving recovery codes; and
 * consuming a recovery code
 */
public interface TOTP {
    /**
     * A data class wrapping the parameters used in a TOTP create request
     * @property expirationMinutes The expiration for the TOTP instance. If the newly created TOTP is not authenticated
     * within this time frame the TOTP will be unusable. Defaults to 60 (1 hour) with a minimum of 5 and a maximum of
     * 1440.
     */
    public data class CreateParameters(
        val expirationMinutes: Int,
    )

    /**
     * A data class wrapping the parameters use in a TOTP authenticate request
     * @property totpCode The TOTP code to authenticate. The TOTP code should consist of 6 digits.
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthenticateParameters
        @JvmOverloads
        constructor(
            val totpCode: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
        )

    /**
     * A data class wrapping the parameters use in a TOTP recover request
     * @property recoveryCode The recovery code to authenticate.
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class RecoverParameters
        @JvmOverloads
        constructor(
            val recoveryCode: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
        )

    /**
     *  Call this method to create a new TOTP instance for a user. The user can use the authenticator application of
     *  their choice to scan the QR code or enter the secret.
     *  @param parameters the parameters required to create a TOTP
     *  @return [TOTPCreateResponse]
     */
    public suspend fun create(parameters: CreateParameters): TOTPCreateResponse

    /**
     *  Call this method to create a new TOTP instance for a user. The user can use the authenticator application of
     *  their choice to scan the QR code or enter the secret.
     *  @param parameters the parameters required to create a TOTP
     *  @param callback a callback that receives a [TOTPCreateResponse]
     */
    public fun create(
        parameters: CreateParameters,
        callback: (TOTPCreateResponse) -> Unit,
    )

    /**
     *  Call this method to create a new TOTP instance for a user. The user can use the authenticator application of
     *  their choice to scan the QR code or enter the secret.
     *  @param parameters the parameters required to create a TOTP
     *  @return [TOTPCreateResponse]
     */
    public fun createCompletable(parameters: CreateParameters): CompletableFuture<TOTPCreateResponse>

    /**
     * Call this method to authenticate a TOTP code entered by a user. If this method succeeds, the user will be logged
     * in and granted an active session
     * @param parameters the parameters required to authenticate a TOTP
     * @return [TOTPAuthenticateResponse]
     */
    public suspend fun authenticate(parameters: AuthenticateParameters): TOTPAuthenticateResponse

    /**
     * Call this method to authenticate a TOTP code entered by a user. If this method succeeds, the user will be logged
     * in and granted an active session
     * @param parameters the parameters required to authenticate a TOTP
     * @param callback a callback that receives a [TOTPAuthenticateResponse]
     */
    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (TOTPAuthenticateResponse) -> Unit,
    )

    /**
     * Call this method to authenticate a TOTP code entered by a user. If this method succeeds, the user will be logged
     * in and granted an active session
     * @param parameters the parameters required to authenticate a TOTP
     * @return [TOTPAuthenticateResponse]
     */
    public fun authenticateCompletable(parameters: AuthenticateParameters): CompletableFuture<TOTPAuthenticateResponse>

    /**
     * Call this method to retrieve the recovery codes for a TOTP instance tied to a user.
     * @return [TOTPRecoveryCodesResponse]
     */
    public suspend fun recoveryCodes(): TOTPRecoveryCodesResponse

    /**
     * Call this method to retrieve the recovery codes for a TOTP instance tied to a user.
     * @param callback a callback that receives a [TOTPRecoveryCodesResponse]
     */
    public fun recoveryCodes(callback: (TOTPRecoveryCodesResponse) -> Unit)

    /**
     * Call this method to retrieve the recovery codes for a TOTP instance tied to a user.
     * @return [TOTPRecoveryCodesResponse]
     */
    public fun recoveryCodesCompletable(): CompletableFuture<TOTPRecoveryCodesResponse>

    /**
     * Call this method to authenticate a recovery code for a TOTP instance. If this method succeeds, the user will be
     * logged in and granted an active session
     * @param parameters the parameters required to authenticate a recovery code for a TOTP instance
     * @return [TOTPRecoverResponse]
     */
    public suspend fun recover(parameters: RecoverParameters): TOTPRecoverResponse

    /**
     * Call this method to authenticate a recovery code for a TOTP instance. If this method succeeds, the user will be
     * logged in and granted an active session
     * @param parameters the parameters required to authenticate a recovery code for a TOTP instance
     * @param callback a callback that receives a [TOTPRecoverResponse]
     */
    public fun recover(
        parameters: RecoverParameters,
        callback: (TOTPRecoverResponse) -> Unit,
    )

    /**
     * Call this method to authenticate a recovery code for a TOTP instance. If this method succeeds, the user will be
     * logged in and granted an active session
     * @param parameters the parameters required to authenticate a recovery code for a TOTP instance
     * @return [TOTPRecoverResponse]
     */
    public fun recoverCompletable(parameters: RecoverParameters): CompletableFuture<TOTPRecoverResponse>
}

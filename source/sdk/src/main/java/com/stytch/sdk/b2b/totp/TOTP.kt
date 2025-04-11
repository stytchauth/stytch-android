package com.stytch.sdk.b2b.totp

import com.stytch.sdk.b2b.TOTPAuthenticateResponse
import com.stytch.sdk.b2b.TOTPCreateResponse
import com.stytch.sdk.b2b.network.models.SetMFAEnrollment
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import java.util.concurrent.CompletableFuture

/**
 * The TOTP interface provides methods for creating and authenticating TOTPs for a member
 */

public interface TOTP {
    /**
     * A data class wrapping the parameters needed to create a TOTP
     * @property organizationId The ID of the organization the member belongs to
     * @property memberId The ID of the member to create the TOTP for
     * @property expirationMinutes The expiration for the TOTP instance. If the newly created TOTP is not authenticated
     * within this time frame the TOTP will be unusable. Defaults to 60 (1 hour) with a minimum of 5 and a maximum of
     * 1440.
     */
    @JacocoExcludeGenerated
    public data class CreateParameters
        @JvmOverloads
        constructor(
            val organizationId: String,
            val memberId: String,
            val expirationMinutes: Int? = null,
        )

    /**
     * Create a TOTP for a member
     * @param parameters required to create a TOTP
     * @return [TOTPCreateResponse]
     */
    public suspend fun create(parameters: CreateParameters): TOTPCreateResponse

    /**
     * Create a TOTP for a member
     * @param parameters required to create a TOTP
     * @param callback a callback that receives a [TOTPCreateResponse]
     */
    public fun create(
        parameters: CreateParameters,
        callback: (TOTPCreateResponse) -> Unit,
    )

    /**
     * Create a TOTP for a member
     * @param parameters required to create a TOTP
     * @return [TOTPCreateResponse]
     */
    public fun createCompletable(parameters: CreateParameters): CompletableFuture<TOTPCreateResponse>

    /**
     * A data class wrapping the parameters needed to authenticate a TOTP
     * @property organizationId The ID of the organization the member belongs to
     * @property memberId The ID of the member to create the TOTP for
     * @property code The TOTP code to authenticate
     * @property setMFAEnrollment If set to 'enroll', enrolls the member in MFA by setting the "mfa_enrolled"
     * boolean to true. If set to 'unenroll', unenrolls the member in MFA by setting the "mfa_enrolled" boolean to
     * false. If not set, does not affect the member's MFA enrollment.
     * @property setDefaultMFAMethod If set to true, sets TOTP as the member's default MFA method.
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    @JacocoExcludeGenerated
    public data class AuthenticateParameters
        @JvmOverloads
        constructor(
            val organizationId: String,
            val memberId: String,
            val code: String,
            val setMFAEnrollment: SetMFAEnrollment? = null,
            val setDefaultMFAMethod: Boolean? = null,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
        )

    /**
     * Authenticate a TOTP for a member
     * @param parameters required to authenticate a TOTP
     * @return [TOTPAuthenticateResponse]
     */
    public suspend fun authenticate(parameters: AuthenticateParameters): TOTPAuthenticateResponse

    /**
     * Create a TOTP for a member
     * @param parameters required to authenticate a TOTP
     * @param callback a callback that receives a [TOTPAuthenticateResponse]
     */
    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (TOTPAuthenticateResponse) -> Unit,
    )

    /**
     * Authenticate a TOTP for a member
     * @param parameters required to authenticate a TOTP
     * @return [TOTPAuthenticateResponse]
     */
    public fun authenticateCompletable(parameters: AuthenticateParameters): CompletableFuture<TOTPAuthenticateResponse>
}

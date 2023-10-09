package com.stytch.sdk.consumer.passkeys

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.consumer.AuthResponse

/**
 * The Passkeys interface provides methods for detecting Passkeys support, registering, and authenticating with
 * Passkeys.
 */
public interface Passkeys {

    /**
     * Data class used for wrapping parameters used with Passkeys registration
     * @property userId the user identifier of the Passkey registration
     * @property domain the domain of the Passkey registration
     * @property userAgent the user agent of the Passkey registration
     */
    public data class RegisterParameters(
        val userId: String,
        val domain: String,
        val userAgent: String? = null,
    )

    /**
     * Data class used for wrapping parameters used with Passkeys authentication
     * @property domain the domain of the Passkey registration
     */
    public data class AuthenticateParameters(
        val domain: String,
    )

    /**
     * Indicates if Passkeys is supported on the device.
     */
    public val isSupported: Boolean

    /**
     * Creates a new Passkey registration.
     * @param parameters required to register a Passkey
     * @return [BaseResponse]
     */
    public suspend fun register(parameters: RegisterParameters): BaseResponse

    /**
     * Creates a new Passkey registration.
     * @param parameters required to register a Passkey
     * @param callback a callback that receives a [BaseResponse]
     */
    public fun register(
        parameters: RegisterParameters,
        callback: (response: BaseResponse) -> Unit
    )

    /**
     * Authenticates a Passkey registration.
     * @param parameters required to authenticate a Passkey registration
     * @return [AuthResponse]
     */
    public suspend fun authenticate(parameters: AuthenticateParameters): AuthResponse

    /**
     * Authenticates a Passkey registration.
     * @param parameters required to authenticate a Passkey registration
     * @param callback a callback that receives a [AuthResponse]
     */
    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (response: AuthResponse) -> Unit,
    )
}

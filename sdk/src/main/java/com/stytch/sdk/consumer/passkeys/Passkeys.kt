package com.stytch.sdk.consumer.passkeys

import android.content.Context
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.WebAuthnRegisterResponse

/**
 * The Passkeys interface provides methods for detecting Passkeys support, registering, and authenticating with
 * Passkeys.
 */
public interface Passkeys {

    /**
     * Data class used for wrapping parameters used with Passkeys registration
     * @property context an activity context for launching the native Passkeys UI
     * @property domain the domain of the Passkey registration. If not provided, the package name will be used
     */
    public data class RegisterParameters(
        val context: Context,
        val domain: String? = null,
    )

    /**
     * Data class used for wrapping parameters used with Passkeys authentication
     * @property context an activity context for launching the native Passkeys UI
     * @property domain the domain of the Passkey registration. If not provided, the package name will be used
     */
    public data class AuthenticateParameters(
        val context: Context,
        val domain: String? = null,
    )

    /**
     * Indicates if Passkeys is supported on the device.
     */
    public val isSupported: Boolean

    /**
     * Creates a new Passkey registration.
     * @param parameters required to register a Passkey
     * @return [WebAuthnRegisterResponse]
     */
    public suspend fun register(parameters: RegisterParameters): WebAuthnRegisterResponse

    /**
     * Creates a new Passkey registration.
     * @param parameters required to register a Passkey
     * @param callback a callback that receives a [WebAuthnRegisterResponse]
     */
    public fun register(
        parameters: RegisterParameters,
        callback: (response: WebAuthnRegisterResponse) -> Unit
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

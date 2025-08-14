package com.stytch.sdk.consumer.passkeys

import android.app.Activity
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.consumer.WebAuthnAuthenticateResponse
import com.stytch.sdk.consumer.WebAuthnRegisterResponse
import com.stytch.sdk.consumer.WebAuthnUpdateResponse
import java.util.concurrent.CompletableFuture

/**
 * The Passkeys interface provides methods for detecting Passkeys support, registering, and authenticating with
 * Passkeys.
 */

public interface Passkeys {
    /**
     * Data class used for wrapping parameters used with Passkeys registration
     * @property activity an activity context for launching the native Passkeys UI
     * @property domain the domain of the Passkey registration. Do not include the protocol
     */
    @JacocoExcludeGenerated
    public data class RegisterParameters(
        val activity: Activity,
        val domain: String,
    )

    /**
     * Data class used for wrapping parameters used with Passkeys authentication
     * @property activity an activity context for launching the native Passkeys UI
     * @property domain the domain of the Passkey registration. Do not include the protocol
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    @JacocoExcludeGenerated
    public data class AuthenticateParameters
        @JvmOverloads
        constructor(
            val activity: Activity,
            val domain: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
        )

    /**
     * Data class used for wrapping parameters used with Passkeys updates
     * @property id the id of a Passkey registration
     * @property name the name for a Passkey
     */
    @JacocoExcludeGenerated
    public data class UpdateParameters(
        val id: String,
        val name: String,
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
        callback: (response: WebAuthnRegisterResponse) -> Unit,
    )

    /**
     * Creates a new Passkey registration.
     * @param parameters required to register a Passkey
     * @return [WebAuthnRegisterResponse]
     */
    public fun registerCompletable(parameters: RegisterParameters): CompletableFuture<WebAuthnRegisterResponse>

    /**
     * Authenticates a Passkey registration.
     * @param parameters required to authenticate a Passkey registration
     * @return [WebAuthnAuthenticateResponse]
     */
    public suspend fun authenticate(parameters: AuthenticateParameters): WebAuthnAuthenticateResponse

    /**
     * Authenticates a Passkey registration.
     * @param parameters required to authenticate a Passkey registration
     * @param callback a callback that receives a [WebAuthnAuthenticateResponse]
     */
    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (response: WebAuthnAuthenticateResponse) -> Unit,
    )

    /**
     * Authenticates a Passkey registration.
     * @param parameters required to authenticate a Passkey registration
     * @return [WebAuthnAuthenticateResponse]
     */
    public fun authenticateCompletable(
        parameters: AuthenticateParameters,
    ): CompletableFuture<WebAuthnAuthenticateResponse>

    /**
     * Updates a Passkey registration.
     * @param parameters required to update a Passkey registration
     * @return [WebAuthnUpdateResponse]
     */
    public suspend fun update(parameters: UpdateParameters): WebAuthnUpdateResponse

    /**
     * Updates a Passkey registration.
     * @param parameters required to update a Passkey registration
     * @param callback a callback that receives a [WebAuthnUpdateResponse]
     */
    public fun update(
        parameters: UpdateParameters,
        callback: (response: WebAuthnUpdateResponse) -> Unit,
    )

    /**
     * Updates a Passkey registration.
     * @param parameters required to update a Passkey registration
     * @return [WebAuthnUpdateResponse]
     */
    public fun updateCompletable(parameters: UpdateParameters): CompletableFuture<WebAuthnUpdateResponse>
}

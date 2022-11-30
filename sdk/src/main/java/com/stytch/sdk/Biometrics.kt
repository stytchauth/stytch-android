package com.stytch.sdk

import android.content.Context

/**
 * Biometrics interface that encompasses registration and authentication functions
 */
public interface Biometrics {
    /**
     * Data class used for wrapping parameters used with Biometrics registration and authenticate start flow
     * @param context is the Android context
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class StartParameters(
        val context: Context,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        val allowFallbackToCleartext: Boolean = false,
    )

    /**
     * Indicates if there is an existing biometric registration on device.
     */
    public val registrationAvailable: Boolean

    /**
     * Clears existing biometric registrations stored on device. Useful when removing a user from a given device.
     */
    public fun removeRegistration(): Boolean

    /**
     * Indicates if the device is using the Android KeyStore
     */
    public fun isUsingKeystore(context: Context): Boolean

    /**
     * When a valid/active session exists, this method will add a biometric registration for the current user.
     * The user will later be able to start a new session with biometrics or use biometrics as an additional
     * authentication factor.
     * @param parameters required to register a biometrics key
     */
    public suspend fun register(
        parameters: StartParameters
    ): BiometricsAuthResponse

    /**
     * When a valid/active session exists, this method will add a biometric registration for the current user.
     * The user will later be able to start a new session with biometrics or use biometrics as an additional
     * authentication factor.
     * @param parameters required to register a biometrics key
     * @param callback calls callback with BiometricsRegisterResponse response from backend
     */
    public fun register(
        parameters: StartParameters,
        callback: (response: BiometricsAuthResponse) -> Unit
    )

    /**
     * If a valid biometric registration exists, this method confirms the current device owner via the device's built-in
     * biometric reader and returns an updated session object by either starting a new session or adding the biometric
     * factor to an existing session.
     * @param parameters required to register a biometrics key
     */
    public suspend fun authenticate(
        parameters: StartParameters
    ): BiometricsAuthResponse

    /**
     * If a valid biometric registration exists, this method confirms the current device owner via the device's built-in
     * biometric reader and returns an updated session object by either starting a new session or adding the biometric
     * factor to an existing session.
     * @param parameters required to register a biometrics key
     * @param callback calls callback with BiometricsRegisterResponse response from backend
     */
    public fun authenticate(
        parameters: StartParameters,
        callback: (response: BiometricsAuthResponse) -> Unit
    )
}

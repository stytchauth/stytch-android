package com.stytch.sdk

import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.FragmentActivity

/**
 * Biometrics interface that encompasses registration and authentication functions
 */
public interface Biometrics {
    /**
     * Data class used for wrapping parameters used with Biometrics registration flow
     * @param context is the calling FragmentActivity
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     * @param allowFallbackToCleartext opts-in to potentially unsafe behavior
     * @param promptInfo is an optional biometric prompt configuration. If one is not provided a default will be created
     */
    public data class RegisterParameters(
        val context: FragmentActivity,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        val allowFallbackToCleartext: Boolean = false,
        val promptInfo: PromptInfo? = null,
        val allowDeviceCredentials: Boolean = false,
    )

    /**
     * Data class used for wrapping parameters used with Biometrics authentication flow
     * @param context is the calling FragmentActivity
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     * @param promptInfo is an optional biometric prompt configuration. If one is not provided a default will be created
     */
    public data class AuthenticateParameters(
        val context: FragmentActivity,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        val promptInfo: PromptInfo? = null,
    )

    /**
     * Indicates if there is an existing biometric registration on device.
     */
    public fun isRegistrationAvailable(context: FragmentActivity): Boolean

    /**
     * Indicates if the biometric sensor is available, and provides a reasoning if not
     */
    public fun areBiometricsAvailable(
        context: FragmentActivity,
        allowDeviceCredentials: Boolean = false,
    ): BiometricAvailability

    /**
     * Clears existing biometric registrations stored on device. Useful when removing a user from a given device.
     */
    public suspend fun removeRegistration(): Boolean

    /**
     * Clears existing biometric registrations stored on device. Useful when removing a user from a given device.
     */
    public fun removeRegistration(callback: (Boolean) -> Unit)

    /**
     * Indicates if the device is using the Android KeyStore
     */
    public fun isUsingKeystore(): Boolean

    /**
     * When a valid/active session exists, this method will add a biometric registration for the current user.
     * The user will later be able to start a new session with biometrics or use biometrics as an additional
     * authentication factor.
     * @param parameters required to register a biometrics key
     */
    public suspend fun register(parameters: RegisterParameters): BiometricsAuthResponse

    /**
     * When a valid/active session exists, this method will add a biometric registration for the current user.
     * The user will later be able to start a new session with biometrics or use biometrics as an additional
     * authentication factor.
     * @param parameters required to register a biometrics key
     * @param callback calls callback with BiometricsAuthResponse response from backend
     */
    public fun register(
        parameters: RegisterParameters,
        callback: (response: BiometricsAuthResponse) -> Unit,
    )

    /**
     * If a valid biometric registration exists, this method confirms the current device owner via the device's built-in
     * biometric reader and returns an updated session object by either starting a new session or adding the biometric
     * factor to an existing session.
     * @param parameters required to authenticate a biometrics key
     */
    public suspend fun authenticate(parameters: AuthenticateParameters): BiometricsAuthResponse

    /**
     * If a valid biometric registration exists, this method confirms the current device owner via the device's built-in
     * biometric reader and returns an updated session object by either starting a new session or adding the biometric
     * factor to an existing session.
     * @param parameters required to authenticate a biometrics key
     * @param callback calls callback with BiometricsAuthResponse response from backend
     */
    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (response: BiometricsAuthResponse) -> Unit,
    )
}

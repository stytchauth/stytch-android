package com.stytch.sdk

/**
 * Biometrics interface that encompasses registration and authentication functions
 */
public interface Biometrics {
    /**
     * Data class used for wrapping parameters used with Biometrics registration and authenticate start flow
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class StartParameters(
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Data class used for wrapping parameters used with Biometrics registration start flow
     * @param publicKey Base64 encoded public key retrieved from SharedPreferences
     */
    public data class RegisterStartParameters(
        val publicKey: String,
    )

    /**
     * Data class used for wrapping parameters used with Biometrics registration flow
     * @param signature Base64 encoded signed challenge
     * @param biometricRegistrationId an identifier returned from the API to identify this request
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class RegisterParameters(
        val signature: String,
        val biometricRegistrationId: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Data class used for wrapping parameters used with Biometrics authentication start flow
     * @param publicKey Base64 encoded public key retrieved from SharedPreferences
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthenticateStartParameters(
        val publicKey: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Data class used for wrapping parameters used with Biometrics authentication flow
     * @param signature Base64 encoded signed challenge
     * @param biometricRegistrationId an identifier returned from the API to identify this request
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthenticateParameters(
        val signature: String,
        val biometricRegistrationId: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Indicates if there is an existing biometric registration on device.
     */
    public val registrationAvailable: Boolean

    /**
     * Clears existing biometric registrations stored on device. Useful when removing a user from a given device.
     */
    public fun removeRegistration()

    /**
     * Wraps the biometrics register API endpoint which validates the signature passed in. If this method succeeds,
     * the user will be logged in and granted an active session
     * @param parameters required to register a biometrics key
     */
    public suspend fun register(
        parameters: StartParameters
    ): BiometricsAuthResponse

    /**
     * Wraps the biometrics register API endpoint which validates the signature passed in. If this method succeeds,
     * the user will be logged in and granted an active session
     * @param parameters required to register a biometrics key
     * @param callback calls callback with BiometricsRegisterResponse response from backend
     */
    public fun register(
        parameters: StartParameters,
        callback: (response: BiometricsAuthResponse) -> Unit
    )

    /**
     * Wraps the biometrics authenticate API endpoint which validates the signature passed in. If this method succeeds,
     * the user will be logged in and granted an active session
     * @param parameters required to register a biometrics key
     */
    public suspend fun authenticate(
        parameters: StartParameters
    ): BiometricsAuthResponse

    /**
     * Wraps the biometrics authenticate API endpoint which validates the signature passed in. If this method succeeds,
     * the user will be logged in and granted an active session
     * @param parameters required to register a biometrics key
     * @param callback calls callback with BiometricsRegisterResponse response from backend
     */
    public fun authenticate(
        parameters: StartParameters,
        callback: (response: BiometricsAuthResponse) -> Unit
    )
}

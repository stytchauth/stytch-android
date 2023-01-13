package com.stytch.sdk.biometrics

import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher

// Wrapper around BiometricManager results to provide friendly messaging for developers
public enum class BiometricAvailability(public val message: String) {
    BIOMETRIC_KEY_GENERATION_FAILED("Biometric key generation failed."),
    BIOMETRIC_SUCCESS("Biometrics are ready to be used."),
    BIOMETRIC_ERROR_NO_HARDWARE("No biometric features available on this device."),
    BIOMETRIC_ERROR_HW_UNAVAILABLE("Biometric features are currently unavailable."),
    BIOMETRIC_ERROR_NONE_ENROLLED("No biometrics currently enrolled on device."),
    BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED(
        "A security vulnerability has been discovered with one or more hardware sensors."
    ),
    BIOMETRIC_ERROR_UNSUPPORTED("The requested biometrics options are incompatible with the current Android version."),
    BIOMETRIC_STATUS_UNKNOWN("Unable to determine whether the user can authenticate."),
    BIOMETRICS_REVOKED("Biometric key was revoked. Must re-register a biometric authentication.")
}

internal interface BiometricsProvider {
    suspend fun showBiometricPromptForRegistration(
        context: FragmentActivity,
        promptData: Biometrics.PromptData? = null,
        allowedAuthenticators: Int,
    ): Cipher

    suspend fun showBiometricPromptForAuthentication(
        context: FragmentActivity,
        promptData: Biometrics.PromptData? = null,
        iv: ByteArray,
        allowedAuthenticators: Int,
    ): Cipher

    fun areBiometricsAvailable(context: FragmentActivity, allowedAuthenticators: Int): BiometricAvailability

    fun deleteSecretKey()

    fun ensureSecretKeyIsAvailable(allowedAuthenticators: Int)
}

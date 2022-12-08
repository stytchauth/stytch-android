package com.stytch.sdk

import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.fragment.app.FragmentActivity

public data class BiometricAvailability(
    val available: Boolean,
    val message: String,
)

public interface BiometricsProvider {
    public suspend fun showBiometricPrompt(
        context: FragmentActivity,
        promptInfo: BiometricPrompt.PromptInfo? = null,
        cryptoObject: CryptoObject,
    ): CryptoObject?

    public fun areBiometricsAvailable(context: FragmentActivity): BiometricAvailability
}

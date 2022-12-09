package com.stytch.sdk

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher

public data class BiometricAvailability(
    val available: Boolean,
    val message: String,
)

internal interface BiometricsProvider {
    suspend fun showBiometricPromptForRegistration(
        context: FragmentActivity,
        promptInfo: BiometricPrompt.PromptInfo? = null,
    ): Cipher

    suspend fun showBiometricPromptForAuthentication(
        context: FragmentActivity,
        promptInfo: BiometricPrompt.PromptInfo? = null,
        iv: ByteArray,
    ): Cipher

    fun areBiometricsAvailable(context: FragmentActivity): BiometricAvailability
}

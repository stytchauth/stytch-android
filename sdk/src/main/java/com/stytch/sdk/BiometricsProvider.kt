package com.stytch.sdk

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

public interface BiometricsProvider {
    public suspend fun showBiometricPrompt(
        context: FragmentActivity,
        promptInfo: BiometricPrompt.PromptInfo? = null
    )
    public fun areBiometricsAvailable(context: FragmentActivity): Pair<Boolean, String>
}

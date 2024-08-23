package com.stytch.sdk.consumer.biometrics

import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher

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

    fun areBiometricsAvailable(
        context: FragmentActivity,
        allowedAuthenticators: Int,
    ): Int

    fun deleteSecretKey()

    fun ensureSecretKeyIsAvailable(allowedAuthenticators: Int)
}

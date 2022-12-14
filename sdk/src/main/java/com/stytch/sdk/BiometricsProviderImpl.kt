package com.stytch.sdk

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import java.util.concurrent.Executors
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal const val AUTHENTICATION_FAILED = "Authentication Failed"
private const val BIOMETRIC_KEY_NAME = "stytch_biometric_key"

internal class BiometricsProviderImpl : BiometricsProvider {
    private var secretKey: SecretKey? = null
    init {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            // Before the keystore can be accessed, it must be loaded.
            keyStore.load(null)
            if (!keyStore.containsAlias(BIOMETRIC_KEY_NAME)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore"
                )
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    BIOMETRIC_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    .build()
                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }
            secretKey = keyStore.getKey(BIOMETRIC_KEY_NAME, null) as SecretKey
        } catch (e: Exception) {
            StytchLog.e(e.message ?: BiometricAvailability.BIOMETRIC_KEY_GENERATION_FAILED.message)
        }
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/" +
                KeyProperties.BLOCK_MODE_CBC + "/" +
                KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }

    private suspend fun showBiometricPrompt(
        context: FragmentActivity,
        promptInfo: BiometricPrompt.PromptInfo?,
        cipher: Cipher,
    ): Cipher = suspendCoroutine { continuation ->
        val executor = Executors.newSingleThreadExecutor()
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                continuation.resumeWithException(StytchExceptions.Input(errString.toString()))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                result.cryptoObject?.cipher
                    ?.let { continuation.resume(it) }
                    ?: continuation.resumeWithException(StytchExceptions.Input(AUTHENTICATION_FAILED))
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                continuation.resumeWithException(StytchExceptions.Input(AUTHENTICATION_FAILED))
            }
        }
        val prompt = promptInfo ?: BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.stytch_biometric_prompt_title))
            .setSubtitle(context.getString(R.string.stytch_biometric_prompt_subtitle))
            .setNegativeButtonText(context.getString(R.string.stytch_biometric_prompt_negative))
            .build()
        BiometricPrompt(context, executor, callback).authenticate(prompt, CryptoObject(cipher))
    }

    override suspend fun showBiometricPromptForRegistration(
        context: FragmentActivity,
        promptInfo: BiometricPrompt.PromptInfo?,
    ): Cipher {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return showBiometricPrompt(context, promptInfo, cipher)
    }

    override suspend fun showBiometricPromptForAuthentication(
        context: FragmentActivity,
        promptInfo: BiometricPrompt.PromptInfo?,
        iv: ByteArray,
    ): Cipher {
        val cipher = getCipher()
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        return showBiometricPrompt(context, promptInfo, cipher)
    }

    override fun areBiometricsAvailable(context: FragmentActivity): BiometricAvailability {
        if (secretKey == null) return BiometricAvailability.BIOMETRIC_KEY_GENERATION_FAILED
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.BIOMETRIC_SUCCESS
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.BIOMETRIC_ERROR_NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.BIOMETRIC_ERROR_HW_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.BIOMETRIC_ERROR_NONE_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricAvailability.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> BiometricAvailability.BIOMETRIC_ERROR_UNSUPPORTED
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> BiometricAvailability.BIOMETRIC_STATUS_UNKNOWN
            else -> BiometricAvailability.BIOMETRIC_STATUS_UNKNOWN
        }
    }
}

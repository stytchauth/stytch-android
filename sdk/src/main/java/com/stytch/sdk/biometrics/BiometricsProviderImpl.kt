package com.stytch.sdk.biometrics

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.fragment.app.FragmentActivity
import com.stytch.sdk.R
import com.stytch.sdk.StytchExceptions
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
    private val keyStore = KeyStore.getInstance("AndroidKeyStore")
    private fun allowedAuthenticatorsIncludeDeviceCredentials(allowedAuthenticators: Int) =
        allowedAuthenticators == Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL

    private fun getSecretKey(allowedAuthenticators: Int): SecretKey? = try {
        if (!keyStore.containsAlias(BIOMETRIC_KEY_NAME)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                BIOMETRIC_KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                setUserAuthenticationRequired(true)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    val authenticationParameters =
                        if (allowedAuthenticatorsIncludeDeviceCredentials(allowedAuthenticators)) {
                            KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
                        } else {
                            KeyProperties.AUTH_BIOMETRIC_STRONG
                        }
                    setUserAuthenticationParameters(0, authenticationParameters)
                }
            }.build()
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
        keyStore.getKey(BIOMETRIC_KEY_NAME, null) as SecretKey
    } catch (_: Exception) {
        null
    }

    init {
        keyStore.load(null)
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
        promptData: Biometrics.PromptData?,
        cipher: Cipher,
        allowedAuthenticators: Int,
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
        }
        val prompt = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(promptData?.title ?: context.getString(R.string.stytch_biometric_prompt_title))
            setSubtitle(promptData?.subTitle ?: context.getString(R.string.stytch_biometric_prompt_subtitle))
            setAllowedAuthenticators(allowedAuthenticators)
            if (!allowedAuthenticatorsIncludeDeviceCredentials(allowedAuthenticators)) {
                // can only show negative button if device credentials are not allowed
                setNegativeButtonText(
                    promptData?.negativeButtonText ?: context.getString(R.string.stytch_biometric_prompt_negative)
                )
            }
        }.build()
        BiometricPrompt(context, executor, callback).authenticate(prompt, CryptoObject(cipher))
    }

    override suspend fun showBiometricPromptForRegistration(
        context: FragmentActivity,
        promptData: Biometrics.PromptData?,
        allowedAuthenticators: Int,
    ): Cipher {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(allowedAuthenticators))
        return showBiometricPrompt(context, promptData, cipher, allowedAuthenticators)
    }

    override suspend fun showBiometricPromptForAuthentication(
        context: FragmentActivity,
        promptData: Biometrics.PromptData?,
        iv: ByteArray,
        allowedAuthenticators: Int,
    ): Cipher {
        val cipher = getCipher()
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(allowedAuthenticators), IvParameterSpec(iv))
        return showBiometricPrompt(context, promptData, cipher, allowedAuthenticators)
    }

    override fun areBiometricsAvailable(context: FragmentActivity, allowedAuthenticators: Int): BiometricAvailability {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(allowedAuthenticators)) {
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

    override fun deleteSecretKey() {
        keyStore.deleteEntry(BIOMETRIC_KEY_NAME)
    }

    override fun ensureSecretKeyIsAvailable(allowedAuthenticators: Int,) {
        val secretKey = getSecretKey(allowedAuthenticators) ?: error("SecretKey cannot be null")
        // initialize a cipher (that we won't use) with the secretkey to ensure it hasn't been invalidated
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    }
}

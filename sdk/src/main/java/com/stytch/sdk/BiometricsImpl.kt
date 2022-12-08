package com.stytch.sdk

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sessions.SessionStorage
import com.stytch.sessions.launchSessionUpdater
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.Signer
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import javax.crypto.spec.IvParameterSpec

internal const val LAST_USED_BIOMETRIC_REGISTRATION_ID = "last_used_biometric_registration_id"
internal const val PUBLIC_KEY_KEY = "biometrics_public_key"
internal const val PRIVATE_KEY_KEY = "biometrics_private_key"
internal const val PRIVATE_KEY_IV_KEY = "biometrics_private_key_iv"
private const val BIOMETRIC_KEY_NAME = "stytch_biometric_key"

public class BiometricsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val storageHelper: StorageHelper,
    private val api: StytchApi.Biometrics,
    private val biometricsProvider: BiometricsProvider,
    private val userManagerApi: StytchApi.UserManagement,
) : Biometrics {
    override val registrationAvailable: Boolean
        get() = storageHelper.preferenceExists(keyAlias = PRIVATE_KEY_KEY)

    override fun areBiometricsAvailable(context: FragmentActivity): BiometricAvailability =
        biometricsProvider.areBiometricsAvailable(context)

    override suspend fun removeRegistration(): Boolean = withContext(dispatchers.io) {
        val biometricRegistrationId = storageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID)
            ?: return@withContext false
        userManagerApi.deleteBiometricRegistrationById(biometricRegistrationId)
        storageHelper.deletePreference(LAST_USED_BIOMETRIC_REGISTRATION_ID)
        storageHelper.deletePreference(PUBLIC_KEY_KEY)
        storageHelper.deletePreference(PRIVATE_KEY_KEY)
        storageHelper.deletePreference(PRIVATE_KEY_IV_KEY)
    }

    override fun removeRegistration(callback: (Boolean) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = removeRegistration()
            callback(result)
        }
    }

    override fun isUsingKeystore(): Boolean = storageHelper.checkIfKeysetIsUsingKeystore()

    private fun ensureKeystoreIsSecureOrFallbackIsAllowedOrThrow(allowFallbackToCleartext: Boolean) {
        if (!isUsingKeystore() && !allowFallbackToCleartext) {
            throw StytchExceptions.Input(StytchErrorType.NOT_USING_KEYSTORE.message)
        }
    }

    private fun ensureSessionIsValidOrThrow() {
        if (
            (sessionStorage.sessionToken == null && sessionStorage.sessionJwt == null) ||
            sessionStorage.session?.isExpired() == true
        ) {
            throw StytchExceptions.Input(StytchErrorType.NO_CURRENT_SESSION.message)
        }
    }

    private fun generateKeyPair(): Pair<String, String> = try {
        val gen = Ed25519KeyPairGenerator()
        gen.init(Ed25519KeyGenerationParameters(SecureRandom()))
        val keyPair = gen.generateKeyPair()
        val publicKey = keyPair.public as Ed25519PublicKeyParameters
        val privateKey = keyPair.private as Ed25519PrivateKeyParameters
        Pair(
            Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP),
            Base64.encodeToString(privateKey.encoded, Base64.NO_WRAP)
        )
    } catch (e: Exception) {
        throw StytchExceptions.Input(StytchErrorType.KEY_GENERATION_FAILED.message)
    }

    private fun signChallenge(challengeString: String, privateKeyString: String): String = try {
        val signer: Signer = Ed25519Signer()
        val challenge = Base64.decode(challengeString, Base64.NO_WRAP)
        val privateKey = Ed25519PrivateKeyParameters(Base64.decode(privateKeyString, Base64.NO_WRAP))
        signer.init(true, privateKey)
        signer.update(challenge, 0, challenge.size)
        val signature: ByteArray = signer.generateSignature()
        Base64.encodeToString(signature, Base64.NO_WRAP)
    } catch (e: Exception) {
        throw StytchExceptions.Input(StytchErrorType.ERROR_SIGNING_CHALLENGE.message)
    }

    init {
        generateSecretKey(
            KeyGenParameterSpec.Builder(
                BIOMETRIC_KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .build()
        )
    }

    /**
     * This method generates an instance of SecretKey
     */
    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    /**
     * This method gets an instance of SecretKey
     */
    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")

        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null)
        return keyStore.getKey(BIOMETRIC_KEY_NAME, null) as SecretKey
    }

    /**
     * This method gets a cipher instance
     */
    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/" +
                KeyProperties.BLOCK_MODE_CBC + "/" +
                KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }

    override suspend fun register(parameters: Biometrics.RegisterParameters): BiometricsAuthResponse =
        withContext(dispatchers.io) {
            try {
                ensureKeystoreIsSecureOrFallbackIsAllowedOrThrow(parameters.allowFallbackToCleartext)
                if (registrationAvailable) {
                    removeRegistration()
                }
                ensureSessionIsValidOrThrow()
                val cipher = getCipher()
                val secretKey = getSecretKey()
                cipher.init(Cipher.ENCRYPT_MODE, secretKey)
                val returnedCipher = withContext(dispatchers.ui) {
                    biometricsProvider.showBiometricPrompt(
                        context = parameters.context,
                        promptInfo = parameters.promptInfo,
                        cryptoObject = BiometricPrompt.CryptoObject(cipher)
                    )
                }?.cipher ?: throw StytchExceptions.Input("No cipher returned")
                val (publicKey, privateKey) = generateKeyPair()
                val encryptedPrivateKey = returnedCipher.doFinal(Base64.decode(privateKey, Base64.NO_WRAP))
                val encryptedPrivateKeyString = Base64.encodeToString(encryptedPrivateKey, Base64.NO_WRAP)
                val startResponse = api.registerStart(publicKey = publicKey).getValueOrThrow()
                val signature = signChallenge(
                    challengeString = startResponse.challenge,
                    privateKeyString = privateKey
                )
                api.register(
                    signature = signature,
                    biometricRegistrationId = startResponse.biometricRegistrationId,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    storageHelper.saveValue(LAST_USED_BIOMETRIC_REGISTRATION_ID, startResponse.biometricRegistrationId)
                    storageHelper.saveValue(PUBLIC_KEY_KEY, publicKey)
                    storageHelper.saveValue(PRIVATE_KEY_KEY, encryptedPrivateKeyString)
                    storageHelper.saveValue(PRIVATE_KEY_IV_KEY, Base64.encodeToString(returnedCipher.iv, Base64.NO_WRAP))
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            } catch (e: StytchExceptions) {
                // remove the existing registration except in case of missing/expired session
                when ((e as? StytchExceptions.Input)?.reason) {
                    StytchErrorType.NO_CURRENT_SESSION.message -> { } // no-op
                    else -> removeRegistration()
                }
                StytchResult.Error(e)
            } catch (e: Exception) {
                removeRegistration()
                StytchResult.Error(StytchExceptions.Critical(e))
            }
        }

    override fun register(
        parameters: Biometrics.RegisterParameters,
        callback: (response: BiometricsAuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = register(parameters)
            callback(result)
        }
    }

    override suspend fun authenticate(parameters: Biometrics.AuthenticateParameters): BiometricsAuthResponse =
        withContext(dispatchers.io) {
            try {
                if (!registrationAvailable) {
                    throw StytchExceptions.Input(StytchErrorType.NO_BIOMETRICS_REGISTRATIONS_AVAILABLE.message)
                }
                val cipher = getCipher()
                val secretKey = getSecretKey()
                val iv = Base64.decode(storageHelper.loadValue(PRIVATE_KEY_IV_KEY), Base64.NO_WRAP)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
                val returnedCipher = withContext(dispatchers.ui) {
                    biometricsProvider.showBiometricPrompt(
                        context = parameters.context,
                        promptInfo = parameters.promptInfo,
                        cryptoObject = BiometricPrompt.CryptoObject(cipher)
                    )
                }?.cipher ?: throw StytchExceptions.Input("No cipher returned")
                val publicKey = storageHelper.loadValue(PUBLIC_KEY_KEY)!!
                val encryptedPrivateKey = storageHelper.loadValue(PRIVATE_KEY_KEY)!!
                val decryptedPrivateKey = returnedCipher.doFinal(Base64.decode(encryptedPrivateKey, Base64.NO_WRAP))
                val startResponse = api.authenticateStart(publicKey = publicKey).getValueOrThrow()
                val signature = signChallenge(
                    challengeString = startResponse.challenge,
                    privateKeyString = Base64.encodeToString(decryptedPrivateKey, Base64.NO_WRAP)
                )
                api.authenticate(
                    signature = signature,
                    biometricRegistrationId = startResponse.biometricRegistrationId,
                    sessionDurationMinutes = parameters.sessionDurationMinutes
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            } catch (e: StytchExceptions) {
                StytchResult.Error(e)
            } catch (e: Exception) {
                StytchResult.Error(StytchExceptions.Critical(e))
            }
        }

    override fun authenticate(
        parameters: Biometrics.AuthenticateParameters,
        callback: (response: BiometricsAuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }
}

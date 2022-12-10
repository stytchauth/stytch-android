package com.stytch.sdk

import androidx.fragment.app.FragmentActivity
import com.stytch.sdk.extensions.toBase64DecodedByteArray
import com.stytch.sdk.extensions.toBase64EncodedString
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sessions.SessionStorage
import com.stytch.sessions.launchSessionUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.Signer
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.security.SecureRandom

internal const val LAST_USED_BIOMETRIC_REGISTRATION_ID = "last_used_biometric_registration_id"
internal const val PRIVATE_KEY_KEY = "biometrics_private_key"
internal const val CIPHER_IV_KEY = "cipher_iv"
private val KEYS_REQUIRED_FOR_REGISTRATION = listOf(
    LAST_USED_BIOMETRIC_REGISTRATION_ID,
    PRIVATE_KEY_KEY,
    CIPHER_IV_KEY,
)

@Suppress("LongParameterList")
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
        get() = KEYS_REQUIRED_FOR_REGISTRATION.all { storageHelper.preferenceExists(it) }

    override fun areBiometricsAvailable(context: FragmentActivity): BiometricAvailability =
        biometricsProvider.areBiometricsAvailable(context)

    override suspend fun removeRegistration(): Boolean = withContext(dispatchers.io) {
        val biometricRegistrationId = storageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID)
            ?: return@withContext false
        userManagerApi.deleteBiometricRegistrationById(biometricRegistrationId)
        KEYS_REQUIRED_FOR_REGISTRATION.forEach { storageHelper.deletePreference(it) }
        true
    }

    override fun removeRegistration(callback: (Boolean) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = removeRegistration()
            callback(result)
        }
    }

    override fun isUsingKeystore(): Boolean = storageHelper.checkIfKeysetIsUsingKeystore()

    private fun generateKeyPair(): Pair<String, String> = try {
        val gen = Ed25519KeyPairGenerator()
        gen.init(Ed25519KeyGenerationParameters(SecureRandom()))
        val keyPair = gen.generateKeyPair()
        val publicKey = keyPair.public as Ed25519PublicKeyParameters
        val privateKey = keyPair.private as Ed25519PrivateKeyParameters
        Pair(publicKey.encoded.toBase64EncodedString(), privateKey.encoded.toBase64EncodedString())
    } catch (e: Exception) {
        StytchLog.e(e.message ?: StytchErrorType.KEY_GENERATION_FAILED.message)
        throw StytchExceptions.Input(StytchErrorType.KEY_GENERATION_FAILED.message)
    }

    private fun signChallenge(challengeString: String, privateKeyString: String): String = try {
        val signer: Signer = Ed25519Signer()
        val challenge = challengeString.toBase64DecodedByteArray()
        val privateKey = Ed25519PrivateKeyParameters(privateKeyString.toBase64DecodedByteArray())
        signer.init(true, privateKey)
        signer.update(challenge, 0, challenge.size)
        val signature: ByteArray = signer.generateSignature()
        signature.toBase64EncodedString()
    } catch (e: Exception) {
        StytchLog.e(e.message ?: StytchErrorType.ERROR_SIGNING_CHALLENGE.message)
        throw StytchExceptions.Input(StytchErrorType.ERROR_SIGNING_CHALLENGE.message)
    }

    override suspend fun register(parameters: Biometrics.RegisterParameters): BiometricsAuthResponse =
        withContext(dispatchers.io) {
            try {
                if (!isUsingKeystore() && !parameters.allowFallbackToCleartext) {
                    throw StytchExceptions.Input(StytchErrorType.NOT_USING_KEYSTORE.message)
                }
                if (registrationAvailable) {
                    removeRegistration()
                }
                sessionStorage.ensureSessionIsValidOrThrow()
                val cipher = withContext(dispatchers.ui) {
                    biometricsProvider.showBiometricPromptForRegistration(
                        context = parameters.context,
                        promptInfo = parameters.promptInfo
                    )
                }
                val (publicKey, privateKey) = generateKeyPair()
                val encryptedPrivateKeyBytes = cipher.doFinal(privateKey.toBase64DecodedByteArray())
                val encryptedPrivateKeyString = encryptedPrivateKeyBytes.toBase64EncodedString()
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
                    if (this is StytchResult.Success) {
                        storageHelper.saveValue(
                            LAST_USED_BIOMETRIC_REGISTRATION_ID,
                            startResponse.biometricRegistrationId
                        )
                        storageHelper.saveValue(PRIVATE_KEY_KEY, encryptedPrivateKeyString)
                        storageHelper.saveValue(CIPHER_IV_KEY, cipher.iv.toBase64EncodedString())
                    }
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            } catch (e: StytchExceptions) {
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

    private fun derivePublicKeyFromPrivateKeyBytes(privateKeyBytes: ByteArray): String {
        val privateKeyRebuild = Ed25519PrivateKeyParameters(privateKeyBytes, 0)
        val publicKeyRebuild = privateKeyRebuild.generatePublicKey()
        return publicKeyRebuild.encoded.toBase64EncodedString()
    }

    override suspend fun authenticate(parameters: Biometrics.AuthenticateParameters): BiometricsAuthResponse =
        withContext(dispatchers.io) {
            try {
                val encryptedPrivateKey = storageHelper.loadValue(PRIVATE_KEY_KEY)
                val iv = storageHelper.loadValue(CIPHER_IV_KEY)
                try {
                    require(registrationAvailable && encryptedPrivateKey != null && iv != null)
                } catch (e: IllegalStateException) {
                    StytchLog.e(e.message ?: StytchErrorType.NO_BIOMETRICS_REGISTRATIONS_AVAILABLE.message)
                    throw StytchExceptions.Input(StytchErrorType.NO_BIOMETRICS_REGISTRATIONS_AVAILABLE.message)
                }
                val cipher = withContext(dispatchers.ui) {
                    biometricsProvider.showBiometricPromptForAuthentication(
                        context = parameters.context,
                        promptInfo = parameters.promptInfo,
                        iv = iv.toBase64DecodedByteArray(),
                    )
                }
                val encryptedPrivateKeyBytes = encryptedPrivateKey.toBase64DecodedByteArray()
                val decryptedPrivateKey = cipher.doFinal(encryptedPrivateKeyBytes)
                val privateKeyString = decryptedPrivateKey.toBase64EncodedString()
                val publicKeyString = derivePublicKeyFromPrivateKeyBytes(decryptedPrivateKey)
                val startResponse = api.authenticateStart(publicKey = publicKeyString).getValueOrThrow()
                val signature = signChallenge(
                    challengeString = startResponse.challenge,
                    privateKeyString = privateKeyString
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

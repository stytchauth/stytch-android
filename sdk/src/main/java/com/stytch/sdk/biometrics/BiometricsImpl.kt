package com.stytch.sdk.biometrics

import androidx.fragment.app.FragmentActivity
import com.stytch.sdk.BiometricsAuthResponse
import com.stytch.sdk.EncryptionManager
import com.stytch.sdk.StorageHelper
import com.stytch.sdk.StytchDispatchers
import com.stytch.sdk.StytchExceptions
import com.stytch.sdk.StytchLog
import com.stytch.sdk.StytchResult
import com.stytch.sdk.extensions.toBase64DecodedByteArray
import com.stytch.sdk.extensions.toBase64EncodedString
import com.stytch.sdk.getValueOrThrow
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sessions.SessionStorage
import com.stytch.sessions.launchSessionUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal const val LAST_USED_BIOMETRIC_REGISTRATION_ID = "last_used_biometric_registration_id"
internal const val PRIVATE_KEY_KEY = "biometrics_private_key"
internal const val CIPHER_IV_KEY = "biometrics_cipher_iv"
private val KEYS_REQUIRED_FOR_REGISTRATION = listOf(
    LAST_USED_BIOMETRIC_REGISTRATION_ID,
    PRIVATE_KEY_KEY,
    CIPHER_IV_KEY,
)

@Suppress("LongParameterList")
internal class BiometricsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val storageHelper: StorageHelper,
    private val api: StytchApi.Biometrics,
    private val biometricsProvider: BiometricsProvider,
    private val deleteBiometricRegistraton: suspend (String) -> Unit,
) : Biometrics {
    override val registrationAvailable: Boolean
        get() = KEYS_REQUIRED_FOR_REGISTRATION.all { storageHelper.preferenceExists(it) }

    override fun areBiometricsAvailable(context: FragmentActivity): BiometricAvailability =
        biometricsProvider.areBiometricsAvailable(context)

    override suspend fun removeRegistration(): Boolean = withContext(dispatchers.io) {
        storageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID)?.let {
            deleteBiometricRegistraton(it)
        }
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
                val (publicKey, privateKey) = EncryptionManager.generateEd25519KeyPair()
                val encryptedPrivateKeyBytes = cipher.doFinal(privateKey.toBase64DecodedByteArray())
                val encryptedPrivateKeyString = encryptedPrivateKeyBytes.toBase64EncodedString()
                val startResponse = api.registerStart(publicKey = publicKey).getValueOrThrow()
                val signature = EncryptionManager.signEd25519Challenge(
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

    override suspend fun authenticate(parameters: Biometrics.AuthenticateParameters): BiometricsAuthResponse =
        withContext(dispatchers.io) {
            try {
                val encryptedPrivateKey = storageHelper.loadValue(PRIVATE_KEY_KEY)
                val iv = storageHelper.loadValue(CIPHER_IV_KEY)
                try {
                    require(registrationAvailable && encryptedPrivateKey != null && iv != null)
                } catch (e: IllegalArgumentException) {
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
                val publicKeyString = EncryptionManager.deriveEd25519PublicKeyFromPrivateKeyBytes(decryptedPrivateKey)
                val startResponse = api.authenticateStart(publicKey = publicKeyString).getValueOrThrow()
                val signature = EncryptionManager.signEd25519Challenge(
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

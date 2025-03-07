package com.stytch.sdk.consumer.biometrics

import android.os.Build
import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.fragment.app.FragmentActivity
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.BiometricsAlreadyEnrolledError
import com.stytch.sdk.common.errors.StytchError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.errors.StytchKeystoreUnavailableError
import com.stytch.sdk.common.errors.StytchNoBiometricsRegistrationError
import com.stytch.sdk.common.extensions.toBase64DecodedByteArray
import com.stytch.sdk.common.extensions.toBase64EncodedString
import com.stytch.sdk.common.getValueOrThrow
import com.stytch.sdk.consumer.BiometricsAuthResponse
import com.stytch.sdk.consumer.ConsumerAuthMethod
import com.stytch.sdk.consumer.DeleteFactorResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal const val LAST_USED_BIOMETRIC_REGISTRATION_ID = "last_used_biometric_registration_id"
internal const val PRIVATE_KEY_KEY = "biometrics_private_key"
internal const val CIPHER_IV_KEY = "biometrics_cipher_iv"
internal const val ALLOW_DEVICE_CREDENTIALS_KEY = "biometric_allow_device_credentials"
internal val KEYS_REQUIRED_FOR_REGISTRATION =
    listOf(
        LAST_USED_BIOMETRIC_REGISTRATION_ID,
        PRIVATE_KEY_KEY,
        CIPHER_IV_KEY,
        ALLOW_DEVICE_CREDENTIALS_KEY,
    )

@Suppress("LongParameterList")
internal class BiometricsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val storageHelper: StorageHelper,
    private val api: StytchApi.Biometrics,
    private val biometricsProvider: BiometricsProvider,
    private val deleteBiometricRegistration: suspend (String) -> DeleteFactorResponse,
) : Biometrics {
    private fun getAllowedAuthenticators(allowDeviceCredentials: Boolean) =
        if (allowDeviceCredentials && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else {
            BIOMETRIC_STRONG
        }

    private fun registrationExists(): Boolean =
        KEYS_REQUIRED_FOR_REGISTRATION.all {
            storageHelper.preferenceExists(it)
        }

    override fun isRegistrationAvailable(context: FragmentActivity): Boolean =
        registrationExists() && areBiometricsAvailable(context) != BiometricAvailability.RegistrationRevoked

    override fun areBiometricsAvailable(
        context: FragmentActivity,
        allowDeviceCredentials: Boolean,
    ): BiometricAvailability {
        val allowedAuthenticators = getAllowedAuthenticators(allowDeviceCredentials)
        try {
            biometricsProvider.ensureSecretKeyIsAvailable(allowedAuthenticators)
        } catch (_: KeyPermanentlyInvalidatedException) {
            externalScope.launch(dispatchers.io) {
                removeRegistration()
            }
            return BiometricAvailability.RegistrationRevoked
        } catch (_: IllegalStateException) {
            // Secret key is null/couldn't be created (likely because of missing biometric factor). Do nothing and fall
            // back to regular areBiometricsAvailable check for full information
        }
        return when (val result = biometricsProvider.areBiometricsAvailable(context, allowedAuthenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                when (registrationExists()) {
                    true -> BiometricAvailability.AvailableRegistered
                    false -> BiometricAvailability.AvailableNoRegistrations
                }
            }
            else -> BiometricAvailability.Unavailable.fromReason(result)
        }
    }

    private fun removeLocalRegistrationOnly() {
        KEYS_REQUIRED_FOR_REGISTRATION.forEach { key -> storageHelper.deletePreference(key) }
        biometricsProvider.deleteSecretKey()
    }

    override suspend fun removeRegistration(): Boolean =
        withContext(dispatchers.io) {
            val lastUsedRegistrationId = storageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID)
            if (lastUsedRegistrationId.isNullOrEmpty()) return@withContext true
            return@withContext when (deleteBiometricRegistration(lastUsedRegistrationId)) {
                is StytchResult.Success -> {
                    removeLocalRegistrationOnly()
                    true
                }
                else -> false
            }
        }

    override fun removeRegistration(callback: (Boolean) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = removeRegistration()
            callback(result)
        }
    }

    override fun removeRegistrationCompletable(): CompletableFuture<Boolean> =
        externalScope
            .async {
                removeRegistration()
            }.asCompletableFuture()

    override fun isUsingKeystore(): Boolean = storageHelper.checkIfKeysetIsUsingKeystore()

    override suspend fun register(parameters: Biometrics.RegisterParameters): BiometricsAuthResponse =
        withContext(dispatchers.io) {
            try {
                if (!isUsingKeystore() && !parameters.allowFallbackToCleartext) {
                    throw StytchKeystoreUnavailableError()
                }
                if (isRegistrationAvailable(parameters.context)) {
                    throw BiometricsAlreadyEnrolledError
                }
                sessionStorage.ensureSessionIsValidOrThrow()
                val allowedAuthenticators = getAllowedAuthenticators(parameters.allowDeviceCredentials)
                val cipher =
                    withContext(dispatchers.ui) {
                        biometricsProvider.showBiometricPromptForRegistration(
                            context = parameters.context,
                            promptData = parameters.promptData,
                            allowedAuthenticators = allowedAuthenticators,
                        )
                    }
                val (publicKey, privateKey) = EncryptionManager.generateEd25519KeyPair()
                val encryptedPrivateKeyBytes = cipher.doFinal(privateKey.toBase64DecodedByteArray())
                val encryptedPrivateKeyString = encryptedPrivateKeyBytes.toBase64EncodedString()
                val startResponse = api.registerStart(publicKey = publicKey).getValueOrThrow()
                val signature =
                    EncryptionManager.signEd25519Challenge(
                        challengeString = startResponse.challenge,
                        privateKeyString = privateKey,
                    )
                api
                    .register(
                        signature = signature,
                        biometricRegistrationId = startResponse.biometricRegistrationId,
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
                    ).apply {
                        if (this is StytchResult.Success) {
                            storageHelper.saveValue(
                                LAST_USED_BIOMETRIC_REGISTRATION_ID,
                                startResponse.biometricRegistrationId,
                            )
                            storageHelper.saveValue(PRIVATE_KEY_KEY, encryptedPrivateKeyString)
                            storageHelper.saveValue(CIPHER_IV_KEY, cipher.iv.toBase64EncodedString())
                            storageHelper.saveBoolean(ALLOW_DEVICE_CREDENTIALS_KEY, parameters.allowDeviceCredentials)
                        }
                        sessionStorage.lastAuthMethodUsed = ConsumerAuthMethod.BIOMETRICS
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
            } catch (e: StytchError) {
                StytchResult.Error(e)
            } catch (e: Exception) {
                removeLocalRegistrationOnly()
                StytchResult.Error(StytchInternalError(exception = e))
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

    override fun registerCompletable(
        parameters: Biometrics.RegisterParameters,
    ): CompletableFuture<BiometricsAuthResponse> =
        externalScope
            .async {
                register(parameters)
            }.asCompletableFuture()

    override suspend fun authenticate(parameters: Biometrics.AuthenticateParameters): BiometricsAuthResponse =
        withContext(dispatchers.io) {
            try {
                val allowDeviceCredentials = storageHelper.getBoolean(ALLOW_DEVICE_CREDENTIALS_KEY)
                val allowedAuthenticators = getAllowedAuthenticators(allowDeviceCredentials)
                val encryptedPrivateKey = storageHelper.loadValue(PRIVATE_KEY_KEY)
                val iv = storageHelper.loadValue(CIPHER_IV_KEY)
                try {
                    require(isRegistrationAvailable(parameters.context) && encryptedPrivateKey != null && iv != null)
                } catch (e: IllegalArgumentException) {
                    throw StytchNoBiometricsRegistrationError()
                }
                val cipher =
                    withContext(dispatchers.ui) {
                        biometricsProvider.showBiometricPromptForAuthentication(
                            context = parameters.context,
                            promptData = parameters.promptData,
                            iv = iv.toBase64DecodedByteArray(),
                            allowedAuthenticators = allowedAuthenticators,
                        )
                    }
                val encryptedPrivateKeyBytes = encryptedPrivateKey.toBase64DecodedByteArray()
                val decryptedPrivateKey = cipher.doFinal(encryptedPrivateKeyBytes)
                val privateKeyString = decryptedPrivateKey.toBase64EncodedString()
                val publicKeyString = EncryptionManager.deriveEd25519PublicKeyFromPrivateKeyBytes(decryptedPrivateKey)
                val startResponse = api.authenticateStart(publicKey = publicKeyString).getValueOrThrow()
                val signature =
                    EncryptionManager.signEd25519Challenge(
                        challengeString = startResponse.challenge,
                        privateKeyString = privateKeyString,
                    )
                api
                    .authenticate(
                        signature = signature,
                        biometricRegistrationId = startResponse.biometricRegistrationId,
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
                    ).apply {
                        sessionStorage.lastAuthMethodUsed = ConsumerAuthMethod.BIOMETRICS
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
            } catch (e: StytchError) {
                StytchResult.Error(e)
            } catch (e: Exception) {
                StytchResult.Error(StytchInternalError(e))
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

    override fun authenticateCompletable(
        parameters: Biometrics.AuthenticateParameters,
    ): CompletableFuture<BiometricsAuthResponse> =
        externalScope
            .async {
                authenticate(parameters)
            }.asCompletableFuture()
}

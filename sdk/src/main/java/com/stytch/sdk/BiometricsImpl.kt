package com.stytch.sdk

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sessions.SessionStorage
import com.stytch.sessions.launchSessionUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal const val BIOMETRICS_REGISTRATION_KEY = "stytch_biometrics_registration_key"
internal const val LAST_USED_BIOMETRIC_REGISTRATION_ID = "last_used_biometric_registration_id"

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
        get() = storageHelper.ed25519KeyExists(keyAlias = BIOMETRICS_REGISTRATION_KEY)

    override fun areBiometricsAvailable(context: FragmentActivity): BiometricAvailability =
        biometricsProvider.areBiometricsAvailable(context)

    override suspend fun removeRegistration(): Boolean = withContext(dispatchers.io) {
        val biometricRegistrationId = storageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID)
            ?: return@withContext false
        userManagerApi.deleteBiometricRegistrationById(biometricRegistrationId)
        storageHelper.saveValue(LAST_USED_BIOMETRIC_REGISTRATION_ID, null)
        storageHelper.deleteEd25519Key(keyAlias = BIOMETRICS_REGISTRATION_KEY)
    }

    override fun removeRegistration(callback: (Boolean) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = removeRegistration()
            callback(result)
        }
    }

    override fun isUsingKeystore(context: Context): Boolean = storageHelper.checkIfKeysetIsUsingKeystore(context)

    private fun ensureKeystoreIsSecureOrFallbackIsAllowedOrThrow(context: Context, allowFallbackToCleartext: Boolean) {
        if (!isUsingKeystore(context) && !allowFallbackToCleartext) {
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

    private fun getPublicKeyOrThrow(context: Context) =
        storageHelper.getEd25519PublicKey(context = context, keyAlias = BIOMETRICS_REGISTRATION_KEY)
            ?: throw StytchExceptions.Input(StytchErrorType.KEY_GENERATION_FAILED.message)

    private fun signChallengeOrThrow(context: Context, challenge: String) =
        storageHelper.signEd25519CodeChallenge(
            context = context,
            challenge = challenge,
            keyAlias = BIOMETRICS_REGISTRATION_KEY,
        ) ?: throw StytchExceptions.Input(StytchErrorType.ERROR_SIGNING_CHALLENGE.message)

    override suspend fun register(parameters: Biometrics.RegisterParameters): BiometricsAuthResponse =
        withContext(dispatchers.io) {
            try {
                ensureKeystoreIsSecureOrFallbackIsAllowedOrThrow(
                    context = parameters.context,
                    allowFallbackToCleartext = parameters.allowFallbackToCleartext
                )
                if (registrationAvailable) {
                    removeRegistration()
                }
                ensureSessionIsValidOrThrow()
                withContext(dispatchers.ui) {
                    biometricsProvider.showBiometricPrompt(parameters.context, parameters.promptInfo)
                }
                val publicKey = getPublicKeyOrThrow(parameters.context)
                val startResponse = api.registerStart(publicKey = publicKey).getValueOrThrow()
                val signature = signChallengeOrThrow(
                    context = parameters.context,
                    challenge = startResponse.challenge
                )
                api.register(
                    signature = signature,
                    biometricRegistrationId = startResponse.biometricRegistrationId,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    storageHelper.saveValue(LAST_USED_BIOMETRIC_REGISTRATION_ID, startResponse.biometricRegistrationId)
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            } catch (e: StytchExceptions) {
                // don't remove the existing registration in case of missing/expired session or existing registration
                when ((e as? StytchExceptions.Input)?.reason) {
                    StytchErrorType.BIOMETRICS_ALREADY_EXISTS.message,
                    StytchErrorType.NO_CURRENT_SESSION.message -> {} // no-op
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
                withContext(dispatchers.ui) {
                    biometricsProvider.showBiometricPrompt(parameters.context, parameters.promptInfo)
                }
                val publicKey = getPublicKeyOrThrow(parameters.context)
                val startResponse = api.authenticateStart(publicKey = publicKey).getValueOrThrow()
                val signature = signChallengeOrThrow(
                    context = parameters.context,
                    challenge = startResponse.challenge
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

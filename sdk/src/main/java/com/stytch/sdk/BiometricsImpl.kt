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

public class BiometricsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val storageHelper: StorageHelper,
    private val api: StytchApi.Biometrics,
    private val biometricsProvider: BiometricsProvider,
) : Biometrics {
    override val registrationAvailable: Boolean
        get() = storageHelper.ed25519KeyExists(keyAlias = BIOMETRICS_REGISTRATION_KEY)

    override fun areBiometricsAvailable(context: FragmentActivity): Pair<Boolean, String> =
        biometricsProvider.areBiometricsAvailable(context)

    override fun removeRegistration(): Boolean = storageHelper.deleteEd25519Key(keyAlias = BIOMETRICS_REGISTRATION_KEY)

    override fun isUsingKeystore(context: Context): Boolean = storageHelper.checkIfKeysetIsUsingKeystore(context)

    override suspend fun register(parameters: Biometrics.StartParameters): BiometricsAuthResponse {
        val result: BiometricsAuthResponse
        withContext(dispatchers.io) {
            if (!isUsingKeystore(parameters.context) && !parameters.allowFallbackToCleartext) {
                removeRegistration()
                result = StytchResult.Error(StytchExceptions.Input(StytchErrorType.NOT_USING_KEYSTORE.message))
                return@withContext
            }
            if (registrationAvailable) {
                result = StytchResult.Error(StytchExceptions.Input(StytchErrorType.BIOMETRICS_ALREADY_EXISTS.message))
                return@withContext
            }
            if (sessionStorage.sessionToken == null && sessionStorage.sessionJwt == null) {
                removeRegistration()
                result = StytchResult.Error(StytchExceptions.Input(StytchErrorType.NO_CURRENT_SESSION.message))
                return@withContext
            }
            try {
                withContext(dispatchers.ui) {
                    biometricsProvider.showBiometricPrompt(parameters.context, parameters.promptInfo)
                }
            } catch (e: StytchExceptions) {
                result = StytchResult.Error(e)
                return@withContext
            } catch (e: Exception) {
                result = StytchResult.Error(StytchExceptions.Critical(e))
                return@withContext
            }
            val publicKey = storageHelper.getEd25519PublicKey(
                context = parameters.context,
                keyAlias = BIOMETRICS_REGISTRATION_KEY,
            ) ?: run {
                removeRegistration()
                result = StytchResult.Error(StytchExceptions.Input(StytchErrorType.KEY_GENERATION_FAILED.message))
                return@withContext
            }
            val startResponse = api.registerStart(publicKey = publicKey)
            if (startResponse is StytchResult.Error) {
                removeRegistration()
                result = startResponse
                return@withContext
            }
            require(startResponse is StytchResult.Success)
            result = storageHelper.signEd25519CodeChallenge(
                context = parameters.context,
                challenge = startResponse.value.challenge,
                keyAlias = BIOMETRICS_REGISTRATION_KEY,
            )?.let { signature ->
                api.register(
                    signature = signature,
                    biometricRegistrationId = startResponse.value.biometricRegistrationId,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            } ?: run {
                removeRegistration()
                StytchResult.Error(StytchExceptions.Input(StytchErrorType.ERROR_SIGNING_CHALLENGE.message))
            }
        }
        return result
    }

    override fun register(
        parameters: Biometrics.StartParameters,
        callback: (response: BiometricsAuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = register(parameters)
            callback(result)
        }
    }

    override suspend fun authenticate(parameters: Biometrics.StartParameters): BiometricsAuthResponse {
        val result: BiometricsAuthResponse
        withContext(dispatchers.io) {
            if (!registrationAvailable) {
                result = StytchResult.Error(
                    StytchExceptions.Input(
                        StytchErrorType.NO_BIOMETRICS_REGISTRATIONS_AVAILABLE.message
                    )
                )
                return@withContext
            }
            try {
                withContext(dispatchers.ui) {
                    biometricsProvider.showBiometricPrompt(parameters.context, parameters.promptInfo)
                }
            } catch (e: StytchExceptions) {
                result = StytchResult.Error(e)
                return@withContext
            } catch (e: Exception) {
                result = StytchResult.Error(StytchExceptions.Critical(e))
                return@withContext
            }
            val publicKey = storageHelper.getEd25519PublicKey(
                context = parameters.context,
                keyAlias = BIOMETRICS_REGISTRATION_KEY,
            ) ?: run {
                result = StytchResult.Error(StytchExceptions.Input(StytchErrorType.KEY_GENERATION_FAILED.message))
                return@withContext
            }
            val startResponse = api.authenticateStart(publicKey = publicKey)
            if (startResponse is StytchResult.Error) {
                result = startResponse
                return@withContext
            }
            require(startResponse is StytchResult.Success)
            result = storageHelper.signEd25519CodeChallenge(
                context = parameters.context,
                challenge = startResponse.value.challenge,
                keyAlias = BIOMETRICS_REGISTRATION_KEY,
            )?.let { signature ->
                api.authenticate(
                    signature = signature,
                    biometricRegistrationId = startResponse.value.biometricRegistrationId,
                    sessionDurationMinutes = parameters.sessionDurationMinutes
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            } ?: StytchResult.Error(StytchExceptions.Input(StytchErrorType.ERROR_SIGNING_CHALLENGE.message))
        }
        return result
    }

    override fun authenticate(
        parameters: Biometrics.StartParameters,
        callback: (response: BiometricsAuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }
}

package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sessions.SessionStorage
import com.stytch.sessions.launchSessionUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal const val ERROR_SIGNING_CHALLENGE = "Failed to sign challenge"

public class BiometricsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val storageHelper: StorageHelper,
    private val api: StytchApi.Biometrics,
) : Biometrics {
    override val registrationAvailable: Boolean
        get() = TODO("Not yet implemented")

    override fun removeRegistration(): Unit = storageHelper.deleteEd25519Key()

    override suspend fun register(parameters: Biometrics.StartParameters): BiometricsAuthResponse {
        val result: BiometricsAuthResponse
        withContext(dispatchers.io) {
            if (sessionStorage.sessionToken == null && sessionStorage.sessionJwt == null) {
                result = StytchResult.Error(StytchExceptions.Input(StytchErrorType.NO_CURRENT_SESSION.message))
                return@withContext
            }
            val publicKey = storageHelper.getEd25519PublicKey()
            if (publicKey == null) {
                result = StytchResult.Error(StytchExceptions.Input(StytchErrorType.KEY_GENERATION_FAILED.message))
                return@withContext
            }
            val startResponse = api.registerStart(publicKey = publicKey)
            if (startResponse is StytchResult.Error) {
                result = startResponse
                return@withContext
            }
            require(startResponse is StytchResult.Success)
            result = storageHelper.signEd25519CodeChallenge(
                challenge = startResponse.value.challenge
            )?.let { signature ->
                api.register(
                    signature = signature,
                    biometricRegistrationId = startResponse.value.biometricRegistrationId,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            } ?: StytchResult.Error(StytchExceptions.Input(ERROR_SIGNING_CHALLENGE))
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
            val publicKey = storageHelper.getEd25519PublicKey()
            if (publicKey == null) {
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
                challenge = startResponse.value.challenge
            )?.let { signature ->
                api.authenticate(
                    signature = signature,
                    biometricRegistrationId = startResponse.value.biometricRegistrationId,
                    sessionDurationMinutes = parameters.sessionDurationMinutes
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            } ?: StytchResult.Error(StytchExceptions.Input(ERROR_SIGNING_CHALLENGE))
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

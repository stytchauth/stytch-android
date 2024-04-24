package com.stytch.sdk.consumer.oauth

import android.app.Activity
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchLog
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.errors.UnexpectedCredentialType
import com.stytch.sdk.consumer.NativeOAuthResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal interface GoogleCredentialManagerProvider {
    suspend fun getSignInWithGoogleCredential(
        activity: Activity,
        dispatchers: StytchDispatchers,
        clientId: String,
        autoSelectEnabled: Boolean,
        nonce: String,
    ): GetCredentialResponse

    fun createTokenCredential(credentialData: Bundle): GoogleIdTokenCredential
}

internal class GoogleCredentialManagerProviderImpl : GoogleCredentialManagerProvider {
    override suspend fun getSignInWithGoogleCredential(
        activity: Activity,
        dispatchers: StytchDispatchers,
        clientId: String,
        autoSelectEnabled: Boolean,
        nonce: String,
    ): GetCredentialResponse {
        val credentialManager = CredentialManager.create(activity)
        val option: GetGoogleIdOption =
            GetGoogleIdOption
                .Builder()
                .setServerClientId(clientId)
                .setNonce(nonce)
                .setAutoSelectEnabled(autoSelectEnabled)
                .build()
        val request: GetCredentialRequest =
            GetCredentialRequest
                .Builder()
                .addCredentialOption(option)
                .setPreferImmediatelyAvailableCredentials(true)
                .build()
        return withContext(dispatchers.ui) {
            credentialManager.getCredential(activity, request)
        }
    }

    override fun createTokenCredential(credentialData: Bundle): GoogleIdTokenCredential =
        GoogleIdTokenCredential.createFrom(credentialData)
}

internal class GoogleOneTapImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.OAuth,
    private val credentialManagerProvider: GoogleCredentialManagerProvider = GoogleCredentialManagerProviderImpl(),
) : OAuth.GoogleOneTap {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal lateinit var nonce: String

    override suspend fun start(parameters: OAuth.GoogleOneTap.StartParameters): NativeOAuthResponse {
        return try {
            withContext(dispatchers.io) {
                nonce = EncryptionManager.encryptCodeChallenge(EncryptionManager.generateCodeChallenge())
                val credentialResponse =
                    credentialManagerProvider.getSignInWithGoogleCredential(
                        activity = parameters.context,
                        dispatchers = dispatchers,
                        clientId = parameters.clientId,
                        autoSelectEnabled = parameters.autoSelectEnabled,
                        nonce = nonce,
                    )
                val credential = credentialResponse.credential
                if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    return@withContext StytchResult.Error(UnexpectedCredentialType(credentialType = credential.type))
                }
                val googleIdTokenCredential = credentialManagerProvider.createTokenCredential(credential.data)
                return@withContext api.authenticateWithGoogleIdToken(
                    idToken = googleIdTokenCredential.idToken,
                    nonce = nonce,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            }
        } catch (e: GoogleIdTokenParsingException) {
            StytchLog.e("Received an invalid google id token response: $e")
            StytchResult.Error(StytchInternalError(e))
        } catch (e: Exception) {
            StytchResult.Error(StytchInternalError(e))
        }
    }

    override fun start(
        parameters: OAuth.GoogleOneTap.StartParameters,
        callback: (NativeOAuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = start(parameters)
            callback(result)
        }
    }

    override fun signOut(activity: Activity) {
        externalScope.launch(dispatchers.io) {
            val credentialManager = CredentialManager.create(activity)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }
    }
}

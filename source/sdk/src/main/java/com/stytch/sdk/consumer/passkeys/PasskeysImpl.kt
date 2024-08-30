package com.stytch.sdk.consumer.passkeys

import android.app.Activity
import android.os.Build
import android.webkit.WebSettings
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.errors.StytchPasskeysNotSupportedError
import com.stytch.sdk.common.getValueOrThrow
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.WebAuthnRegisterResponse
import com.stytch.sdk.consumer.WebAuthnUpdateResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.models.WebAuthnAuthenticateStartData
import com.stytch.sdk.consumer.network.models.WebAuthnRegisterStartData
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal interface PasskeysProvider {
    suspend fun createPublicKeyCredential(
        startResponse: WebAuthnRegisterStartData,
        activity: Activity,
        dispatchers: StytchDispatchers,
    ): CreatePublicKeyCredentialResponse

    suspend fun getPublicKeyCredential(
        startResponse: WebAuthnAuthenticateStartData,
        activity: Activity,
        dispatchers: StytchDispatchers,
    ): PublicKeyCredential
}

private class PasskeysProviderImpl : PasskeysProvider {
    override suspend fun createPublicKeyCredential(
        startResponse: WebAuthnRegisterStartData,
        activity: Activity,
        dispatchers: StytchDispatchers,
    ): CreatePublicKeyCredentialResponse {
        val createPublicKeyCredentialRequest =
            CreatePublicKeyCredentialRequest(
                requestJson = startResponse.publicKeyCredentialCreationOptions,
                preferImmediatelyAvailableCredentials = true,
            )
        val credentialManager = CredentialManager.create(activity)
        return withContext(dispatchers.ui) {
            credentialManager.createCredential(
                context = activity,
                request = createPublicKeyCredentialRequest,
            )
        } as CreatePublicKeyCredentialResponse
        // if this is not a CreatePublicKeyCredentialResponse, it will error and be caught in the calling class
    }

    override suspend fun getPublicKeyCredential(
        startResponse: WebAuthnAuthenticateStartData,
        activity: Activity,
        dispatchers: StytchDispatchers,
    ): PublicKeyCredential {
        val getPublicKeyCredentialOption =
            GetPublicKeyCredentialOption(
                requestJson = startResponse.publicKeyCredentialRequestOptions,
            )
        val credentialManager = CredentialManager.create(activity)
        return withContext(dispatchers.ui) {
            credentialManager.getCredential(
                context = activity,
                request = GetCredentialRequest(listOf(getPublicKeyCredentialOption)),
            )
        }.credential as PublicKeyCredential
        // if credential is not a PublicKeyCredential, it will error and be caught in the calling class
    }
}

internal class PasskeysImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.WebAuthn,
    private val provider: PasskeysProvider = PasskeysProviderImpl(),
) : Passkeys {
    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    override val isSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    override suspend fun register(parameters: Passkeys.RegisterParameters): WebAuthnRegisterResponse {
        if (!isSupported) return StytchResult.Error(StytchPasskeysNotSupportedError())
        return try {
            withContext(dispatchers.io) {
                val startResponse =
                    api
                        .registerStart(
                            domain = parameters.domain,
                            userAgent = WebSettings.getDefaultUserAgent(parameters.activity),
                            authenticatorType = "platform",
                            isPasskey = true,
                        ).getValueOrThrow()
                val credentialResponse =
                    provider.createPublicKeyCredential(
                        startResponse = startResponse,
                        activity = parameters.activity,
                        dispatchers = dispatchers,
                    )
                api
                    .register(
                        publicKeyCredential = credentialResponse.registrationResponseJson,
                    ).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
            }
        } catch (e: Exception) {
            StytchResult.Error(StytchInternalError(e))
        }
    }

    override fun register(
        parameters: Passkeys.RegisterParameters,
        callback: (response: WebAuthnRegisterResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = register(parameters)
            callback(result)
        }
    }

    override suspend fun authenticate(parameters: Passkeys.AuthenticateParameters): AuthResponse {
        if (!isSupported) return StytchResult.Error(StytchPasskeysNotSupportedError())
        return try {
            withContext(dispatchers.io) {
                val startResponse =
                    if (sessionStorage.persistedSessionIdentifiersExist) {
                        api
                            .authenticateStartSecondary(
                                domain = parameters.domain,
                                isPasskey = true,
                            ).getValueOrThrow()
                    } else {
                        api
                            .authenticateStartPrimary(
                                domain = parameters.domain,
                                isPasskey = true,
                            ).getValueOrThrow()
                    }
                val credentialResponse =
                    provider.getPublicKeyCredential(
                        startResponse = startResponse,
                        activity = parameters.activity,
                        dispatchers = dispatchers,
                    )
                api
                    .authenticate(
                        publicKeyCredential = credentialResponse.authenticationResponseJson,
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
                    ).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
            }
        } catch (e: Exception) {
            StytchResult.Error(StytchInternalError(e))
        }
    }

    override fun authenticate(
        parameters: Passkeys.AuthenticateParameters,
        callback: (response: AuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    override suspend fun update(parameters: Passkeys.UpdateParameters): WebAuthnUpdateResponse =
        withContext(dispatchers.io) {
            api.update(
                id = parameters.id,
                name = parameters.name,
            )
        }

    override fun update(
        parameters: Passkeys.UpdateParameters,
        callback: (response: WebAuthnUpdateResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = update(parameters)
            callback(result)
        }
    }
}

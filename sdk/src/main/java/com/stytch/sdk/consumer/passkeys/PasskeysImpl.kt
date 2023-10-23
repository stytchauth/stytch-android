package com.stytch.sdk.consumer.passkeys

import android.os.Build
import android.webkit.WebSettings
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.getValueOrThrow
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.WebAuthnRegisterResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PasskeysImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.WebAuthn,
) : Passkeys {
    private val moshi = Moshi.Builder().build()
    private val type = Types.newParameterizedType(
        MutableMap::class.java,
        String::class.java,
        Any::class.java
    )
    private val adapter: JsonAdapter<Map<String, Any>> = moshi.adapter(type)

    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    override val isSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    override suspend fun register(parameters: Passkeys.RegisterParameters): WebAuthnRegisterResponse {
        if (!isSupported) return StytchResult.Error(StytchExceptions.Input("Passkeys are not supported"))
        return try {
            withContext(dispatchers.io) {
                val startResponse = api.registerStart(
                    domain = parameters.domain,
                    userAgent = WebSettings.getDefaultUserAgent(parameters.activity),
                    authenticatorType = "platform",
                    isPasskey = true,
                ).getValueOrThrow()
                val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
                    requestJson = startResponse.publicKeyCredentialCreationOptions,
                    preferImmediatelyAvailableCredentials = true,
                )
                val credentialManager = CredentialManager.create(parameters.activity)
                val credentialResponse = withContext(dispatchers.ui) {
                    credentialManager.createCredential(
                        context = parameters.activity,
                        request = createPublicKeyCredentialRequest,
                    )
                }
                with(credentialResponse as CreatePublicKeyCredentialResponse) {
                    api.register(
                        publicKeyCredential = this.registrationResponseJson
                    ).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
                }
            }
        } catch (e: Exception) {
            StytchResult.Error(StytchExceptions.Critical(e))
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
        if (!isSupported) return StytchResult.Error(StytchExceptions.Input("Passkeys are not supported"))
        return try {
            withContext(dispatchers.io) {
                val startResponse = if (sessionStorage.activeSessionExists) {
                    api.authenticateStartSecondary(
                        domain = parameters.domain,
                        isPasskey = true
                    ).getValueOrThrow()
                } else {
                    api.authenticateStartPrimary(
                        domain = parameters.domain,
                        isPasskey = true
                    ).getValueOrThrow()
                }
                val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
                    requestJson = startResponse.publicKeyCredentialRequestOptions
                )
                val credentialManager = CredentialManager.create(parameters.activity)
                val credentialResponse = withContext(dispatchers.ui) {
                    val result = credentialManager.getCredential(
                        context = parameters.activity,
                        request = GetCredentialRequest(listOf(getPublicKeyCredentialOption)),
                    )
                    result
                }
                with(credentialResponse.credential as PublicKeyCredential) {
                    api.authenticate(
                        publicKeyCredential = this.authenticationResponseJson,
                        sessionDurationMinutes = parameters.sessionDurationMinutes
                    ).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
                }
            }
        } catch (e: Exception) {
            StytchResult.Error(StytchExceptions.Critical(e))
        }
    }

    override fun authenticate(parameters: Passkeys.AuthenticateParameters, callback: (response: AuthResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }
}

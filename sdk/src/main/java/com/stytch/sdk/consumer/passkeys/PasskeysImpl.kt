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
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
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
        return withContext(dispatchers.io) {
            println("JORDAN 1")
            val startResponse = api.registerStart(
                domain = parameters.domain ?: parameters.context.packageName,
                userAgent = WebSettings.getDefaultUserAgent(parameters.context),
                authenticatorType = "platform",
                isPasskey = true,
            ).getValueOrThrow()
            println("JORDAN 2")
            println(startResponse)
            val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
                requestJson = startResponse.publicKeyCredentialCreationOptions,
                preferImmediatelyAvailableCredentials = true,
            )
            println("JORDAN 3")
            val credentialManager = CredentialManager.create(parameters.context)
            println("JORDAN 4")
            val credentialResponse = withContext(dispatchers.ui) {
                try {
                    println("JORDAN 5")
                    val result = credentialManager.createCredential(
                        context = parameters.context,
                        request = createPublicKeyCredentialRequest,
                    )
                    println("JORDAN 6")
                    result
                } catch (e: CreateCredentialException) {
                    throw e // TODO: handle this
                }
            }
            println("JORDAN 7")
            println(credentialResponse)
            println("JORDAN 8")
            with(credentialResponse as CreatePublicKeyCredentialResponse) {
                api.register(
                    publicKeyCredential = this.registrationResponseJson
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            }
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
        return withContext(dispatchers.io) {
            val startResponse = api.authenticateStart(
                domain = parameters.domain ?: parameters.context.packageName,
            ).getValueOrThrow()
            val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
                requestJson = startResponse.publicKeyCredentialCreationOptions
            )
            val credentialManager = CredentialManager.create(parameters.context)
            val credentialResponse = withContext(dispatchers.ui) {
                try {
                    val result = credentialManager.getCredential(
                        context = parameters.context,
                        request = GetCredentialRequest(listOf(getPublicKeyCredentialOption)),
                    )
                    result
                } catch (e: GetCredentialException) {
                    throw e // TODO: handle this
                }
            }
            with(credentialResponse.credential as PublicKeyCredential) {
                val map: Map<String, Any> = adapter.fromJson(this.authenticationResponseJson) ?: emptyMap()
                api.authenticate(
                    publicKeyCredential = map
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            }
        }
    }

    override fun authenticate(parameters: Passkeys.AuthenticateParameters, callback: (response: AuthResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }
}

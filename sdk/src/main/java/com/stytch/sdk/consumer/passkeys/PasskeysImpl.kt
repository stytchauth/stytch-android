package com.stytch.sdk.consumer.passkeys

import android.os.Build
import android.webkit.WebSettings
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.CreateCredentialException
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.getValueOrThrow
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.userAgent

internal class PasskeysImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.WebAuthn,
) : Passkeys {

    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    override val isSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    override suspend fun register(parameters: Passkeys.RegisterParameters): BaseResponse {
        if (!isSupported) return StytchResult.Error(StytchExceptions.Input("Passkeys are not supported"))
        return withContext(dispatchers.io) {
            val startResponse = api.registerStart(
                userId = parameters.userId,
                domain = parameters.context.packageName,
                userAgent = WebSettings.getDefaultUserAgent(parameters.context),
                authenticatorType = "platform",
                isPasskey = true,
            ).getValueOrThrow()
            println(startResponse.publicKeyCredentialCreationOptions)
            val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
                requestJson = startResponse.publicKeyCredentialCreationOptions,
                preferImmediatelyAvailableCredentials = true,
            )
            val credentialManager = CredentialManager.create(parameters.context)
            val credentialResponse = withContext(dispatchers.ui) createCredential@{
                try {
                    val result = credentialManager.createCredential(
                        context = parameters.context,
                        request = createPublicKeyCredentialRequest,
                    )
                    result
                } catch (e: CreateCredentialException) {
                    throw e // TODO: handle this
                }
            }
            println(credentialResponse)
            StytchResult.Error(StytchExceptions.Input("Testing"))
        }
    }

    override fun register(parameters: Passkeys.RegisterParameters, callback: (response: BaseResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = register(parameters)
            callback(result)
        }
    }

    override suspend fun authenticate(parameters: Passkeys.AuthenticateParameters): AuthResponse {
        if (!isSupported) return StytchResult.Error(StytchExceptions.Input("Passkeys are not supported"))
        return StytchResult.Error(StytchExceptions.Input("Testing"))
    }

    override fun authenticate(parameters: Passkeys.AuthenticateParameters, callback: (response: AuthResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }
}

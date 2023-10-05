package com.stytch.sdk.consumer.passkeys

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PasskeysImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.WebAuthn,
) : Passkeys {

    override val isSupported: Boolean
        get() = TODO("Not yet implemented")

    override suspend fun register(parameters: Passkeys.RegisterParameters): BaseResponse {
        TODO("Not yet implemented")
    }

    override fun register(parameters: Passkeys.RegisterParameters, callback: (response: BaseResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = register(parameters)
            callback(result)
        }
    }

    override suspend fun authenticate(parameters: Passkeys.AuthenticateParameters): AuthResponse {
        TODO("Not yet implemented")
    }

    override fun authenticate(parameters: Passkeys.AuthenticateParameters, callback: (response: AuthResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }
}
package com.stytch.sdk.consumer.passkeys

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.consumer.AuthResponse

public interface Passkeys {

    public data class RegisterParameters(
        val userId: String,
        val domain: String,
        val userAgent: String? = null,
    )

    public data class AuthenticateParameters(
        val domain: String,
    )

    public val isSupported: Boolean

    public suspend fun register(parameters: RegisterParameters): BaseResponse

    public fun register(
        parameters: RegisterParameters,
        callback: (response: BaseResponse) -> Unit
    )

    public suspend fun authenticate(parameters: AuthenticateParameters): AuthResponse

    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (response: AuthResponse) -> Unit,
    )
}

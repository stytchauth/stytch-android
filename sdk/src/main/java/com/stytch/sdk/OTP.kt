package com.stytch.sdk

public interface OTP {

    public data class AuthParameters(
        val token: String,
        val sessionDurationInMinutes: UInt = 60u,
    )

    public data class PhoneParameters(
        val phoneNumber: String,
        val expirationInMinutes: UInt = 60u,
    )

    public data class EmailParameters(
        val email: String,
        val expirationInMinutes: UInt = 60u,
    )

    public suspend fun authenticate(
        authParams: AuthParameters,
    ): BaseResponse

    public fun authenticate(
        authParams: AuthParameters,
        callback: (response: BaseResponse) -> Unit,
    )

    public suspend fun loginOrCreateUserWithSMS(params: PhoneParameters): BaseResponse

    public fun loginOrCreateUserWithSMS(
        parameters: PhoneParameters,
        callback: (response: BaseResponse) -> Unit,
    )

    public suspend fun loginOrCreateUserWithWhatsapp(parameters: PhoneParameters): BaseResponse

    public fun loginOrCreateUserWithWhatsapp(
        parameters: PhoneParameters,
        callback: (response: BaseResponse) -> Unit,
    )

    public suspend fun loginOrCreateUserWithEmail(parameters: EmailParameters): BaseResponse

    public fun loginOrCreateUserWithEmail(
        parameters: EmailParameters,
        callback: (response: BaseResponse) -> Unit,
    )
}

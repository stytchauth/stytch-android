package com.stytch.sdk

public interface MagicLinks {
    public data class Parameters(
        val email: String,
        val loginMagicLinkUrl: String? = null,
        val loginExpirationInMinutes: Int? = null,
        val signupMagicLinkUrl: String? = null,
        val signupExpirationInMinutes: Int? = null,
    )

    public suspend fun loginOrCreate(parameters: Parameters): LoginOrCreateUserByEmailResponse

    public fun loginOrCreate(
        parameters: Parameters,
        callback: (response: LoginOrCreateUserByEmailResponse) -> Unit,
    )

    public suspend fun authenticate(token: String, sessionExpirationMinutes: Int = 60): BaseResponse

    public fun authenticate(
        token: String,
        sessionExpirationMinutes: Int = 60,
        callback: (response: BaseResponse) -> Unit,
    )

}
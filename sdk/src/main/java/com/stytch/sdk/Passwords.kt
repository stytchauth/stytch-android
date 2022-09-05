package com.stytch.sdk

public interface Passwords {

    public data class AuthParameters(
        val email: String,
        val password: String,
        val sessionDurationMinutes: Int,
    )

    public data class CreateParameters(
        val email: String,
        val password: String,
        val sessionDurationMinutes: Int,
    )

    public data class ResetByEmailStartParameters(
        val email: String,
        val codeChallenge: String,
        val codeChallengeMethod: String,
        val loginRedirectUrl: String?,
        val loginExpirationMinutes: Int?,
        val resetPasswordRedirectUrl: String?,
        val resetPasswordExpirationMinutes: Int?,
    )

    public data class ResetByEmailParameters(
        val token: String,
        val password: String,
        val sessionDurationMinutes: Int,
        val codeVerifier: String,
    )

    public data class StrengthCheckParameters(
        val email: String?,
        val password: String,
    )

    public suspend fun authenticate(
        parameters: AuthParameters,
    ): AuthResponse

    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    public suspend fun create(
        parameters: CreateParameters,
    ): PasswordsCreateResponse

    public fun create(
        parameters: CreateParameters,
        callback: (response: PasswordsCreateResponse) -> Unit,
    )

    public suspend fun resetByEmailStart(
        parameters: ResetByEmailStartParameters,
    ): BaseResponse

    public fun resetByEmailStart(
        parameters: ResetByEmailStartParameters,
        callback: (response: BaseResponse) -> Unit,
    )

    public suspend fun resetByEmail(
        parameters: ResetByEmailParameters,
    ): AuthResponse

    public fun resetByEmail(
        parameters: ResetByEmailParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    public suspend fun strengthCheck(
        parameters: StrengthCheckParameters,
    ): PasswordsStrengthCheckResponse

    public fun strengthCheck(
        parameters: StrengthCheckParameters,
        callback: (response: PasswordsStrengthCheckResponse) -> Unit,
    )

}

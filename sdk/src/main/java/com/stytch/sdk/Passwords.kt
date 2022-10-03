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
        val loginRedirectUrl: String? = null,
        val loginExpirationMinutes: Int? = null,
        val resetPasswordRedirectUrl: String? = null,
        val resetPasswordExpirationMinutes: Int? = null,
    )

    public data class ResetByEmailParameters(
        val token: String,
        val password: String,
        val sessionDurationMinutes: Int,
    )

    public data class StrengthCheckParameters(
        val email: String?,
        val password: String,
    )

    /**
     * Wraps the passwords authenticate API endpoint which validates the passwords token passed in. If this method succeeds, the user will be logged in, granted an active session
     * @param parameters required to authenticate
     * @return AuthResponse response from backend
     */
    public suspend fun authenticate(
        parameters: AuthParameters,
    ): AuthResponse

    /**
     * Wraps the passwords authenticate API endpoint which validates the passwords token passed in. If this method succeeds, the user will be logged in, granted an active session
     * @param parameters required to authenticate
     * @param callback calls callback with AuthResponse response from backend
     */
    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    /**
     * Wraps Stytch’s passwords create endpoint. Creates an account using an email and password given that an account with such an email does not already exist.
     * @param parameters required to create an account
     * @return PasswordsCreateResponse response from backend
     */
    public suspend fun create(
        parameters: CreateParameters,
    ): PasswordsCreateResponse

    /**
     * Wraps Stytch’s passwords create endpoint. Creates an account using an email and password given that an account with such an email does not already exist.
     * @param parameters required to create an account
     * @param callback calls callback with PasswordsCreateResponse response from backend
     */
    public fun create(
        parameters: CreateParameters,
        callback: (response: PasswordsCreateResponse) -> Unit,
    )

    /**
     * Wraps Stytch’s resetByEmailStart endpoint. Initiates an account password reset by using an email.
     * @param parameters required to reset an account password
     * @return BaseResponse response from backend
     */
    public suspend fun resetByEmailStart(
        parameters: ResetByEmailStartParameters,
    ): BaseResponse

    /**
     * Wraps Stytch’s resetByEmailStart endpoint. Initiates an account password reset by using an email.
     * @param parameters required to reset an account password
     * @param callback calls callback with BaseResponse response from backend
     */
    public fun resetByEmailStart(
        parameters: ResetByEmailStartParameters,
        callback: (response: BaseResponse) -> Unit,
    )

    /**
     * Wraps Stytch’s resetByEmail endpoint. Resets an account password by using a token and password.
     * @param parameters required to reset an account password
     * @return AuthResponse response from backend
     */
    public suspend fun resetByEmail(
        parameters: ResetByEmailParameters,
    ): AuthResponse

    /**
     * Wraps Stytch’s resetByEmail endpoint. Resets an account password by using a token and password.
     * @param parameters required to reset an account password
     * @param callback calls callback with AuthResponse response from backend
     */
    public fun resetByEmail(
        parameters: ResetByEmailParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    /**
     * Wraps Stytch’s strengthCheck endpoint. Advises on password strength when provided an email and a password.
     * @param parameters required to advise on password strength
     * @return PasswordsStrengthCheckResponse response from backend
     */
    public suspend fun strengthCheck(
        parameters: StrengthCheckParameters,
    ): PasswordsStrengthCheckResponse

    /**
     * Wraps Stytch’s strengthCheck endpoint. Advises on password strength when provided an email and a password.
     * @param parameters required to advise on password strength
     * @param callback calls callback with PasswordsStrengthCheckResponse response from backend
     */
    public fun strengthCheck(
        parameters: StrengthCheckParameters,
        callback: (response: PasswordsStrengthCheckResponse) -> Unit,
    )

}

package com.stytch.sdk.consumer.passwords

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.Constants.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.PasswordsCreateResponse
import com.stytch.sdk.consumer.PasswordsStrengthCheckResponse

/**
 * The Passwords interface provides methods for authenticating, creating, resetting, and performing strength checks of
 * passwords.
 */
public interface Passwords {

    /**
     * Data class used for wrapping parameters used with Passwords authentication
     * @property email is the account identifier for the account in the form of an Email address
     * @property password is your private sequence of characters to authenticate
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthParameters(
        val email: String,
        val password: String,
        val sessionDurationMinutes: UInt = DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Data class used for wrapping parameters used with Passwords create endpoint
     * @property email is the account identifier for the account in the form of an Email address that you wish to use
     * for account creation
     * @property password is your private sequence of characters you wish to use when authenticating with the newly
     * created account in the future
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class CreateParameters(
        val email: String,
        val password: String,
        val sessionDurationMinutes: UInt = DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Data class used for wrapping parameters used with Passwords ResetByEmailStart endpoint
     * @property email is the account identifier for the account in the form of an Email address to identify which
     * account's password you wish to start resetting
     * @property loginRedirectUrl is the url where you should be redirected after a login
     * @property loginExpirationMinutes is the duration after which the login should expire
     * @property resetPasswordRedirectUrl is the url where you should be redirected after a reset
     * @property resetPasswordExpirationMinutes is the duration after which a reset password request should expire
     * @property resetPasswordTemplateId Use a custom template for password reset emails. By default, it will use your
     * default email template. The template must be a template using our built-in customizations or a custom HTML email
     * for Password Reset.
     */
    public data class ResetByEmailStartParameters(
        val email: String,
        val loginRedirectUrl: String? = null,
        val loginExpirationMinutes: UInt? = null,
        val resetPasswordRedirectUrl: String? = null,
        val resetPasswordExpirationMinutes: UInt? = null,
        val resetPasswordTemplateId: String? = null
    )

    /**
     * Data class used for wrapping parameters used with Passwords ResetByEmail endpoint
     * @property token is the unique sequence of characters that should be received after calling the resetByEmailStart
     * @property password is the private sequence of characters you wish to use as a password
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class ResetByEmailParameters(
        val token: String,
        val password: String,
        val sessionDurationMinutes: UInt = DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Data class used for wrapping parameters used with Passwords StrengthCheck endpoint
     * @property email is the account identifier for the account in the form of an Email address that you wish to use to
     * initiate a password strength check
     * @property password is the private sequence of characters you wish to check to get advice on improving it
     */
    public data class StrengthCheckParameters(
        val email: String?,
        val password: String,
    )

    /**
     * Authenticate a user with their email address and password. This endpoint verifies that the user has a password
     * currently set, and that the entered password is correct.
     * @param parameters required to authenticate
     * @return [AuthResponse]
     */
    public suspend fun authenticate(parameters: AuthParameters): AuthResponse

    /**
     * Authenticate a user with their email address and password. This endpoint verifies that the user has a password
     * currently set, and that the entered password is correct.
     * @param parameters required to authenticate
     * @param callback a callback that receives an [AuthResponse]
     */
    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    /**
     * Create a new user with a password and an authenticated session for the user if requested. If a user with this
     * email already exists in the project, this method will return an error.
     * @param parameters required to create an account
     * @return [PasswordsCreateResponse]
     */
    public suspend fun create(parameters: CreateParameters): PasswordsCreateResponse

    /**
     * Create a new user with a password and an authenticated session for the user if requested. If a user with this
     * email already exists in the project, this method will return an error.
     * @param parameters required to create an account
     * @param callback a callback that receives a [PasswordsCreateResponse]
     */
    public fun create(
        parameters: CreateParameters,
        callback: (response: PasswordsCreateResponse) -> Unit,
    )

    /**
     * Initiates a password reset for the email address provided. This will trigger an email to be sent to the address,
     * containing a magic link that will allow them to set a new password and authenticate.
     * @param parameters required to reset an account password
     * @return [BaseResponse]
     */
    public suspend fun resetByEmailStart(parameters: ResetByEmailStartParameters): BaseResponse

    /**
     * Initiates a password reset for the email address provided. This will trigger an email to be sent to the address,
     * containing a magic link that will allow them to set a new password and authenticate.
     * @param parameters required to reset an account password
     * @param callback a callback that receives a [BaseResponse]
     */
    public fun resetByEmailStart(
        parameters: ResetByEmailStartParameters,
        callback: (response: BaseResponse) -> Unit,
    )

    /**
     * Reset the user’s password and authenticate them. This endpoint checks that the magic link token is valid, hasn’t
     * expired, or already been used. The provided password needs to meet our password strength requirements, which can
     * be checked in advance with the [strengthCheck] method. If the token and password are accepted, the password is
     * securely stored for future authentication and the user is authenticated.
     * @param parameters required to reset an account password
     * @return [AuthResponse]
     */
    public suspend fun resetByEmail(parameters: ResetByEmailParameters): AuthResponse

    /**
     * Reset the user’s password and authenticate them. This endpoint checks that the magic link token is valid, hasn’t
     * expired, or already been used. The provided password needs to meet our password strength requirements, which can
     * be checked in advance with the [strengthCheck] method. If the token and password are accepted, the password is
     * securely stored for future authentication and the user is authenticated.
     * @param parameters required to reset an account password
     * @param callback a callback that receives an [AuthResponse]
     */
    public fun resetByEmail(
        parameters: ResetByEmailParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    /**
     * This method allows you to check whether or not the user’s provided password is valid, and to provide feedback to
     * the user on how to increase the strength of their password.
     * @param parameters required to advise on password strength
     * @return [PasswordsStrengthCheckResponse]
     */
    public suspend fun strengthCheck(parameters: StrengthCheckParameters): PasswordsStrengthCheckResponse

    /**
     * This method allows you to check whether or not the user’s provided password is valid, and to provide feedback to
     * the user on how to increase the strength of their password.
     * @param parameters required to advise on password strength
     * @param callback a callback that receives a [PasswordsStrengthCheckResponse]
     */
    public fun strengthCheck(
        parameters: StrengthCheckParameters,
        callback: (response: PasswordsStrengthCheckResponse) -> Unit,
    )
}

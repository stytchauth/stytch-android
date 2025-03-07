package com.stytch.sdk.consumer.passwords

import android.os.Parcelable
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.PasswordsCreateResponse
import com.stytch.sdk.consumer.PasswordsStrengthCheckResponse
import kotlinx.parcelize.Parcelize
import java.util.concurrent.CompletableFuture

/**
 * The Passwords interface provides methods for authenticating, creating, resetting, and performing strength checks of
 * passwords.
 *
 * Stytch supports creating, storing, and authenticating passwords, as well as support for account recovery
 * (password reset) and account deduplication with passwordless login methods.
 *
 * Our implementation of passwords has built-in breach detection powered by [HaveIBeenPwned](https://haveibeenpwned.com)
 * on both sign-up and login, to prevent the use of compromised credentials and uses configurable strength requirements
 * (either Dropbox’s [zxcvbn](https://github.com/dropbox/zxcvbn) or adjustable LUDS) to guide members towards creating
 * passwords that are easy for humans to remember but difficult for computers to crack.
 */
public interface Passwords {
    /**
     * Data class used for wrapping parameters used with Passwords authentication
     * @property email is the account identifier for the account in the form of an Email address
     * @property password is your private sequence of characters to authenticate
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthParameters
        @JvmOverloads
        constructor(
            val email: String,
            val password: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
        )

    /**
     * Data class used for wrapping parameters used with Passwords create endpoint
     * @property email is the account identifier for the account in the form of an Email address that you wish to use
     * for account creation
     * @property password is your private sequence of characters you wish to use when authenticating with the newly
     * created account in the future
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class CreateParameters
        @JvmOverloads
        constructor(
            val email: String,
            val password: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
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
    @Parcelize
    public data class ResetByEmailStartParameters
        @JvmOverloads
        constructor(
            val email: String,
            val loginRedirectUrl: String? = null,
            val loginExpirationMinutes: Int? = null,
            val resetPasswordRedirectUrl: String? = null,
            val resetPasswordExpirationMinutes: Int? = null,
            val resetPasswordTemplateId: String? = null,
            val locale: Locale? = null,
        ) : Parcelable

    /**
     * Data class used for wrapping parameters used with Passwords ResetByEmail endpoint
     * @property token is the unique sequence of characters that should be received after calling the resetByEmailStart
     * @property password is the private sequence of characters you wish to use as a password
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class ResetByEmailParameters
        @JvmOverloads
        constructor(
            val token: String,
            val password: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
            val locale: Locale? = null,
        )

    /**
     * Data class used for wrapping parameters used with Passwords StrengthCheck endpoint
     * @property password is the new password to set
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class ResetBySessionParameters
        @JvmOverloads
        constructor(
            val password: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
            val locale: Locale? = null,
        )

    /**
     * Data class used for wrapping parameters used with Passwords StrengthCheck endpoint
     * @property email is the account identifier for the account in the form of an Email address to identify which
     * account's password you wish to start resetting
     * @property existingPassword The user's existing password.
     * @property newPassword The new password for the user.
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class ResetByExistingPasswordParameters
        @JvmOverloads
        constructor(
            val email: String,
            val existingPassword: String,
            val newPassword: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
        )

    /**
     * Reset the user’s password and authenticate them. This endpoint checks that the session is valid and hasn’t
     * expired or been revoked. The provided password needs to meet our password strength requirements, which can be
     * checked in advance with the password strength endpoint. If the password and accompanying parameters are accepted,
     * the password is securely stored for future authentication and the user is authenticated.
     * @param parameters required to reset a user's password
     * @return [AuthResponse]
     */
    public suspend fun resetBySession(parameters: ResetBySessionParameters): AuthResponse

    /**
     * Reset the user’s password and authenticate them. This endpoint checks that the session is valid and hasn’t
     * expired or been revoked. The provided password needs to meet our password strength requirements, which can be
     * checked in advance with the password strength endpoint. If the password and accompanying parameters are accepted,
     * the password is securely stored for future authentication and the user is authenticated.
     * @param parameters required to reset a user's password
     * @param callback a callback that receives an [AuthResponse]
     */
    public fun resetBySession(
        parameters: ResetBySessionParameters,
        callback: (AuthResponse) -> Unit,
    )

    /**
     * Reset the user’s password and authenticate them. This endpoint checks that the session is valid and hasn’t
     * expired or been revoked. The provided password needs to meet our password strength requirements, which can be
     * checked in advance with the password strength endpoint. If the password and accompanying parameters are accepted,
     * the password is securely stored for future authentication and the user is authenticated.
     * @param parameters required to reset a user's password
     * @return [AuthResponse]
     */
    public fun resetBySessionCompletable(parameters: ResetBySessionParameters): CompletableFuture<AuthResponse>

    /**
     * Data class used for wrapping parameters used with Passwords StrengthCheck endpoint
     * @property email is the account identifier for the account in the form of an Email address that you wish to use to
     * initiate a password strength check
     * @property password is the private sequence of characters you wish to check to get advice on improving it
     */
    public data class StrengthCheckParameters
        @JvmOverloads
        constructor(
            val email: String? = null,
            val password: String,
        )

    /**
     * Authenticate a user with their email address and password. This endpoint verifies that the user has a password
     * currently set, and that the entered password is correct.
     *
     * There are two instances where the endpoint will return a reset_password error even if they enter their previous
     * password:
     * 1. The member's credentials appeared in the HaveIBeenPwned dataset. We force a password reset to ensure that the
     * member is the legitimate owner of the email address, and not a malicious actor abusing the compromised
     * credentials.
     * 2. The member used email based authentication (e.g. Magic Links, Google OAuth) for the first time, and had not
     * previously verified their email address for password based login. We force a password reset in this instance in
     * order to safely deduplicate the account by email address, without introducing the risk of a pre-hijack
     * account-takeover attack.
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
     * Authenticate a user with their email address and password. This endpoint verifies that the user has a password
     * currently set, and that the entered password is correct.
     *
     * There are two instances where the endpoint will return a reset_password error even if they enter their previous
     * password:
     * 1. The member's credentials appeared in the HaveIBeenPwned dataset. We force a password reset to ensure that the
     * member is the legitimate owner of the email address, and not a malicious actor abusing the compromised
     * credentials.
     * 2. The member used email based authentication (e.g. Magic Links, Google OAuth) for the first time, and had not
     * previously verified their email address for password based login. We force a password reset in this instance in
     * order to safely deduplicate the account by email address, without introducing the risk of a pre-hijack
     * account-takeover attack.
     * @param parameters required to authenticate
     * @return [AuthResponse]
     */
    public fun authenticateCompletable(parameters: AuthParameters): CompletableFuture<AuthResponse>

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
     * Create a new user with a password and an authenticated session for the user if requested. If a user with this
     * email already exists in the project, this method will return an error.
     * @param parameters required to create an account
     * @return [PasswordsCreateResponse]
     */
    public fun createCompletable(parameters: CreateParameters): CompletableFuture<PasswordsCreateResponse>

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
     * Initiates a password reset for the email address provided. This will trigger an email to be sent to the address,
     * containing a magic link that will allow them to set a new password and authenticate.
     * @param parameters required to reset an account password
     * @return [BaseResponse]
     */
    public fun resetByEmailStartCompletable(parameters: ResetByEmailStartParameters): CompletableFuture<BaseResponse>

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
     * Reset the user’s password and authenticate them. This endpoint checks that the magic link token is valid, hasn’t
     * expired, or already been used. The provided password needs to meet our password strength requirements, which can
     * be checked in advance with the [strengthCheck] method. If the token and password are accepted, the password is
     * securely stored for future authentication and the user is authenticated.
     * @param parameters required to reset an account password
     * @return [AuthResponse]
     */
    public fun resetByEmailCompletable(parameters: ResetByEmailParameters): CompletableFuture<AuthResponse>

    /**
     * Reset a user's password and authenticate them
     * @param parameters required to reset a user's password
     * @returns [AuthResponse]
     */
    public suspend fun resetByExistingPassword(parameters: ResetByExistingPasswordParameters): AuthResponse

    /**
     * Reset a user's password and authenticate them
     * @param parameters required to reset a user's password
     * @param callback a callback that receives an [AuthResponse]
     */
    public fun resetByExistingPassword(
        parameters: ResetByExistingPasswordParameters,
        callback: (AuthResponse) -> Unit,
    )

    /**
     * Reset a user's password and authenticate them
     * @param parameters required to reset a user's password
     * @returns [AuthResponse]
     */
    public fun resetByExistingPasswordCompletable(
        parameters: ResetByExistingPasswordParameters,
    ): CompletableFuture<AuthResponse>

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

    /**
     * This method allows you to check whether or not the user’s provided password is valid, and to provide feedback to
     * the user on how to increase the strength of their password.
     * @param parameters required to advise on password strength
     * @return [PasswordsStrengthCheckResponse]
     */
    public fun strengthCheckCompletable(
        parameters: StrengthCheckParameters,
    ): CompletableFuture<PasswordsStrengthCheckResponse>
}

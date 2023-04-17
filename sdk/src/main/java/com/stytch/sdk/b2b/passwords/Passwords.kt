package com.stytch.sdk.b2b.passwords

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.b2b.EmailResetResponse
import com.stytch.sdk.b2b.PasswordStrengthCheckResponse
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.Constants

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
@Suppress("TooManyFunctions")
public interface Passwords {
    /**
     * Data class used for wrapping parameters used with Password Authentication
     * @property organizationId is the member's organization ID
     * @property emailAddress is the member's email address
     * @property password is the member's password
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthParameters(
        val organizationId: String,
        val emailAddress: String,
        val password: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Authenticate a member with their email address and password. This endpoint verifies that the member has a
     * password currently set, and that the entered password is correct.
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
     * Authenticate a member with their email address and password. This endpoint verifies that the member has a
     * password currently set, and that the entered password is correct.
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
     * @param callback a callback that receives an [AuthResponse]
     */
    public fun authenticate(parameters: AuthParameters, callback: (AuthResponse) -> Unit)

    /**
     * Data class used for wrapping parameters used with Passwords ResetByEmailStart endpoint
     * @property organizationId is the member's organization ID
     * @property emailAddress is the member's email address
     * @property loginRedirectUrl is the url where you should be redirected after a login
     * @property resetPasswordRedirectUrl is the url where you should be redirected after a reset
     * @property resetPasswordExpirationMinutes is the duration after which a reset password request should expire
     * @property resetPasswordTemplateId Use a custom template for password reset emails. By default, it will use your
     * default email template. The template must be a template using our built-in customizations or a custom HTML email
     * for Password Reset.
     */
    public data class ResetByEmailStartParameters(
        val organizationId: String,
        val emailAddress: String,
        val loginRedirectUrl: String? = null,
        val resetPasswordRedirectUrl: String? = null,
        val resetPasswordExpirationMinutes: UInt? = null,
        val resetPasswordTemplateId: String? = null,
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
    public fun resetByEmailStart(parameters: ResetByEmailStartParameters, callback: (BaseResponse) -> Unit)

    /**
     * Data class used for wrapping parameters used with Passwords ResetByEmail endpoint
     * @property token is the unique sequence of characters that should be received after calling the resetByEmailStart
     * @property password is the private sequence of characters you wish to use as a password
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class ResetByEmailParameters(
        val token: String,
        val password: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the magic link token is valid,
     * hasn’t expired, or already been used. The provided password needs to meet our password strength requirements,
     * which can be checked in advance with the [strengthCheck] method. If the token and password are accepted, the
     * password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset an account password
     * @return [EmailResetResponse]
     */
    public suspend fun resetByEmail(parameters: ResetByEmailParameters): EmailResetResponse

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the magic link token is valid,
     * hasn’t expired, or already been used. The provided password needs to meet our password strength requirements,
     * which can be checked in advance with the [strengthCheck] method. If the token and password are accepted, the
     * password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset an account password
     * @param callback a callback that receives an [EmailResetResponse]
     */
    public fun resetByEmail(parameters: ResetByEmailParameters, callback: (EmailResetResponse) -> Unit)

    /**
     * Data class used for wrapping parameters used with Passwords StrengthCheck endpoint
     * @property organizationId is the member's organization ID
     * @property emailAddress is the member's email address
     * @property password is the new password to set
     */
    public data class ResetByExistingPasswordParameters(
        val organizationId: String,
        val emailAddress: String,
        val existingPassword: String,
        val newPassword: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the existing password matches the
     * stored value. The provided password needs to meet our password strength requirements, which can be checked in
     * advance with the password strength endpoint. If the password and accompanying parameters are accepted, the
     * password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset a member's password
     * @return [AuthResponse]
     */
    public suspend fun resetByExisting(parameters: ResetByExistingPasswordParameters): AuthResponse

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the existing password matches the
     * stored value. The provided password needs to meet our password strength requirements, which can be checked in
     * advance with the password strength endpoint. If the password and accompanying parameters are accepted, the
     * password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset a member's password
     * @param callback a callback that receives an [AuthResponse]
     */
    public fun resetByExisting(parameters: ResetByExistingPasswordParameters, callback: (AuthResponse) -> Unit)

    /**
     * Data class used for wrapping parameters used with Passwords StrengthCheck endpoint
     * @property organizationId is the member's organization ID
     * @property password is the new password to set
     */
    public data class ResetBySessionParameters(
        val organizationId: String,
        val password: String,
    )

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the session is valid and hasn’t
     * expired or been revoked. The provided password needs to meet our password strength requirements, which can be
     * checked in advance with the password strength endpoint. If the password and accompanying parameters are accepted,
     * the password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset a member's password
     * @return [AuthResponse]
     */
    public suspend fun resetBySession(parameters: ResetBySessionParameters): AuthResponse

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the session is valid and hasn’t
     * expired or been revoked. The provided password needs to meet our password strength requirements, which can be
     * checked in advance with the password strength endpoint. If the password and accompanying parameters are accepted,
     * the password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset a member's password
     * @param callback a callback that receives an [AuthResponse]
     */
    public fun resetBySession(parameters: ResetBySessionParameters, callback: (AuthResponse) -> Unit)

    /**
     * Data class used for wrapping parameters used with Passwords StrengthCheck endpoint
     * @property email is the account identifier for the account in the form of an Email address that you wish to use to
     * initiate a password strength check
     * @property password is the private sequence of characters you wish to check to get advice on improving it
     */
    public data class StrengthCheckParameters(
        val email: String? = null,
        val password: String,
    )

    /**
     * This method allows you to check whether or not the member’s provided password is valid, and to provide feedback
     * to the member on how to increase the strength of their password.
     * @param parameters required to advise on password strength
     * @return [PasswordStrengthCheckResponse]
     */
    public suspend fun strengthCheck(parameters: StrengthCheckParameters): PasswordStrengthCheckResponse

    /**
     * This method allows you to check whether or not the member’s provided password is valid, and to provide feedback
     * to the member on how to increase the strength of their password.
     * @param parameters required to advise on password strength
     * @param callback a callback that receives a [PasswordStrengthCheckResponse]
     */
    public fun strengthCheck(parameters: StrengthCheckParameters, callback: (PasswordStrengthCheckResponse) -> Unit)
}

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
    public data class AuthParameters(
        val organizationId: String,
        val emailAddress: String,
        val password: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )
    public suspend fun authenticate(parameters: AuthParameters): AuthResponse
    public fun authenticate(parameters: AuthParameters, callback: (AuthResponse) -> Unit)

    public data class ResetByEmailStartParameters(
        val organizationId: String,
        val emailAddress: String,
        val loginRedirectUrl: String? = null,
        val resetPasswordRedirectUrl: String? = null,
        val resetPasswordExpirationMinutes: UInt? = null,
        val resetPasswordTemplateId: String? = null,
    )
    public suspend fun resetByEmailStart(parameters: ResetByEmailStartParameters): BaseResponse
    public fun resetByEmailStart(parameters: ResetByEmailStartParameters, callback: (BaseResponse) -> Unit)

    public data class ResetByEmailParameters(
        val token: String,
        val password: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )
    public suspend fun resetByEmail(parameters: ResetByEmailParameters): EmailResetResponse
    public fun resetByEmail(parameters: ResetByEmailParameters, callback: (EmailResetResponse) -> Unit)

    public data class ResetByExistingPasswordParameters(
        val organizationId: String,
        val emailAddress: String,
        val existingPassword: String,
        val newPassword: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )
    public suspend fun resetByExisting(parameters: ResetByExistingPasswordParameters): AuthResponse
    public fun resetByExisting(parameters: ResetByExistingPasswordParameters, callback: (AuthResponse) -> Unit)

    public data class ResetBySessionParameters(
        val organizationId: String,
        val password: String,
    )
    public suspend fun resetBySession(parameters: ResetBySessionParameters): AuthResponse
    public fun resetBySession(parameters: ResetBySessionParameters, callback: (AuthResponse) -> Unit)

    public data class StrengthCheckParameters(
        val email: String? = null,
        val password: String,
    )
    public suspend fun strengthCheck(parameters: StrengthCheckParameters): PasswordStrengthCheckResponse
    public fun strengthCheck(parameters: StrengthCheckParameters, callback: (PasswordStrengthCheckResponse) -> Unit)
}

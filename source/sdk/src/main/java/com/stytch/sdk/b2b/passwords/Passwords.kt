package com.stytch.sdk.b2b.passwords

import com.stytch.sdk.b2b.B2BPasswordDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.B2BPasswordDiscoveryResetByEmailResponse
import com.stytch.sdk.b2b.EmailResetResponse
import com.stytch.sdk.b2b.PasswordResetByExistingPasswordResponse
import com.stytch.sdk.b2b.PasswordStrengthCheckResponse
import com.stytch.sdk.b2b.PasswordsAuthenticateResponse
import com.stytch.sdk.b2b.SessionResetResponse
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.network.models.Locale
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
@Suppress("TooManyFunctions")
public interface Passwords {
    /**
     * Data class used for wrapping parameters used with Password Authentication
     * @property organizationId is the member's organization ID
     * @property emailAddress is the member's email address
     * @property password is the member's password
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     * @property locale Used to determine which language to use when sending the user this delivery method.
     * Currently supported languages are English (`"en"`), Spanish (`"es"`), and Brazilian Portuguese (`"pt-br"`);
     * if no value is provided, the copy defaults to English.
     */
    @JacocoExcludeGenerated
    public data class AuthParameters
        @JvmOverloads
        constructor(
            val organizationId: String,
            val emailAddress: String,
            val password: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
            val locale: Locale? = null,
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
     * @return [PasswordsAuthenticateResponse]
     */
    public suspend fun authenticate(parameters: AuthParameters): PasswordsAuthenticateResponse

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
     * @param callback a callback that receives an [PasswordsAuthenticateResponse]
     */
    public fun authenticate(
        parameters: AuthParameters,
        callback: (PasswordsAuthenticateResponse) -> Unit,
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
     * @return [PasswordsAuthenticateResponse]
     */
    public fun authenticateCompletable(parameters: AuthParameters): CompletableFuture<PasswordsAuthenticateResponse>

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
     * @property verifyEmailTemplateId Use a custom template for password verify emails. By default, it will use your
     * default email template. The template must be a template using our built-in customizations or a custom HTML email
     * for Password Verification.
     */
    @JacocoExcludeGenerated
    public data class ResetByEmailStartParameters
        @JvmOverloads
        constructor(
            val organizationId: String,
            val emailAddress: String,
            val loginRedirectUrl: String? = null,
            val resetPasswordRedirectUrl: String? = null,
            val resetPasswordExpirationMinutes: Int? = null,
            val resetPasswordTemplateId: String? = null,
            val verifyEmailTemplateId: String? = null,
            val locale: Locale? = null,
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
        callback: (BaseResponse) -> Unit,
    )

    /**
     * Initiates a password reset for the email address provided. This will trigger an email to be sent to the address,
     * containing a magic link that will allow them to set a new password and authenticate.
     * @param parameters required to reset an account password
     * @return [BaseResponse]
     */
    public fun resetByEmailStartCompletable(parameters: ResetByEmailStartParameters): CompletableFuture<BaseResponse>

    /**
     * Data class used for wrapping parameters used with Passwords ResetByEmail endpoint
     * @property token is the unique sequence of characters that should be received after calling the resetByEmailStart
     * @property password is the private sequence of characters you wish to use as a password
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     * @property locale Used to determine which language to use when sending the user this delivery method.
     * Currently supported languages are English (`"en"`), Spanish (`"es"`), and Brazilian Portuguese (`"pt-br"`);
     * if no value is provided, the copy defaults to English.
     */
    @JacocoExcludeGenerated
    public data class ResetByEmailParameters
        @JvmOverloads
        constructor(
            val token: String,
            val password: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
            val locale: Locale? = null,
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
    public fun resetByEmail(
        parameters: ResetByEmailParameters,
        callback: (EmailResetResponse) -> Unit,
    )

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the magic link token is valid,
     * hasn’t expired, or already been used. The provided password needs to meet our password strength requirements,
     * which can be checked in advance with the [strengthCheck] method. If the token and password are accepted, the
     * password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset an account password
     * @return [EmailResetResponse]
     */
    public fun resetByEmailCompletable(parameters: ResetByEmailParameters): CompletableFuture<EmailResetResponse>

    /**
     * Data class used for wrapping parameters used with Passwords StrengthCheck endpoint
     * @property organizationId is the member's organization ID
     * @property emailAddress is the member's email address
     * @property existingPassword The member's current password that they supplied.
     * @property newPassword The member's elected new password.
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     * @property locale Used to determine which language to use when sending the user this delivery method.
     * Currently supported languages are English (`"en"`), Spanish (`"es"`), and Brazilian Portuguese (`"pt-br"`);
     * if no value is provided, the copy defaults to English.
     */
    @JacocoExcludeGenerated
    public data class ResetByExistingPasswordParameters
        @JvmOverloads
        constructor(
            val organizationId: String,
            val emailAddress: String,
            val existingPassword: String,
            val newPassword: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
            val locale: Locale? = null,
        )

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the existing password matches the
     * stored value. The provided password needs to meet our password strength requirements, which can be checked in
     * advance with the password strength endpoint. If the password and accompanying parameters are accepted, the
     * password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset a member's password
     * @return [PasswordResetByExistingPasswordResponse]
     */
    public suspend fun resetByExisting(
        parameters: ResetByExistingPasswordParameters,
    ): PasswordResetByExistingPasswordResponse

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the existing password matches the
     * stored value. The provided password needs to meet our password strength requirements, which can be checked in
     * advance with the password strength endpoint. If the password and accompanying parameters are accepted, the
     * password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset a member's password
     * @param callback a callback that receives an [PasswordResetByExistingPasswordResponse]
     */
    public fun resetByExisting(
        parameters: ResetByExistingPasswordParameters,
        callback: (PasswordResetByExistingPasswordResponse) -> Unit,
    )

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the existing password matches the
     * stored value. The provided password needs to meet our password strength requirements, which can be checked in
     * advance with the password strength endpoint. If the password and accompanying parameters are accepted, the
     * password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset a member's password
     * @return [PasswordResetByExistingPasswordResponse]
     */
    public fun resetByExistingCompletable(
        parameters: ResetByExistingPasswordParameters,
    ): CompletableFuture<PasswordResetByExistingPasswordResponse>

    /**
     * Data class used for wrapping parameters used with Passwords StrengthCheck endpoint
     * @property organizationId is the member's organization ID
     * @property password is the new password to set
     */
    @JacocoExcludeGenerated
    public data class ResetBySessionParameters(
        val organizationId: String,
        val password: String,
        val locale: Locale? = null,
    )

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the session is valid and hasn’t
     * expired or been revoked. The provided password needs to meet our password strength requirements, which can be
     * checked in advance with the password strength endpoint. If the password and accompanying parameters are accepted,
     * the password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset a member's password
     * @return [SessionResetResponse]
     */
    public suspend fun resetBySession(parameters: ResetBySessionParameters): SessionResetResponse

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the session is valid and hasn’t
     * expired or been revoked. The provided password needs to meet our password strength requirements, which can be
     * checked in advance with the password strength endpoint. If the password and accompanying parameters are accepted,
     * the password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset a member's password
     * @param callback a callback that receives an [SessionResetResponse]
     */
    public fun resetBySession(
        parameters: ResetBySessionParameters,
        callback: (SessionResetResponse) -> Unit,
    )

    /**
     * Reset the member’s password and authenticate them. This endpoint checks that the session is valid and hasn’t
     * expired or been revoked. The provided password needs to meet our password strength requirements, which can be
     * checked in advance with the password strength endpoint. If the password and accompanying parameters are accepted,
     * the password is securely stored for future authentication and the member is authenticated.
     * @param parameters required to reset a member's password
     * @return [SessionResetResponse]
     */
    public fun resetBySessionCompletable(parameters: ResetBySessionParameters): CompletableFuture<SessionResetResponse>

    /**
     * Data class used for wrapping parameters used with Passwords StrengthCheck endpoint
     * @property email is the account identifier for the account in the form of an Email address that you wish to use to
     * initiate a password strength check
     * @property password is the private sequence of characters you wish to check to get advice on improving it
     */
    @JacocoExcludeGenerated
    public data class StrengthCheckParameters
        @JvmOverloads
        constructor(
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
    public fun strengthCheck(
        parameters: StrengthCheckParameters,
        callback: (PasswordStrengthCheckResponse) -> Unit,
    )

    /**
     * This method allows you to check whether or not the member’s provided password is valid, and to provide feedback
     * to the member on how to increase the strength of their password.
     * @param parameters required to advise on password strength
     * @return [PasswordStrengthCheckResponse]
     */
    public fun strengthCheckCompletable(
        parameters: StrengthCheckParameters,
    ): CompletableFuture<PasswordStrengthCheckResponse>

    /**
     * Public variable that exposes an instance of [Discovery]
     */
    public val discovery: Discovery

    public interface Discovery {
        /**
         * A data class used for wrapping the parameters used in a Passwords.Discovery.ResetByEmailStart call
         * @property emailAddress - The email that requested the password reset
         * @property discoveryRedirectUrl - The url that the Member clicks from the password reset email to skip
         * resetting their password and directly login. This should be a url that your app receives, parses, and
         * subsequently sends an API request to the magic link authenticate endpoint to complete the login process
         * without reseting their password. If this value is not passed, the login redirect URL that you set in your
         * Dashboard is used. If you have not set a default login redirect URL, an error is returned.
         * @property resetPasswordRedirectUrl - The url that the Member clicks from the password reset email to finish
         * the reset password flow. This should be a url that your app receives and parses before showing your app's
         * reset password page. After the Member submits a new password to your app, it should send an API request to
         * complete the password reset process. If this value is not passed, the default reset password redirect URL
         * that you set in your Dashboard is used. If you have not set a default reset password redirect URL, an error
         * is returned.
         * @property resetPasswordExpirationMinutes - Set the expiration for the password reset, in minutes. By default,
         * it expires in 30 minutes. The minimum expiration is 5 minutes and the maximum is 7 days (10080 mins).
         * @property resetPasswordTemplateId - The email template ID to use for password reset. If not provided, your
         * default email template will be sent. If providing a template ID, it must be either a template using
         * Stytch's customizations, or a Passwords reset custom HTML template.
         * @property verifyEmailTemplateId Use a custom template for password verify emails. By default, it will use
         * your default email template. The template must be a template using our built-in customizations or a custom
         * HTML email for Password Verification.
         */
        @JacocoExcludeGenerated
        public data class ResetByEmailStartParameters(
            val emailAddress: String,
            val discoveryRedirectUrl: String? = null,
            val resetPasswordRedirectUrl: String? = null,
            val resetPasswordExpirationMinutes: Int? = null,
            val resetPasswordTemplateId: String? = null,
            val verifyEmailTemplateId: String? = null,
            val locale: Locale? = null,
        )

        /**
         * The `resetByEmailStart` method wraps the Reset By Email Discovery Start Password API endpoint.
         * If this method succeeds, an email will be sent to the provided email address with a link to reset the
         * password.
         * @param parameters - [ResetByEmailStartParameters]
         * @return [BaseResponse]
         */
        public suspend fun resetByEmailStart(parameters: ResetByEmailStartParameters): BaseResponse

        /**
         * The `resetByEmailStart` method wraps the Reset By Email Discovery Start Password API endpoint.
         * If this method succeeds, an email will be sent to the provided email address with a link to reset the
         * password.
         * @param parameters - [ResetByEmailStartParameters]
         * @param callback - a callback that receives a [BaseResponse]
         */
        public fun resetByEmailStart(
            parameters: ResetByEmailStartParameters,
            callback: (BaseResponse) -> Unit,
        )

        /**
         * The `resetByEmailStart` method wraps the Reset By Email Discovery Start Password API endpoint.
         * If this method succeeds, an email will be sent to the provided email address with a link to reset the
         * password.
         * @param parameters - [ResetByEmailParameters]
         * @return CompletableFuture<[BaseResponse]>
         */
        public fun resetByEmailStartCompletable(
            parameters: ResetByEmailStartParameters,
        ): CompletableFuture<BaseResponse>

        /**
         * A data class used for wrapping the parameters used in a Passwords.Discovery.ResetByEmail call
         * @property passwordResetToken - The token to authenticate.
         * @property password - The new password for the Member.
         */
        @JacocoExcludeGenerated
        public data class ResetByEmailParameters(
            val passwordResetToken: String,
            val password: String,
            val locale: Locale? = null,
        )

        /**
         * The `resetByEmail` method wraps the Reset By Email Discovery Password API endpoint.
         * This endpoint resets the password associated with an email and starts an intermediate session for the user.
         * @param parameters - [ResetByEmailParameters]
         * @return [B2BPasswordDiscoveryResetByEmailResponse]
         */
        public suspend fun resetByEmail(parameters: ResetByEmailParameters): B2BPasswordDiscoveryResetByEmailResponse

        /**
         * The `resetByEmail` method wraps the Reset By Email Discovery Password API endpoint.
         * This endpoint resets the password associated with an email and starts an intermediate session for the user.
         * @param parameters - [ResetByEmailParameters]
         * @param callback - a callback that receives a [B2BPasswordDiscoveryResetByEmailResponse]
         */
        public fun resetByEmail(
            parameters: ResetByEmailParameters,
            callback: (B2BPasswordDiscoveryResetByEmailResponse) -> Unit,
        )

        /**
         * The `resetByEmail` method wraps the Reset By Email Discovery Password API endpoint.
         * This endpoint resets the password associated with an email and starts an intermediate session for the user.
         * @param parameters - [ResetByEmailParameters]
         * @return CompletableFuture<[B2BPasswordDiscoveryResetByEmailResponse]>
         */
        public fun resetByEmailCompletable(
            parameters: ResetByEmailParameters,
        ): CompletableFuture<B2BPasswordDiscoveryResetByEmailResponse>

        /**
         * A data class used for wrapping the parameters used in a Passwords.Discovery.Authenticate call
         * @property emailAddress - The email attempting to login.
         * @property password - The password for the email address.
         */
        @JacocoExcludeGenerated
        public data class AuthenticateParameters(
            val emailAddress: String,
            val password: String,
        )

        /**
         * The `authenticate` method wraps the Discovery Authenticate Password API endpoint.
         * This endpoint verifies that the email has a password currently set, and that the entered password is correct.
         * @param parameters - [AuthenticateParameters]
         * @return [B2BPasswordDiscoveryAuthenticateResponse]
         */
        public suspend fun authenticate(parameters: AuthenticateParameters): B2BPasswordDiscoveryAuthenticateResponse

        /**
         * The `authenticate` method wraps the Discovery Authenticate Password API endpoint.
         * This endpoint verifies that the email has a password currently set, and that the entered password is correct.
         * @param parameters - [AuthenticateParameters]
         * @param callback - a callback that receives a [B2BPasswordDiscoveryAuthenticateResponse]
         */
        public fun authenticate(
            parameters: AuthenticateParameters,
            callback: (B2BPasswordDiscoveryAuthenticateResponse) -> Unit,
        )

        /**
         * The `authenticate` method wraps the Discovery Authenticate Password API endpoint.
         * This endpoint verifies that the email has a password currently set, and that the entered password is correct.
         * @param parameters - [AuthenticateParameters]
         * @return CompletableFuture<[B2BPasswordDiscoveryAuthenticateResponse]>
         */
        public fun authenticateCompletable(
            parameters: AuthenticateParameters,
        ): CompletableFuture<B2BPasswordDiscoveryAuthenticateResponse>
    }
}

package com.stytch.sdk.consumer.network.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.common.network.models.NameData

internal object ConsumerRequests {
    object MagicLinks {
        object Email {
            @Keep
            @JsonClass(generateAdapter = true)
            data class LoginOrCreateUserRequest(
                val email: String,
                @Json(name = "login_magic_link_url")
                val loginMagicLinkUrl: String?,
                @Json(name = "signup_magic_link_url")
                val signupMagicLinkUrl: String?,
                @Json(name = "code_challenge")
                val codeChallenge: String,
                @Json(name = "login_template_id")
                val loginTemplateId: String? = null,
                @Json(name = "signup_template_id")
                val signupTemplateId: String? = null,
                val locale: Locale? = null,
            )
        }

        @Keep
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val token: String,
            @Json(name = "code_verifier")
            val codeVerifier: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class SendRequest(
            val email: String,
            @Json(name = "login_magic_link_url")
            val loginMagicLinkUrl: String?,
            @Json(name = "signup_magic_link_url")
            val signupMagicLinkUrl: String?,
            @Json(name = "login_expiration_minutes")
            val loginExpirationMinutes: Int?,
            @Json(name = "signup_expiration_minutes")
            val signupExpirationMinutes: Int?,
            @Json(name = "login_template_id")
            val loginTemplateId: String?,
            @Json(name = "signup_template_id")
            val signupTemplateId: String?,
            @Json(name = "code_challenge")
            val codeChallenge: String?,
            val locale: Locale? = null,
        )
    }

    object Passwords {
        @Keep
        @JsonClass(generateAdapter = true)
        data class CreateRequest(
            val email: String,
            val password: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val email: String,
            val password: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class ResetByEmailStartRequest(
            val email: String,
            @Json(name = "code_challenge")
            val codeChallenge: String,
            @Json(name = "login_redirect_url")
            val loginRedirectUrl: String?,
            @Json(name = "login_expiration_minutes")
            val loginExpirationMinutes: Int?,
            @Json(name = "reset_password_redirect_url")
            val resetPasswordRedirectUrl: String?,
            @Json(name = "reset_password_expiration_minutes")
            val resetPasswordExpirationMinutes: Int?,
            @Json(name = "reset_password_template_id")
            val resetPasswordTemplateId: String?,
            val locale: Locale? = null,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class ResetByEmailRequest(
            val token: String,
            val password: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
            @Json(name = "code_verifier")
            val codeVerifier: String,
            val locale: Locale? = null,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class ResetBySessionRequest(
            val password: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
            val locale: Locale? = null,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class StrengthCheckRequest(
            val email: String?,
            val password: String,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class PasswordResetByExistingPasswordRequest(
            val email: String,
            @Json(name = "existing_password")
            val existingPassword: String,
            @Json(name = "new_password")
            val newPassword: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }

    object OTP {
        @Keep
        @JsonClass(generateAdapter = true)
        data class SMS(
            @Json(name = "phone_number")
            val phoneNumber: String,
            @Json(name = "expiration_minutes")
            val expirationMinutes: Int?,
            @Json(name = "enable_autofill")
            val enableAutofill: Boolean = false,
            val locale: Locale? = null,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class WhatsApp(
            @Json(name = "phone_number")
            val phoneNumber: String,
            @Json(name = "expiration_minutes")
            val expirationMinutes: Int?,
            val locale: Locale? = null,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class Email(
            val email: String,
            @Json(name = "expiration_minutes")
            val expirationMinutes: Int?,
            @Json(name = "login_template_id")
            val loginTemplateId: String?,
            @Json(name = "signup_template_id")
            val signupTemplateId: String?,
            val locale: Locale? = null,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class Authenticate(
            val token: String,
            @Json(name = "method_id")
            val methodId: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }

    object Biometrics {
        @Keep
        @JsonClass(generateAdapter = true)
        data class RegisterStartRequest(
            @Json(name = "public_key")
            val publicKey: String,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class RegisterRequest(
            val signature: String,
            @Json(name = "biometric_registration_id")
            val biometricRegistrationId: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class AuthenticateStartRequest(
            @Json(name = "public_key")
            val publicKey: String,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val signature: String,
            @Json(name = "biometric_registration_id")
            val biometricRegistrationId: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }

    object OAuth {
        object Google {
            @Keep
            @JsonClass(generateAdapter = true)
            data class AuthenticateRequest(
                @Json(name = "id_token")
                val idToken: String,
                val nonce: String,
                @Json(name = "session_duration_minutes")
                val sessionDurationMinutes: Int,
            )
        }

        object ThirdParty {
            @Keep
            @JsonClass(generateAdapter = true)
            data class AuthenticateRequest(
                val token: String,
                @Json(name = "session_duration_minutes")
                val sessionDurationMinutes: Int,
                @Json(name = "session_custom_claims")
                val sessionCustomClaims: Map<String, Any>? = null,
                @Json(name = "session_jwt")
                val sessionJwt: String? = null,
                @Json(name = "session_token")
                val sessionToken: String? = null,
                @Json(name = "code_verifier")
                val codeVerifier: String,
            )
        }
    }

    object User {
        @Keep
        @JsonClass(generateAdapter = true)
        data class UpdateRequest(
            val name: NameData? = null,
            @Json(name = "untrusted_metadata")
            val untrustedMetadata: Map<String, Any?>? = null,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class SearchRequest(
            val email: String,
        )
    }

    object WebAuthn {
        @Keep
        @JsonClass(generateAdapter = true)
        data class RegisterStartRequest(
            val domain: String,
            @Json(name = "user_agent")
            val userAgent: String? = null,
            @Json(name = "authenticator_type")
            val authenticatorType: String? = null,
            @Json(name = "return_passkey_credential_options")
            val isPasskey: Boolean? = false,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class RegisterRequest(
            @Json(name = "public_key_credential")
            val publicKeyCredential: String,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class AuthenticateStartRequest(
            @Json(name = "user_id")
            val userId: String? = null,
            val domain: String,
            @Json(name = "return_passkey_credential_options")
            val isPasskey: Boolean? = false,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "public_key_credential")
            val publicKeyCredential: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
            @Json(name = "session_custom_claims")
            val sessionCustomClaims: Map<String, Any>? = null,
            @Json(name = "session_jwt")
            val sessionJwt: String? = null,
            @Json(name = "session_token")
            val sessionToken: String? = null,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class UpdateRequest(
            val name: String,
        )
    }

    object Crypto {
        @Keep
        @JsonClass(generateAdapter = true)
        data class CryptoWalletAuthenticateStartRequest(
            @Json(name = "crypto_wallet_address")
            val cryptoWalletAddress: String,
            @Json(name = "crypto_wallet_type")
            val cryptoWalletType: CryptoWalletType,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class CryptoWalletAuthenticateRequest(
            @Json(name = "crypto_wallet_address")
            val cryptoWalletAddress: String,
            @Json(name = "crypto_wallet_type")
            val cryptoWalletType: CryptoWalletType,
            val signature: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }

    object TOTP {
        @Keep
        @JsonClass(generateAdapter = true)
        data class TOTPCreateRequest(
            @Json(name = "expiration_minutes")
            val expirationMinutes: Int,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class TOTPAuthenticateRequest(
            @Json(name = "totp_code")
            val totpCode: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class TOTPRecoverRequest(
            @Json(name = "recovery_code")
            val recoveryCode: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }
}

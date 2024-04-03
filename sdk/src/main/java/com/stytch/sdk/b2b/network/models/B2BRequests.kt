package com.stytch.sdk.b2b.network.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal object B2BRequests {
    object MagicLinks {
        object Email {
            @Keep
            @JsonClass(generateAdapter = true)
            data class LoginOrSignupRequest(
                @Json(name = "email_address")
                val email: String,
                @Json(name = "organization_id")
                val organizationId: String,
                @Json(name = "login_redirect_url")
                val loginRedirectUrl: String?,
                @Json(name = "signup_redirect_url")
                val signupRedirectUrl: String?,
                @Json(name = "pkce_code_challenge")
                val codeChallenge: String,
                @Json(name = "login_template_id")
                val loginTemplateId: String? = null,
                @Json(name = "signup_template_id")
                val signupTemplateId: String? = null,
            )
        }

        object Discovery {
            @Keep
            @JsonClass(generateAdapter = true)
            data class SendRequest(
                @Json(name = "email_address")
                val email: String,
                @Json(name = "discovery_redirect_url")
                val discoveryRedirectUrl: String? = null,
                @Json(name = "pkce_code_challenge")
                val codeChallenge: String,
                @Json(name = "login_template_id")
                val loginTemplateId: String? = null,
            )

            @Keep
            @JsonClass(generateAdapter = true)
            data class AuthenticateRequest(
                @Json(name = "discovery_magic_links_token")
                val token: String,
                @Json(name = "pkce_code_verifier")
                val codeVerifier: String,
            )
        }

        @Keep
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "magic_links_token")
            val token: String,
            @Json(name = "pkce_code_verifier")
            val codeVerifier: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }

    object Passwords {
        @Keep
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "organization_id")
            val organizationId: String,
            @Json(name = "email_address")
            val emailAddress: String,
            val password: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class ResetByEmailStartRequest(
            @Json(name = "organization_id")
            val organizationId: String,
            @Json(name = "email_address")
            val emailAddress: String,
            @Json(name = "login_redirect_url")
            val loginRedirectUrl: String?,
            @Json(name = "reset_password_redirect_url")
            val resetPasswordRedirectUrl: String?,
            @Json(name = "reset_password_expiration_minutes")
            val resetPasswordExpirationMinutes: Int?,
            @Json(name = "reset_password_template_id")
            val resetPasswordTemplateId: String?,
            @Json(name = "code_challenge")
            val codeChallenge: String,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class ResetByEmailRequest(
            @Json(name = "password_reset_token")
            val passwordResetToken: String,
            val password: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
            @Json(name = "code_verifier")
            val codeVerifier: String,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class ResetByExistingPasswordRequest(
            @Json(name = "organization_id")
            val organizationId: String,
            @Json(name = "email_address")
            val emailAddress: String,
            @Json(name = "existing_password")
            val existingPassword: String,
            @Json(name = "new_password")
            val newPassword: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class ResetBySessionRequest(
            @Json(name = "organization_id")
            val organizationId: String,
            val password: String,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class StrengthCheckRequest(
            val email: String?,
            val password: String,
        )
    }

    object Discovery {
        @Keep
        @JsonClass(generateAdapter = true)
        data class MembershipsRequest(
            @Json(name = "intermediate_session_token")
            val intermediateSessionToken: String? = null,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class SessionExchangeRequest(
            @Json(name = "intermediate_session_token")
            val intermediateSessionToken: String,
            @Json(name = "organization_id")
            val organizationId: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @Keep
        @JsonClass(generateAdapter = true)
        data class CreateRequest(
            @Json(name = "intermediate_session_token")
            val intermediateSessionToken: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
            @Json(name = "organization_name")
            val organizationName: String?,
            @Json(name = "organization_slug")
            val organizationSlug: String?,
            @Json(name = "organization_logo_url")
            val organizationLogoUrl: String?,
            @Json(name = "sso_jit_provisioning")
            val ssoJitProvisioning: SsoJitProvisioning?,
            @Json(name = "email_allowed_domains")
            val emailAllowedDomains: List<String>?,
            @Json(name = "email_jit_provisioning")
            val emailJitProvisioning: EmailJitProvisioning?,
            @Json(name = "email_invites")
            val emailInvites: EmailInvites?,
            @Json(name = "auth_methods")
            val authMethods: AuthMethods?,
            @Json(name = "allowed_auth_methods")
            val allowedAuthMethods: List<AllowedAuthMethods>?,
        )
    }

    object SSO {
        @Keep
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "sso_token")
            val ssoToken: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
            @Json(name = "pkce_code_verifier")
            val codeVerifier: String,
        )
    }
}

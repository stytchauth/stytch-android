package com.stytch.sdk.b2b.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal object B2BRequests {
    object MagicLinks {
        object Email {
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

            @JsonClass(generateAdapter = true)
            data class AuthenticateRequest(
                @Json(name = "discovery_magic_links_token")
                val token: String,
                @Json(name = "pkce_code_verifier")
                val codeVerifier: String,
            )
        }

        object Invite {
            @JsonClass(generateAdapter = true)
            data class InviteRequest(
                @Json(name = "email_address")
                val emailAddress: String,
                @Json(name = "invite_redirect_url")
                val inviteRedirectUrl: String? = null,
                @Json(name = "invite_template_id")
                val inviteTemplateId: String? = null,
                val name: String? = null,
                @Json(name = "untrusted_metadata")
                val untrustedMetadata: Map<String, Any?>? = null,
                val locale: String? = null,
                val roles: List<String>? = null,
            )
        }

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

        @JsonClass(generateAdapter = true)
        data class ResetBySessionRequest(
            @Json(name = "organization_id")
            val organizationId: String,
            val password: String,
        )

        @JsonClass(generateAdapter = true)
        data class StrengthCheckRequest(
            val email: String?,
            val password: String,
        )
    }

    object Discovery {
        @JsonClass(generateAdapter = true)
        data class MembershipsRequest(
            @Json(name = "intermediate_session_token")
            val intermediateSessionToken: String? = null,
        )

        @JsonClass(generateAdapter = true)
        data class SessionExchangeRequest(
            @Json(name = "intermediate_session_token")
            val intermediateSessionToken: String,
            @Json(name = "organization_id")
            val organizationId: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

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

    object Session {
        @JsonClass(generateAdapter = true)
        data class ExchangeRequest(
            @Json(name = "organization_id")
            val organizationId: String,
            val locale: String? = null,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int? = null,
        )
    }

    object Member {
        @JsonClass(generateAdapter = true)
        data class UpdateRequest(
            val name: String? = null,
            @Json(name = "untrusted_metadata")
            val untrustedMetadata: Map<String, Any?>? = null,
            @Json(name = "mfa_enrolled")
            val mfaEnrolled: Boolean? = null,
            @Json(name = "mfa_phone_number")
            val mfaPhoneNumber: String? = null,
            @Json(name = "default_mfa_method")
            val defaultMfaMethod: MfaMethod? = null,
        )
    }

    object Organization {
        @JsonClass(generateAdapter = true)
        data class UpdateRequest(
            @Json(name = "organization_name")
            val organizationName: String? = null,
            @Json(name = "organization_slug")
            val organizationSlug: String? = null,
            @Json(name = "organization_logo_url")
            val organizationLogoUrl: String? = null,
            @Json(name = "sso_default_connection_id")
            val ssoDefaultConnectionId: String? = null,
            @Json(name = "sso_jit_provisioning")
            val ssoJitProvisioning: SsoJitProvisioning? = null,
            @Json(name = "sso_jit_provisioning_allowed_connections")
            val ssoJitProvisioningAllowedConnections: List<String>? = null,
            @Json(name = "email_allowed_domains")
            val emailAllowedDomains: List<String>? = null,
            @Json(name = "email_jit_provisioning")
            val emailJitProvisioning: EmailJitProvisioning? = null,
            @Json(name = "email_invites")
            val emailInvites: EmailInvites? = null,
            @Json(name = "auth_methods")
            val authMethods: AuthMethods? = null,
            @Json(name = "allowed_auth_methods")
            val allowedAuthMethods: List<AllowedAuthMethods>? = null,
            @Json(name = "mfa_methods")
            val mfaMethods: MfaMethods? = null,
            @Json(name = "allowed_mfa_methods")
            val allowedMfaMethods: List<MfaMethod>? = null,
            @Json(name = "mfa_policy")
            val mfaPolicy: MfaPolicy? = null,
            @Json(name = "rbac_email_implicit_role_assignments")
            val rbacEmailImplicitRoleAssignments: List<String>? = null,
            val defaultMfaMethod: MfaMethod? = null,
        )

        @JsonClass(generateAdapter = true)
        data class CreateMemberRequest(
            @Json(name = "email_address")
            val emailAddress: String,
            @Json(name = "name")
            val name: String? = null,
            @Json(name = "is_breakglass")
            val isBreakGlass: Boolean? = null,
            @Json(name = "mfa_enrolled")
            val mfaEnrolled: Boolean? = null,
            @Json(name = "mfa_phone_number")
            val mfaPhoneNumber: String? = null,
            @Json(name = "untrusted_metadata")
            val untrustedMetadata: Map<String, Any?>? = null,
            @Json(name = "create_member_as_pending")
            val createMemberAsPending: Boolean? = null,
            @Json(name = "roles")
            val roles: List<String>? = null,
        )

        @JsonClass(generateAdapter = true)
        data class UpdateMemberRequest(
            @Json(name = "email_address")
            val emailAddress: String? = null,
            @Json(name = "name")
            val name: String? = null,
            @Json(name = "is_breakglass")
            val isBreakGlass: Boolean? = null,
            @Json(name = "mfa_enrolled")
            val mfaEnrolled: Boolean? = null,
            @Json(name = "mfa_phone_number")
            val mfaPhoneNumber: String? = null,
            @Json(name = "untrusted_metadata")
            val untrustedMetadata: Map<String, Any?>? = null,
            @Json(name = "roles")
            val roles: List<String>? = null,
            @Json(name = "preserve_existing_sessions")
            val preserveExistingSessions: Boolean? = null,
            @Json(name = "default_mfa_method")
            val defaultMfaMethod: MfaMethod? = null,
        )

        @JsonClass(generateAdapter = true)
        data class SearchMembersRequest(
            val cursor: String? = null,
            val limit: Int? = null,
            val query: SearchQuery? = null,
        )
    }

    @JsonClass(generateAdapter = true)
    data class SearchQuery(
        val operator: SearchOperator,
        val operands: List<SearchQueryOperand>,
    )

    @JsonClass(generateAdapter = true)
    data class SearchQueryOperand(
        @Json(name = "filter_name")
        val filterName: String,
        @Json(name = "filter_value")
        val filterValue: Any,
    )

    object OTP {
        object SMS {
            @JsonClass(generateAdapter = true)
            data class SendRequest(
                @Json(name = "organization_id")
                val organizationId: String,
                @Json(name = "member_id")
                val memberId: String,
                @Json(name = "mfa_phone_number")
                val mfaPhoneNumber: String? = null,
                val locale: String? = null,
            )

            @JsonClass(generateAdapter = true)
            data class AuthenticateRequest(
                @Json(name = "organization_id")
                val organizationId: String,
                @Json(name = "member_id")
                val memberId: String,
                val code: String,
                @Json(name = "set_mfa_enrollment")
                val setMFAEnrollment: SetMFAEnrollment? = null,
                @Json(name = "session_duration_minutes")
                val sessionDurationMinutes: Int,
            )
        }
    }

    object TOTP {
        @JsonClass(generateAdapter = true)
        data class CreateRequest(
            @Json(name = "organization_id")
            val organizationId: String,
            @Json(name = "member_id")
            val memberId: String,
            @Json(name = "expiration_minutes")
            val expirationMinutes: Int? = null,
        )

        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "organization_id")
            val organizationId: String,
            @Json(name = "member_id")
            val memberId: String,
            val code: String,
            @Json(name = "set_mfa_enrollment")
            val setMFAEnrollment: SetMFAEnrollment? = null,
            @Json(name = "set_default_mfa")
            val setDefaultMfaMethod: Boolean? = null,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }

    object RecoveryCodes {
        @JsonClass(generateAdapter = true)
        data class RecoverRequest(
            @Json(name = "organization_id")
            val organizationId: String,
            @Json(name = "member_id")
            val memberId: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
            @Json(name = "recovery_code")
            val recoveryCode: String,
        )
    }

    object OAuth {
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "oauth_token")
            val oauthToken: String,
            val locale: String? = null,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
            @Json(name = "pkce_code_verifier")
            val pkceCodeVerifier: String,
            @Json(name = "intermediate_session_token")
            val intermediateSessionToken: String? = null,
        )

        @JsonClass(generateAdapter = true)
        data class DiscoveryAuthenticateRequest(
            @Json(name = "discovery_oauth_token")
            val discoveryOauthToken: String,
            @Json(name = "pkce_code_verifier")
            val pkceCodeVerifier: String,
        )
    }
}

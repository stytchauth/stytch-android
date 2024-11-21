package com.stytch.sdk.b2b.network.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import com.stytch.sdk.common.network.models.IBasicData
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
public interface IB2BAuthData : CommonAuthenticationData {
    public val memberSession: B2BSessionData
    public override val sessionJwt: String
    public override val sessionToken: String
    public val member: MemberData
    public val organization: OrganizationData
}

@Keep
public interface IB2BAuthDataWithMFA : CommonAuthenticationData {
    public val memberId: String
    public override val sessionToken: String
    public override val sessionJwt: String
    public val member: MemberData
    public val organization: OrganizationData
    public val memberSession: B2BSessionData?
    public val memberAuthenticated: Boolean
    public val intermediateSessionToken: String?
    public val mfaRequired: MFARequired?
}

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class MFARequired(
    @Json(name = "member_options")
    val memberOptions: MemberOptions? = null,
    @Json(name = "secondary_auth_initiated")
    val secondaryAuthInitiated: String? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class MemberOptions(
    @Json(name = "mfa_phone_number")
    val mfaPhoneNumber: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SessionsAuthenticateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val member: MemberData,
    override val organization: OrganizationData,
) : IB2BAuthData,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SMSAuthenticateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val member: MemberData,
    override val organization: OrganizationData,
) : IB2BAuthData,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BEMLAuthenticateData(
    override val member: MemberData,
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "method_id")
    val methodId: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData?,
    @Json(name = "reset_sessions")
    val resetSessions: Boolean,
    @Json(name = "member_id")
    override val memberId: String,
    override val organization: OrganizationData,
    @Json(name = "member_authenticated")
    override val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    override val intermediateSessionToken: String?,
    @Json(name = "mfa_required")
    override val mfaRequired: MFARequired?,
) : IB2BAuthDataWithMFA,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSessionData(
    @Json(name = "member_session_id")
    val memberSessionId: String,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "started_at")
    val startedAt: String,
    @Json(name = "last_accessed_at")
    val lastAccessedAt: String,
    @Json(name = "expires_at")
    val expiresAt: String,
    @Json(name = "authentication_factors")
    val authenticationFactors: List<B2BAuthenticationFactor>,
    @Json(name = "custom_claims")
    val customClaims: @RawValue Map<String, Any?>?,
    val roles: List<String>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class MemberResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val member: MemberData,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class MemberData(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "email_address")
    val email: String,
    val status: String,
    val name: String,
    @Json(name = "trusted_metadata")
    val trustedMetadata: @RawValue Map<String, Any?>?,
    @Json(name = "untrusted_metadata")
    val untrustedMetadata: @RawValue Map<String, Any?>?,
    @Json(name = "sso_registrations")
    val ssoRegistrations: List<SSORegistration>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SSORegistration(
    @Json(name = "connection_id")
    val connectionId: String,
    @Json(name = "external_id")
    val externalId: String,
    @Json(name = "registration_id")
    val registrationId: String,
    @Json(name = "sso_attributes")
    val ssoAttributes: @RawValue Map<String, Any?>?,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OrganizationResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val organization: OrganizationData,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OrganizationData(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "organization_name")
    val organizationName: String,
    @Json(name = "organization_slug")
    val organizationSlug: String,
    @Json(name = "organization_logo_url")
    val organizationLogoUrl: String,
    @Json(name = "trusted_metadata")
    val trustedMetadata: @RawValue Map<String, Any?>,
    @Json(name = "sso_default_connection_id")
    val ssoDefaultConnectionId: String?,
    @Json(name = "sso_jit_provisioning")
    val ssoJitProvisioning: SsoJitProvisioning?,
    @Json(name = "sso_jit_provisioning_allowed_connections")
    val ssoJitProvisioningAllowedConnections: List<String>?,
    @Json(name = "sso_active_connections")
    val ssoActiveConnections: List<SSOActiveConnection>?,
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
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SSOActiveConnection(
    @Json(name = "connection_id")
    val connectionId: String,
    @Json(name = "display_name")
    val displayName: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class PasswordsAuthenticateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData?,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "member_id")
    override val memberId: String,
    override val member: MemberData,
    override val organization: OrganizationData,
    @Json(name = "member_authenticated")
    override val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    override val intermediateSessionToken: String?,
    @Json(name = "mfa_required")
    override val mfaRequired: MFARequired?,
) : IB2BAuthDataWithMFA,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class EmailResetResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val member: MemberData,
    override val organization: OrganizationData,
    @Json(name = "member_email_id")
    val memberEmailId: String,
    @Json(name = "member_id")
    override val memberId: String,
    @Json(name = "member_authenticated")
    override val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    override val intermediateSessionToken: String?,
    @Json(name = "mfa_required")
    override val mfaRequired: MFARequired?,
) : IB2BAuthDataWithMFA,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class PasswordResetByExistingPasswordResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData?,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val member: MemberData,
    override val organization: OrganizationData,
    @Json(name = "member_email_id")
    val memberEmailId: String,
    @Json(name = "member_id")
    override val memberId: String,
    @Json(name = "member_authenticated")
    override val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    override val intermediateSessionToken: String?,
    @Json(name = "mfa_required")
    override val mfaRequired: MFARequired?,
) : IB2BAuthDataWithMFA,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SessionResetResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "member_session")
    val memberSession: B2BSessionData,
    val member: MemberData,
    val organization: OrganizationData,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class StrengthCheckResponseData(
    @Json(name = "valid_password")
    val validPassword: Boolean,
    val score: Int,
    @Json(name = "breached_password")
    val breachedPassword: Boolean,
    @Json(name = "strength_policy")
    val strengthPolicy: String,
    @Json(name = "breach_detection_on_create")
    val breachDetectionOnCreate: Boolean,
    @Json(name = "zxcvbn_feedback")
    val zxcvbnFeedback: ZXCVBNFeedback,
    @Json(name = "luds_feedback")
    val ludsFeedback: LUDSFeedback,
) : Parcelable {
    @Keep
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class ZXCVBNFeedback(
        val suggestions: List<String>,
        val warning: String,
    ) : Parcelable

    @Keep
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class LUDSFeedback(
        @Json(name = "has_lower_case")
        val hasLowerCase: Boolean,
        @Json(name = "has_upper_case")
        val hasUpperCase: Boolean,
        @Json(name = "has_digit")
        val hasDigit: Boolean,
        @Json(name = "has_symbol")
        val hasSymbol: Boolean,
        @Json(name = "missing_complexity")
        val missingComplexity: Int,
        @Json(name = "missing_characters")
        val missingCharacters: Int,
    ) : Parcelable
}

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class DiscoveredOrganizationsResponseData(
    @Json(name = "email_address")
    val emailAddress: String,
    @Json(name = "discovered_organizations")
    val discoveredOrganizations: List<DiscoveredOrganization>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class DiscoveredOrganization(
    val organization: OrganizationData,
    val membership: Membership,
    @Json(name = "member_authenticated")
    val memberAuthenticated: Boolean,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class Membership(
    val type: String,
    val details: MembershipDetails?,
    val member: MemberData?,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class MembershipDetails(
    val domain: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class IntermediateSessionExchangeResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData?,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "member_id")
    override val memberId: String,
    override val member: MemberData,
    override val organization: OrganizationData,
    @Json(name = "member_authenticated")
    override val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    override val intermediateSessionToken: String?,
    @Json(name = "mfa_required")
    override val mfaRequired: MFARequired?,
) : IB2BAuthDataWithMFA,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OrganizationCreateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData?,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "member_id")
    override val memberId: String,
    override val member: MemberData,
    override val organization: OrganizationData,
    @Json(name = "member_authenticated")
    override val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    override val intermediateSessionToken: String?,
    @Json(name = "mfa_required")
    override val mfaRequired: MFARequired?,
) : IB2BAuthDataWithMFA,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class DiscoveryAuthenticateResponseData(
    @Json(name = "intermediate_session_token")
    val intermediateSessionToken: String,
    @Json(name = "email_address")
    val emailAddress: String,
    @Json(name = "discovered_organizations")
    val discoveredOrganizations: List<DiscoveredOrganization>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SSOAuthenticateResponseData(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "method_id")
    val methodId: String,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "session")
    override val memberSession: B2BSessionData?,
    @Json(name = "reset_session")
    val resetSession: Boolean,
    @Json(name = "member_id")
    override val memberId: String,
    override val member: MemberData,
    override val organization: OrganizationData,
    @Json(name = "member_authenticated")
    override val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    override val intermediateSessionToken: String?,
    @Json(name = "mfa_required")
    override val mfaRequired: MFARequired?,
) : IB2BAuthDataWithMFA,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SessionExchangeResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData?,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "member_id")
    override val memberId: String,
    override val member: MemberData,
    override val organization: OrganizationData,
    @Json(name = "member_authenticated")
    override val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    override val intermediateSessionToken: String?,
    @Json(name = "mfa_required")
    override val mfaRequired: MFARequired?,
) : IB2BAuthDataWithMFA,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class UpdateMemberResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val member: MemberData,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class MemberDeleteAuthenticationFactorData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val member: MemberData,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OrganizationUpdateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val organization: OrganizationData,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OrganizationDeleteResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "organization_id")
    val organizationId: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OrganizationMemberDeleteResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_id")
    val memberId: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class MemberResponseCommonData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_id")
    val memberId: String,
    val member: MemberData,
    val organization: OrganizationData,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class MemberSearchResponseData(
    val members: List<MemberData>,
    val organizations: Map<String, OrganizationData>,
    @Json(name = "results_metadata")
    val resultsMetadata: ResultsMetadata,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class ResultsMetadata(
    val total: Int,
    @Json(name = "next_cursor")
    val nextCursor: String? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OTPAuthenticateResponse(
    @Json(name = "member_id")
    val memberId: String,
    override val member: MemberData,
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "method_id")
    val methodId: String,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "session")
    override val memberSession: B2BSessionData,
    @Json(name = "reset_session")
    val resetSession: Boolean,
    override val organization: OrganizationData,
) : IB2BAuthData,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class TOTPCreateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val secret: String,
    @Json(name = "qr_code")
    val qrCode: String,
    @Json(name = "recovery_codes")
    val recoveryCodes: List<String>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class TOTPAuthenticateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData,
    override val member: MemberData,
    override val organization: OrganizationData,
) : IB2BAuthData,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class RecoveryCodeGetResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "recovery_codes")
    val recoveryCodes: List<String>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class RecoveryCodeRotateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "recovery_codes")
    val recoveryCodes: List<String>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class RecoveryCodeRecoverResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData,
    override val member: MemberData,
    override val organization: OrganizationData,
    @Json(name = "recovery_codes_remaining")
    val recoveryCodesRemaining: Int,
) : IB2BAuthData,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OAuthAuthenticateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData?,
    @Json(name = "provider_values")
    val providerValues: OAuthProviderValues,
    @Json(name = "member_id")
    override val memberId: String,
    override val member: MemberData,
    override val organization: OrganizationData,
    @Json(name = "member_authenticated")
    override val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    override val intermediateSessionToken: String?,
    @Json(name = "mfa_required")
    override val mfaRequired: MFARequired?,
) : IB2BAuthDataWithMFA,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OAuthProviderValues(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "id_token")
    val idToken: String,
    @Json(name = "refresh_token")
    val refreshToken: String? = null,
    val scopes: List<String> = emptyList(),
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSSOGetConnectionsResponseData(
    @Json(name = "saml_connections")
    val samlConnections: List<SAMLConnection>,
    @Json(name = "oidc_connections")
    val oidcConnections: List<OIDCConnection>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SAMLConnection(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "connection_id")
    val connectionId: String,
    val status: String,
    @Json(name = "attribute_mapping")
    val attributeMapping: Map<String, String>,
    @Json(name = "idp_entity_id")
    val idpEntityId: String,
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "idp_sso_url")
    val idpSSOUrl: String,
    @Json(name = "acs_url")
    val acsUrl: String,
    @Json(name = "audience_uri")
    val audienceUri: String,
    @Json(name = "signing_certificates")
    val signingCertificate: List<X509Certificate>,
    @Json(name = "verification_certificates")
    val verificationCertificates: List<X509Certificate>,
    @Json(name = "saml_connection_implicit_role_assignments")
    val samlConnectionImplicitRoleAssignments: List<ConnectionRoleAssignment>,
    @Json(name = "saml_group_implicit_role_assignments")
    val samlGroupImplicitRoleAssignments: List<GroupRoleAssignment>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class X509Certificate(
    @Json(name = "certificate_id")
    val certificateId: String,
    @Json(name = "certificate")
    val certificate: String,
    @Json(name = "issuer")
    val issuer: String,
    @Json(name = "created_at")
    val createdAt: String? = null,
    @Json(name = "expires_at")
    val expiresAt: String? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class ConnectionRoleAssignment(
    @Json(name = "role_id")
    val roleId: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class GroupRoleAssignment(
    @Json(name = "role_id")
    val roleId: String,
    val group: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OIDCConnection(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "connection_id")
    val connectionId: String,
    val status: String,
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "redirect_url")
    val redirectUrl: String,
    val issuer: String,
    @Json(name = "client_id")
    val clientId: String,
    @Json(name = "client_secret")
    val clientSecret: String,
    @Json(name = "authorization_url")
    val authorizationUrl: String,
    @Json(name = "token_url")
    val tokenUrl: String,
    @Json(name = "userinfo_url")
    val userInfoUrl: String,
    @Json(name = "jwks_url")
    val jwksUrl: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSSODeleteConnectionResponseData(
    @Json(name = "connection_id")
    val connectionId: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSSOSAMLCreateConnectionResponseData(
    val connection: SAMLConnection,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSSOOIDCCreateConnectionResponseData(
    val connection: OIDCConnection,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSSOSAMLUpdateConnectionResponseData(
    val connection: SAMLConnection,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSSOOIDCUpdateConnectionResponseData(
    val connection: OIDCConnection,
    val warning: String? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSSOSAMLUpdateConnectionByURLResponseData(
    val connection: SAMLConnection,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSSOSAMLDeleteVerificationCertificateResponseData(
    @Json(name = "certificate_id")
    val certificateId: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSearchOrganizationResponseData(
    val organization: InternalOrganizationData? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class InternalOrganizationData(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "organization_name")
    val organizationName: String,
    @Json(name = "organization_logo_url")
    val organizationLogoUrl: String?,
    @Json(name = "sso_active_connections")
    val ssoActiveConnections: List<SSOActiveConnection>?,
    @Json(name = "sso_default_connection_id")
    val ssoDefaultConnectionId: String?,
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
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSearchMemberResponseData(
    val member: InternalMemberData? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class InternalMemberData(
    val status: String,
    val name: String,
    @Json(name = "member_password_id")
    val memberPasswordId: String,
) : Parcelable

@Keep
public interface BaseSCIMConnection {
    @Json(name = "organization_id")
    public val organizationId: String

    @Json(name = "connection_id")
    public val connectionId: String
    public val status: String

    @Json(name = "display_name")
    public val displayName: String

    @Json(name = "identity_provider")
    public val identityProvider: String

    @Json(name = "base_url")
    public val baseUrl: String

    @Json(name = "scim_group_implicit_role_assignments")
    public val scimGroupImplicitRoleAssignments: List<SCIMGroupImplicitRoleAssignment>
}

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SCIMGroupImplicitRoleAssignment(
    @Json(name = "role_id")
    val roleId: String,
    @Json(name = "group_id")
    val groupId: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SCIMConnection(
    @Json(name = "organization_id")
    override val organizationId: String,
    @Json(name = "connection_id")
    override val connectionId: String,
    override val status: String,
    @Json(name = "display_name")
    override val displayName: String,
    @Json(name = "identity_provider")
    override val identityProvider: String,
    @Json(name = "base_url")
    override val baseUrl: String,
    @Json(name = "scim_group_implicit_role_assignments")
    override val scimGroupImplicitRoleAssignments: List<SCIMGroupImplicitRoleAssignment>,
    @Json(name = "bearer_token_last_four")
    val bearerTokenLastFour: String?,
    @Json(name = "bearer_token_expires_at")
    val bearerTokenExpiresAt: String?,
    @Json(name = "next_bearer_token_last_four")
    val nextBearerTokenLastFour: String?,
    @Json(name = "next_bearer_token_expires_at")
    val nextBearerTokenExpiresAt: String?,
) : BaseSCIMConnection,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SCIMConnectionWithBearerToken(
    @Json(name = "organization_id")
    override val organizationId: String,
    @Json(name = "connection_id")
    override val connectionId: String,
    override val status: String,
    @Json(name = "display_name")
    override val displayName: String,
    @Json(name = "identity_provider")
    override val identityProvider: String,
    @Json(name = "base_url")
    override val baseUrl: String,
    @Json(name = "scim_group_implicit_role_assignments")
    override val scimGroupImplicitRoleAssignments: List<SCIMGroupImplicitRoleAssignment>,
    @Json(name = "bearer_token")
    val bearerToken: String,
    @Json(name = "bearer_token_expires_at")
    val bearerTokenExpiresAt: String,
) : BaseSCIMConnection,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SCIMConnectionWithNextBearerToken(
    @Json(name = "organization_id")
    override val organizationId: String,
    @Json(name = "connection_id")
    override val connectionId: String,
    override val status: String,
    @Json(name = "display_name")
    override val displayName: String,
    @Json(name = "identity_provider")
    override val identityProvider: String,
    @Json(name = "base_url")
    override val baseUrl: String,
    @Json(name = "scim_group_implicit_role_assignments")
    override val scimGroupImplicitRoleAssignments: List<SCIMGroupImplicitRoleAssignment>,
    @Json(name = "next_bearer_token")
    val nextBearerToken: String?,
    @Json(name = "next_bearer_token_expires_at")
    val nextBearerTokenExpiresAt: String?,
    @Json(name = "bearer_token_last_four")
    val bearerTokenLastFour: String?,
    @Json(name = "bearer_token_expires_at")
    val bearerTokenExpiresAt: String?,
) : BaseSCIMConnection,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class SCIMGroup(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "connection_id")
    val connectionId: String,
    @Json(name = "group_id")
    val groupId: String,
    @Json(name = "group_name")
    val groupName: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSCIMCreateConnectionResponseData(
    val connection: SCIMConnectionWithBearerToken,
    @Json(name = "status_code")
    override val statusCode: Int,
    @Json(name = "request_id")
    override val requestId: String,
) : Parcelable,
    IBasicData

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSCIMUpdateConnectionResponseData(
    val connection: SCIMConnection,
    @Json(name = "status_code")
    override val statusCode: Int,
    @Json(name = "request_id")
    override val requestId: String,
) : Parcelable,
    IBasicData

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSCIMDeleteConnectionResponseData(
    @Json(name = "status_code")
    override val statusCode: Int,
    @Json(name = "request_id")
    override val requestId: String,
    @Json(name = "connection_id")
    val connectionId: String,
) : Parcelable,
    IBasicData

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSCIMGetConnectionResponseData(
    @Json(name = "status_code")
    override val statusCode: Int,
    @Json(name = "request_id")
    override val requestId: String,
    val connection: SCIMConnection,
) : IBasicData,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSCIMGetConnectionGroupsResponseData(
    @Json(name = "scim_groups")
    val scimGroups: List<SCIMGroup>,
    @Json(name = "next_cursor")
    val nextCursor: String? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSCIMRotateStartResponseData(
    @Json(name = "status_code")
    override val statusCode: Int,
    @Json(name = "request_id")
    override val requestId: String,
    val connection: SCIMConnectionWithNextBearerToken,
) : IBasicData,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSCIMRotateCompleteResponseData(
    @Json(name = "status_code")
    override val statusCode: Int,
    @Json(name = "request_id")
    override val requestId: String,
    val connection: SCIMConnection,
) : IBasicData,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BSCIMRotateCancelResponseData(
    @Json(name = "status_code")
    override val statusCode: Int,
    @Json(name = "request_id")
    override val requestId: String,
    val connection: SCIMConnection,
) : IBasicData,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BAuthenticationFactor(
    @Json(name = "delivery_method")
    val deliveryMethod: String,
    val type: String,
    @Json(name = "last_authenticated_at")
    val lastAuthenticatedAt: String,
    @Json(name = "email_factor")
    val emailFactor: EmailFactor?,
    @Json(name = "phone_number_factor")
    val phoneFactor: PhoneFactor?,
    @Json(name = "google_oauth_factor")
    val googleOAuthFactor: OAuthFactor?,
    @Json(name = "microsoft_oauth_factor")
    val microsoftOAuthFactor: OAuthFactor?,
    @Json(name = "github_oauth_factor")
    val githubOAuthFactor: OAuthFactor?,
    @Json(name = "hubspot_oauth_factor")
    val hubspotOAuthFactor: OAuthFactor?,
    @Json(name = "slack_oauth_factor")
    val slackOAuthFactor: OAuthFactor?,
) : Parcelable {
    @Keep
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class EmailFactor(
        @Json(name = "email_id")
        val emailId: String,
        @Json(name = "email_address")
        val emailAddress: String,
    ) : Parcelable

    @Keep
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class PhoneFactor(
        @Json(name = "phone_id")
        val phoneId: String,
        @Json(name = "phone_number")
        val phoneNumber: String,
    ) : Parcelable

    @Keep
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class OAuthFactor(
        val id: String,
        @Json(name = "email_id")
        val emailId: String? = null,
        @Json(name = "provider_subject")
        val providerSubject: String,
        @Json(name = "provider_tenant_id")
        val providerTenantId: String? = null,
    ) : Parcelable
}

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BOTPsEmailLoginOrSignupResponseData(
    @Json(name = "status_code")
    override val statusCode: Int,
    @Json(name = "request_id")
    override val requestId: String,
) : Parcelable,
    IBasicData

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BOTPsEmailAuthenticateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData?,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "member_id")
    override val memberId: String,
    override val member: MemberData,
    override val organization: OrganizationData,
    @Json(name = "member_authenticated")
    override val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    override val intermediateSessionToken: String?,
    @Json(name = "mfa_required")
    override val mfaRequired: MFARequired?,
    @Json(name = "method_id")
    val methodId: String,
) : IB2BAuthDataWithMFA,
    Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BDiscoveryOTPEmailSendResponseData(
    @Json(name = "status_code")
    override val statusCode: Int,
    @Json(name = "request_id")
    override val requestId: String,
) : Parcelable,
    IBasicData

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BDiscoveryOTPEmailAuthenticateResponseData(
    @Json(name = "email_address")
    val emailAddress: String,
    @Json(name = "discovered_organizations")
    val discoveredOrganizations: List<DiscoveredOrganization>,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "intermediate_session_token")
    val intermediateSessionToken: String,
) : Parcelable

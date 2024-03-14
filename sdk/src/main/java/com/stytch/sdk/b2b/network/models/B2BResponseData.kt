package com.stytch.sdk.b2b.network.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.models.AuthenticationFactor
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

public interface IB2BAuthData : CommonAuthenticationData {
    public val memberSession: B2BSessionData
    public override val sessionJwt: String
    public override val sessionToken: String
    public val member: MemberData
    public val organization: OrganizationData
}

@JsonClass(generateAdapter = true)
@Parcelize
public data class B2BAuthData(
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
) : IB2BAuthData, Parcelable

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
    override val memberSession: B2BSessionData,
    @Json(name = "reset_sessions")
    val resetSessions: Boolean,
    override val organization: OrganizationData,
) : IB2BAuthData, Parcelable

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
    val authenticationFactors: List<AuthenticationFactor>,
    @Json(name = "custom_claims")
    val customClaims: @RawValue Map<String, Any?>?,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class MemberResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val member: MemberData,
) : Parcelable

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

@JsonClass(generateAdapter = true)
@Parcelize
public data class OrganizationResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val organization: OrganizationData,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class OrganizationData(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "organization_name")
    val organizatioName: String,
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

@JsonClass(generateAdapter = true)
@Parcelize
public data class SSOActiveConnection(
    @Json(name = "connection_id")
    val connectionId: String,
    @Json(name = "display_name")
    val displayName: String,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class PasswordsAuthenticateResponseData(
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
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "organization_id")
    val organizationId: String,
) : IB2BAuthData, Parcelable

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
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "member_email_id")
    val memberEmailId: String,
) : IB2BAuthData, Parcelable

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
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class ZXCVBNFeedback(
        val suggestions: List<String>,
        val warning: String,
    ) : Parcelable

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

@JsonClass(generateAdapter = true)
@Parcelize
public data class DiscoveredOrganizationsResponseData(
    @Json(name = "email_address")
    val emailAddress: String,
    @Json(name = "discovered_organizations")
    val discoveredOrganizations: List<DiscoveredOrganization>,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class DiscoveredOrganization(
    val organization: OrganizationData,
    val membership: Membership,
    @Json(name = "member_authenticated")
    val memberAuthenticated: Boolean,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class Membership(
    val type: String,
    val details: MembershipDetails?,
    val member: MemberData?,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class MembershipDetails(
    val domain: String,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class IntermediateSessionExchangeResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    val sessionJwt: String,
    @Json(name = "session_token")
    val sessionToken: String,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class OrganizationCreateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    val sessionJwt: String,
    @Json(name = "session_token")
    val sessionToken: String,
) : Parcelable

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

@JsonClass(generateAdapter = true)
@Parcelize
public data class SSOAuthenticateResponseData(
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
) : IB2BAuthData, Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class SessionExchangeResponseData(
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
    @Json(name = "member_authenticated")
    val memberAuthenticated: Boolean,
    @Json(name = "intermediate_session_token")
    val intermediateSessionToken: String,
) : IB2BAuthData, Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class UpdateMemberResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val member: MemberData,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class MemberDeleteAuthenticationFactorData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val member: MemberData,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class OrganizationUpdateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val organization: OrganizationData,
) : Parcelable

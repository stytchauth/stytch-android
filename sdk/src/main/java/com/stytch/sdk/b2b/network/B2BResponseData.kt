package com.stytch.sdk.b2b.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.AuthenticationFactor
import com.stytch.sdk.common.network.CommonAuthenticationData

public interface IB2BAuthData : CommonAuthenticationData {
    public val memberSession: B2BSessionData
    public override val sessionJwt: String
    public override val sessionToken: String
    public val member: MemberData
    public val organization: Organization
}

@JsonClass(generateAdapter = true)
public data class B2BAuthData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    override val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val member: MemberData,
    override val organization: Organization
) : IB2BAuthData

@JsonClass(generateAdapter = true)
public data class B2BEMLAuthenticateData(
    @Json(name = "member_id")
    val memberId: String,
    override val member: MemberData,
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "method_id")
    val methodId: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    override val memberSession: B2BSessionData,
    @Json(name = "reset_sessions")
    val resetSessions: Boolean,
    override val organization: Organization
) : IB2BAuthData

@JsonClass(generateAdapter = true)
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
    val customClaims: Map<String, Any?>?,
)

@JsonClass(generateAdapter = true)
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
    val trustedMetadata: Map<String, Any?>?,
    @Json(name = "untrusted_metadata")
    val untrustedMetadata: Map<String, Any?>?,
    @Json(name = "sso_registrations")
    val ssoRegistrations: List<SSORegistration>
)

@JsonClass(generateAdapter = true)
public data class SSORegistration(
    @Json(name = "connection_id")
    val connectionId: String,
    @Json(name = "external_id")
    val externalId: String,
    @Json(name = "registration_id")
    val registrationId: String,
    @Json(name = "sso_attributes")
    val ssoAttributes: Map<String, Any?>?,
)

@JsonClass(generateAdapter = true)
public data class Organization(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "organization_name")
    val organizatioName: String,
    @Json(name = "organization_slug")
    val organizationSlug: String,
    @Json(name = "organization_logo_url")
    val organizationLogoUrl: String,
    @Json(name = "trusted_metadata")
    val trustedMetadata: Map<String, Any?>,
)

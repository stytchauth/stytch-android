package com.stytch.sdk.b2b.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

public interface IB2BAuthData {
    public val session: B2BSessionData
    public val sessionJwt: String
    public val sessionToken: String
    public val member: MemberData
}

@JsonClass(generateAdapter = true)
public data class B2BAuthData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    override val session: B2BSessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val member: MemberData
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
    override val session: B2BSessionData,
    @Json(name = "reset_sessions")
    val resetSessions: Boolean

) : IB2BAuthData

@JsonClass(generateAdapter = true)
public data class B2BSessionData(
    @Json(name = "member_session_id")
    val memberSessionId: String,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "started_at")
    val startedAt: String,
    @Json(name = "last_accessed_at")
    val lastAccessedAt: String,
    @Json(name = "expires_at")
    val expiresAt: String,
    @Json(name = "custom_claims")
    val customClaims: Map<String, String>?,
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
    val name: String
)

public enum class SSOJitProvisioning {
    ALL_ALLOWED,
    RESTRICTED,
    NOT_ALLOWED,
}

public enum class EmailJitProvisioning {
    RESTRICTED,
    NOT_ALLOWED,
}

public enum class EmailInvites {
    ALL_ALLOWED,
    RESTRICTED,
    NOT_ALLOWED,
}

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
    @Json(name = "sso_default_connection_id")
    val ssoDefaultConnectionId: String?,
    @Json(name = "sso_jit_provisioning")
    val ssoJitProvisioning: SSOJitProvisioning,
    @Json(name = "sso_jit_provisioning_allowed_connections")
    val ssoJITProvisioningAllowedConnections: List<String>,
    @Json(name = "sso_active_connections")
    val ssoActiveConnections: List<SSOConnection>,
    @Json(name = "email_allowed_domains")
    val emailAllowedDomains: List<String>,
    @Json(name = "email_jit_provisioning")
    val emailJitProvisioning: EmailJitProvisioning,
    @Json(name = "email_invites")
    val emailInvites: EmailInvites,
)

@JsonClass(generateAdapter = true)
public data class SSOConnection(
    @Json(name = "connection_id")
    val connectionId: String,
    @Json(name = "display_name")
    val displayName: String,
)

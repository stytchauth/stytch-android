package com.stytch.sdk.b2b.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.sessions.ISessionData

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
) : ISessionData

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

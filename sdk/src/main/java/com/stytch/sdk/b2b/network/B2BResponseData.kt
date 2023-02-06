package com.stytch.sdk.b2b.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.BiometricRegistrations
import com.stytch.sdk.common.network.CryptoWalletData
import com.stytch.sdk.common.network.EmailData
import com.stytch.sdk.common.network.NameData
import com.stytch.sdk.common.network.Password
import com.stytch.sdk.common.network.PhoneNumber
import com.stytch.sdk.common.network.Provider
import com.stytch.sdk.common.network.TOTP
import com.stytch.sdk.common.network.WebAuthNRegistrations

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
public data class B2BSessionData(
    val attributes: AttributesData,
    @Json(name = "custom_claims")
    val customClaims: Map<String, String>?,
    @Json(name = "expires_at")
    val expiresAt: String,
    @Json(name = "last_accessed_at")
    val lastAccessedAt: String,
    @Json(name = "session_id")
    val sessionId: String,
    @Json(name = "started_at")
    val startedAt: String,
    @Json(name = "member_id")
    val memberId: String,
) {
    @JsonClass(generateAdapter = true)
    public data class AttributesData(
        @Json(name = "ip_address")
        val ipAddress: String,
        @Json(name = "user_agent")
        val userAgent: String,
    )
}

@JsonClass(generateAdapter = true)
public data class MemberData(
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "crypto_wallets")
    val cryptoWallets: List<CryptoWalletData>,
    val emails: List<EmailData>,
    val name: NameData,
    val password: Password?,
    @Json(name = "phone_numbers")
    val phoneNumbers: List<PhoneNumber>,
    val providers: List<Provider>,
    @Json(name = "request_id")
    val requestId: String?,
    val status: String,
    @Json(name = "status_code")
    val statusCode: Int?,
    val totps: List<TOTP>,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "webauthn_registrations")
    val webauthnRegistrations: List<WebAuthNRegistrations>,
    @Json(name = "biometric_registrations")
    val biometricRegistrations: List<BiometricRegistrations>
)

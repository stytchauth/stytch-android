package com.stytch.sdk.network.responseData

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class BasicData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
)

@JsonClass(generateAdapter = true)
public data class StytchErrorResponse(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "error_type")
    val errorType: String,
    @Json(name = "error_message")
    val errorMessage: String?,
    @Json(name = "error_url")
    val errorUrl: String,
)

public interface IAuthData {
    public val session: SessionData
    public val sessionJwt: String
    public val sessionToken: String
    public val user: UserData
}

@JsonClass(generateAdapter = true)
public data class AuthData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
) : IAuthData

@JsonClass(generateAdapter = true)
public data class LoginOrCreateOTPData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "method_id")
    val methodId: String,
)

@JsonClass(generateAdapter = true)
public data class SessionData(
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
    @Json(name = "user_id")
    val userId: String,
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
public data class UserData(
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
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "webauthn_registrations")
    val webauthnRegistrations: List<WebAuthNRegistrations>,
) {

    @JsonClass(generateAdapter = true)
    public data class CryptoWalletData(
        // The id of the crypto wallet.
        @Json(name = "crypto_wallet_id")
        val id: String,

        // The address of the cryptowallet.
        @Json(name = "crypto_wallet_address")
        val address: String,
        @Json(name = "crypto_wallet_type")
        val walletType: String,
        // The verification status of the cryptowallet.
        val verified: Boolean,
    )

    @JsonClass(generateAdapter = true)
    public data class EmailData(
        public val email: String,
        @Json(name = "email_id")
        public val emailId: String,
        public val verified: Boolean,
    )

    @JsonClass(generateAdapter = true)
    public data class NameData(
        @Json(name = "first_name")
        val firstName: String?,
        @Json(name = "last_name")
        val lastName: String?,
        @Json(name = "middle_name")
        val middleName: String?,
    )

    @JsonClass(generateAdapter = true)
    public data class WebAuthNRegistrations(
        // The domain of the WebAuthN registration.
        val domain: String,
        // The user agent of the registration.
        val userAgent: String,
        // The verification status of the registration.
        val verified: Boolean,
        // The id of the registration.
        @Json(name = "webauthn_registration_id")
        val id: String,
    )

    @JsonClass(generateAdapter = true)
    public data class PhoneNumber(
        // The phone number.
        @Json(name = "phone_number")
        val phoneNumber: String,
        // The id of the phone number.
        @Json(name = "phone_id")
        val id: String,
        // The verification status of the phone number.
        val verified: Boolean,
    )

    @JsonClass(generateAdapter = true)
    public data class Provider(
        // The subject of the provider.
        @Json(name = "provider_subject")
        val providerSubject: String,
        // The type of the provider.
        @Json(name = "provider_type")
        val providerType: String,
    )

    @JsonClass(generateAdapter = true)
    public data class TOTP(
        // / The id of the TOTP.
        @Json(name = "totpId")
        var id: String,
        // / The verification status of the TOTP.
        val verified: Boolean,
    )
}

@JsonClass(generateAdapter = true)
public data class Password(
    @Json(name = "password_id")
    val passwordId: String,
    @Json(name = "requires_reset")
    val requiresReset: Boolean,
)

@JsonClass(generateAdapter = true)
public data class Feedback(
    val suggestions: List<String>,
    val warning: String,
)

@JsonClass(generateAdapter = true)
public data class CreateResponse(
    @Json(name = "method_id")
    val methodId: String?,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "reset_sessions")
    val resetSessions: Boolean?,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
    @Json(name = "email_id")
    val emailId: String,
    @Json(name = "user_id")
    val userId: String,
) : IAuthData

@JsonClass(generateAdapter = true)
public data class StrengthCheckResponse(
    @Json(name = "breached_password")
    val breachedPassword: Boolean,
    val feedback: Feedback,
    @Json(name = "request_id")
    val requestId: String,
    val score: Int,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "valid_password")
    val validPassword: Boolean,
)

package com.stytch.sdk.network.responseData

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class BasicData(
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "status_code")
    val statusCode: Int,
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

@JsonClass(generateAdapter = true)
public data class AuthData(
    val session: SessionData,
    @Json(name = "session_jwt")
    val sessionJwt: String,
    @Json(name = "session_token")
    val sessionToken: String,
    val user: UserData,
)

@JsonClass(generateAdapter = true)
public data class SessionData(
    val attributes: AttributesData,
    @Json(name = "custom_claims")
    val customClaims: HashMap<String,String>?,
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
    val password: String?,
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
        @Json(name = "provider_typeval")
        val providerTypeval: String,
    )

    @JsonClass(generateAdapter = true)
    public data class TOTP(
        /// The id of the TOTP.
        @Json(name = "totpId")
        var id: String,
        /// The verification status of the TOTP.
        val verified: Boolean,
    )
}

@JsonClass(generateAdapter = true)
public data class NameData(
    val first_name: String,
    val last_name: String,
    val middle_name: String,
)

@JsonClass(generateAdapter = true)
public data class Attributes(
    val ip_address: String,
    val user_agent: String
)

@JsonClass(generateAdapter = true)
public data class AuthenticationFactor(
    val delivery_method: String,
    val last_authenticated_at: String,
    val type: String
)

@JsonClass(generateAdapter = true)
public data class Email(
    val email: String,
    val email_id: String,
    val verified: Boolean
)

@JsonClass(generateAdapter = true)
public data class Name(
    val first_name: String,
    val last_name: String,
    val middle_name: String
)

@JsonClass(generateAdapter = true)
public data class Password(
    val password_id: String,
    val requires_reset: Boolean
)

@JsonClass(generateAdapter = true)
public data class Session(
    val attributes: Attributes,
    val authentication_factors: List<AuthenticationFactor>,
    val custom_claims: Any,
    val expires_at: String,
    val last_accessed_at: String,
    val session_id: String,
    val started_at: String,
    val user_id: String
)

@JsonClass(generateAdapter = true)
public data class User(
    val biometric_registrations: List<Any>,
    val created_at: String,
    val crypto_wallets: List<Any>,
    val emails: List<Email>,
    val name: Name,
    val password: Password,
    val phone_numbers: List<Any>,
    val providers: List<Any>,
    val status: String,
    val totps: List<Any>,
    val user_id: String,
    val webauthn_registrations: List<Any>
)

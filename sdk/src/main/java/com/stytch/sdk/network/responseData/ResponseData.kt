package com.stytch.sdk.network.responseData

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class BasicData(
    val request_id: String,
    val status_code: Int,
)

@JsonClass(generateAdapter = true)
public data class StytchErrorResponse(
    val status_code: Int,
    val request_id: String,
    val error_type: String,
    val error_message: String?,
    val error_url: String,
)

@JsonClass(generateAdapter = true)
public data class AuthData(
    val method_id: String?,
    val request_id: String,
    val status_code: Int,
    val reset_sessions: Boolean?,
    val session: SessionData,
    val session_jwt: String,
    val session_token: String,
    val user: UserData,
)

@JsonClass(generateAdapter = true)
public data class SessionData(
    val attributes: AttributesData,
    val authentication_factors: List<Any>,
    val custom_claims: Any?,
    val expires_at: String,
    val last_accessed_at: String,
    val session_id: String,
    val started_at: String,
    val user_id: String,
)

@JsonClass(generateAdapter = true)
public data class AttributesData(
    val ip_address: String,
    val user_agent: String,
)

@JsonClass(generateAdapter = true)
public data class UserData(
    val biometric_registrations: List<Any>,
    val created_at: String,
    val crypto_wallets: List<Any>,
    val emails: List<EmailData>,
    val name: NameData,
    val password: String?,
    val phone_numbers: List<Any>,
    val providers: List<Any>,
    val request_id: String?,
    val status: String,
    val status_code: Int?,
    val totps: List<Any>,
    val user_id: String,
    val webauthn_registrations: List<Any>,
)

@JsonClass(generateAdapter = true)
public data class EmailData(
    val email: String,
    val email_id: String,
    val verified: Boolean
)

@JsonClass(generateAdapter = true)
public data class NameData(
    val first_name: String,
    val last_name: String,
    val middle_name: String,
)
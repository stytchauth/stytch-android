package com.stytch.sdk.consumer.network.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.models.AuthenticationFactor
import com.stytch.sdk.common.network.models.BiometricRegistrations
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import com.stytch.sdk.common.network.models.CryptoWalletData
import com.stytch.sdk.common.network.models.EmailData
import com.stytch.sdk.common.network.models.Feedback
import com.stytch.sdk.common.network.models.NameData
import com.stytch.sdk.common.network.models.Password
import com.stytch.sdk.common.network.models.PhoneNumber
import com.stytch.sdk.common.network.models.Provider
import com.stytch.sdk.common.network.models.SDKDeviceHistory
import com.stytch.sdk.common.network.models.StrengthPolicy
import com.stytch.sdk.common.network.models.TOTP
import com.stytch.sdk.common.network.models.WebAuthNRegistrations
import com.stytch.sdk.common.utils.IEnumValue
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.Date

public interface IAuthData : CommonAuthenticationData {
    public val session: SessionData
    public override val sessionJwt: String
    public override val sessionToken: String
    public val user: UserData
}

public interface INativeOAuthData : IAuthData {
    public val userCreated: Boolean
}

@JsonClass(generateAdapter = true)
@Parcelize
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
) : IAuthData,
    Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class SessionData(
    val attributes: AttributesData,
    @Json(name = "custom_claims")
    val customClaims: @RawValue Map<String, Any?>?,
    @Json(name = "expires_at")
    val expiresAt: Date,
    @Json(name = "last_accessed_at")
    val lastAccessedAt: Date,
    @Json(name = "session_id")
    val sessionId: String,
    @Json(name = "started_at")
    val startedAt: Date,
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "authentication_factors")
    val authenticationFactors: List<AuthenticationFactor>,
) : Parcelable {
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class AttributesData(
        @Json(name = "ip_address")
        val ipAddress: String,
        @Json(name = "user_agent")
        val userAgent: String,
    ) : Parcelable
}

@JsonClass(generateAdapter = true)
@Parcelize
public data class UserData(
    @Json(name = "created_at")
    val createdAt: Date,
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
    @Json(name = "biometric_registrations")
    val biometricRegistrations: List<BiometricRegistrations>,
    @Json(name = "trusted_metadata")
    val trustedMetadata: @RawValue Map<String, Any?>?,
    @Json(name = "untrusted_metadata")
    val untrustedMetadata: @RawValue Map<String, Any?>?,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
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
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
) : IAuthData,
    Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class BiometricsAuthData(
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
    @Json(name = "biometric_registration_id")
    val biometricRegistrationId: String,
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
) : IAuthData,
    Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class DeleteAuthenticationFactorData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val user: UserData,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class OAuthData(
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
    @Json(name = "oauth_user_registration_id")
    val oauthUserRegistrationId: String,
    @Json(name = "provider_subject")
    val providerSubject: String,
    @Json(name = "provider_type")
    val providerType: String,
    @Json(name = "provider_values")
    val providerValues: OAuthProviderValues,
) : IAuthData,
    Parcelable {
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class OAuthProviderValues(
        @Json(name = "access_token")
        val accessToken: String,
        @Json(name = "refresh_token")
        val refreshToken: String,
        @Json(name = "id_token")
        val idToken: String,
        val scopes: List<String>,
    ) : Parcelable
}

@JsonClass(generateAdapter = true)
@Parcelize
public data class NativeOAuthData(
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
    @Json(name = "user_created")
    override val userCreated: Boolean,
) : INativeOAuthData,
    Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
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
    @Json(name = "strength_policy")
    val strengthPolicy: StrengthPolicy,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class UpdateUserResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val user: UserData,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class UserSearchResponseData(
    val userType: UserType,
) : Parcelable

@JsonClass(generateAdapter = false)
@Parcelize
public enum class UserType : Parcelable {
    @Json(name = "new")
    NEW,

    @Json(name = "password")
    PASSWORD,

    @Json(name = "passwordless")
    PASSWORDLESS,
}

@JsonClass(generateAdapter = true)
@Parcelize
public data class WebAuthnRegisterStartData(
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "public_key_credential_creation_options")
    val publicKeyCredentialCreationOptions: String,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class WebAuthnRegisterData(
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
    @Json(name = "webauthn_registration_id")
    val webauthnRegistrationId: String,
) : Parcelable,
    IAuthData

@JsonClass(generateAdapter = true)
@Parcelize
public data class WebAuthnAuthenticateStartData(
    @Json(name = "public_key_credential_request_options")
    val publicKeyCredentialRequestOptions: String,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class WebAuthnUpdateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "webauthn_registration")
    val webauthnRegistration: WebAuthNRegistrations,
) : Parcelable

@JsonClass(generateAdapter = false)
@Parcelize
public enum class CryptoWalletType(
    override val jsonName: String,
) : IEnumValue,
    Parcelable {
    ETHEREUM("ethereum"),
    SOLANA("solana"),
}

@JsonClass(generateAdapter = true)
@Parcelize
public data class CryptoWalletAuthenticateStartResponseData(
    val challenge: String,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class TOTPCreateResponseData(
    @Json(name = "totp_id")
    val totpId: String,
    val secret: String,
    @Json(name = "qr_code")
    val qrCode: String,
    @Json(name = "recovery_codes")
    val recoveryCodes: List<String>,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class TOTPAuthenticateResponseData(
    @Json(name = "totp_id")
    val totpId: String,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
) : IAuthData,
    Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class TOTPRecoveryCodesResponseData(
    @Json(name = "user_id")
    val userId: String,
    val totps: List<TOTPRecovery>,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class TOTPRecovery(
    @Json(name = "totp_id")
    val totpId: String,
    val verified: Boolean,
    @Json(name = "recovery_codes")
    val recoveryCodes: List<String>,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class TOTPRecoverResponseData(
    @Json(name = "totp_id")
    val totpId: String,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
) : IAuthData,
    Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
public data class CryptoWalletsAuthenticateResponseData(
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
) : Parcelable,
    IAuthData

@JsonClass(generateAdapter = true)
@Parcelize
public data class OTPsAuthenticateResponseData(
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
) : Parcelable,
    IAuthData

@JsonClass(generateAdapter = true)
@Parcelize
public data class B2CPasswordsAuthenticateResponseData(
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
) : Parcelable,
    IAuthData

@JsonClass(generateAdapter = true)
@Parcelize
public data class PasswordsEmailResetResponseData(
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
) : Parcelable,
    IAuthData

@JsonClass(generateAdapter = true)
@Parcelize
public data class PasswordsExistingPasswordResetResponseData(
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
) : Parcelable,
    IAuthData

@JsonClass(generateAdapter = true)
@Parcelize
public data class PasswordsSessionResetResponseData(
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
) : Parcelable,
    IAuthData

@JsonClass(generateAdapter = true)
@Parcelize
public data class WebAuthnAuthenticateResponseData(
    @Json(name = "user_device")
    val userDevice: SDKDeviceHistory?,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
) : Parcelable,
    IAuthData

@JsonClass(generateAdapter = true)
@Parcelize
public data class SessionAttestResponseData(
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
) : Parcelable,
    IAuthData

@JsonClass(generateAdapter = true)
@Parcelize
public data class OAuthAttachResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "oauth_attach_token")
    val oauthAttachToken: String,
) : Parcelable

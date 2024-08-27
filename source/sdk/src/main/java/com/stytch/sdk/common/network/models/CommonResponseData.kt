package com.stytch.sdk.common.network.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
public interface IBasicData {
    public val statusCode: Int
    public val requestId: String
}

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class BasicData(
    @Json(name = "status_code")
    override val statusCode: Int,
    @Json(name = "request_id")
    override val requestId: String,
) : Parcelable,
    IBasicData

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class StytchErrorResponse(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String? = null,
    @Json(name = "error_type")
    val errorType: String,
    @Json(name = "error_message")
    val errorMessage: String?,
    @Json(name = "error_url")
    val errorUrl: String? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class StytchSchemaError(
    val body: @RawValue Any?,
    val params: @RawValue Any?,
    val query: @RawValue Any?,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
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
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class EmailData(
    public val email: String,
    @Json(name = "email_id")
    public val emailId: String,
    public val verified: Boolean,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class NameData(
    @Json(name = "first_name")
    val firstName: String?,
    @Json(name = "last_name")
    val lastName: String?,
    @Json(name = "middle_name")
    val middleName: String?,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class WebAuthNRegistrations(
    // The domain of the WebAuthN registration.
    val domain: String,
    // The user agent of the registration.
    val userAgent: String? = null,
    // The verification status of the registration.
    val verified: Boolean,
    // The id of the registration.
    @Json(name = "webauthn_registration_id")
    val id: String,
    // the name of the registration
    val name: String? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class PhoneNumber(
    // The phone number.
    @Json(name = "phone_number")
    val phoneNumber: String,
    // The id of the phone number.
    @Json(name = "phone_id")
    val id: String,
    // The verification status of the phone number.
    val verified: Boolean,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class Provider(
    // The subject of the provider.
    @Json(name = "provider_subject")
    val providerSubject: String,
    // The type of the provider.
    @Json(name = "provider_type")
    val providerType: String,
    // The profile picture url, if available
    @Json(name = "profile_picture_url")
    val profilePictureUrl: String? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class TOTP(
    // The id of the TOTP.
    @Json(name = "totp_id")
    var id: String,
    // The verification status of the TOTP.
    val verified: Boolean,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class BiometricRegistrations(
    // The id of the biometric registration
    @Json(name = "biometric_registration_id")
    val id: String,
    // The verification status of the biometric registration.
    val verified: Boolean,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class Password(
    @Json(name = "password_id")
    val passwordId: String,
    @Json(name = "requires_reset")
    val requiresReset: Boolean,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class BiometricsStartResponse(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "biometric_registration_id")
    val biometricRegistrationId: String,
    val challenge: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class LoginOrCreateOTPData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "method_id")
    val methodId: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class OTPSendResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "method_id")
    val methodId: String,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class AuthenticationFactor(
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
    @Json(name = "apple_oauth_factor")
    val appleOAuthFactor: OAuthFactor?,
    @Json(name = "github_oauth_factor")
    val githubOAuthFactor: OAuthFactor?,
    @Json(name = "gitlab_oauth_factor")
    val gitlabOAuthFactor: OAuthFactor?,
    @Json(name = "facebook_oauth_factor")
    val facebookOAuthFactor: OAuthFactor?,
    @Json(name = "discord_oauth_factor")
    val discordOAuthFactor: OAuthFactor?,
    @Json(name = "salesforce_oauth_factor")
    val salesforceOAuthFactor: OAuthFactor?,
    @Json(name = "yahoo_oauth_factor")
    val yahooOAuthFactor: OAuthFactor?,
    @Json(name = "slack_oauth_factor")
    val slackOAuthFactor: OAuthFactor?,
    @Json(name = "amazon_oauth_factor")
    val amazonOAuthFactor: OAuthFactor?,
    @Json(name = "bitbucket_oauth_factor")
    val bitbucketOAuthFactor: OAuthFactor?,
    @Json(name = "linkedin_oauth_factor")
    val linkedinOAuthFactor: OAuthFactor?,
    @Json(name = "coinbase_oauth_factor")
    val coinbaseOAuthFactor: OAuthFactor?,
    @Json(name = "twitch_oauth_factor")
    val twitchOAuthFactor: OAuthFactor?,
    @Json(name = "twitter_oauth_factor")
    val twitterOAuthFactor: OAuthFactor?,
    @Json(name = "tiktok_oauth_factor")
    val tiktokOAuthFactor: OAuthFactor?,
    @Json(name = "snapchat_oauth_factor")
    val snapchatOAuthFactor: OAuthFactor?,
    @Json(name = "figma_oauth_factor")
    val figmaOAuthFactor: OAuthFactor?,
    @Json(name = "webauthn_factor")
    val webAuthnFactor: WebAuthnFactor?,
    @Json(name = "biometric_factor")
    val biometricFactor: BiometricFactor?,
    @Json(name = "authenticator_app_factor")
    val authenticatorAppFactor: AuthenticatorAppFactor?,
    @Json(name = "recovery_code_factor")
    val recoveryCodeFactor: RecoveryCodeFactor?,
    @Json(name = "crypto_wallet_factor")
    val cryptoWalletFactor: CryptoWalletFactor?,
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
    ) : Parcelable

    @Keep
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class WebAuthnFactor(
        @Json(name = "webauthn_registration_id")
        val webauthnRegistrationId: String,
        val domain: String,
        @Json(name = "user_agent")
        val userAgent: String,
    ) : Parcelable

    @Keep
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class BiometricFactor(
        @Json(name = "biometric_registration_id")
        val biometricRegistrationId: String,
    ) : Parcelable

    @Keep
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class AuthenticatorAppFactor(
        @Json(name = "totp_id")
        val totpId: String,
    ) : Parcelable

    @Keep
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class RecoveryCodeFactor(
        @Json(name = "totp_recovery_code_id")
        val totpRecoveryCodeId: String,
    ) : Parcelable

    @Keep
    @JsonClass(generateAdapter = true)
    @Parcelize
    public data class CryptoWalletFactor(
        @Json(name = "crypto_wallet_id")
        val cryptoWalletId: String,
        @Json(name = "crypto_wallet_address")
        val cryptoWalletAddress: String,
        @Json(name = "crypto_wallet_type")
        val cryptoWalletType: String,
    ) : Parcelable
}

@Keep
public interface CommonAuthenticationData {
    public val sessionJwt: String
    public val sessionToken: String
}

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class BootstrapData(
    @Json(name = "disable_sdk_watermark")
    val disableSDKWatermark: Boolean = false,
    @Json(name = "cname_domain")
    val cnameDomain: String? = null,
    @Json(name = "email_domains")
    val emailDomains: List<String> = listOf("stytch.com"),
    @Json(name = "captcha_settings")
    val captchaSettings: CaptchaSettings = CaptchaSettings(),
    @Json(name = "pkce_required_for_email_magic_links")
    val pkceRequiredForEmailMagicLinks: Boolean = false,
    @Json(name = "pkce_required_for_password_resets")
    val pkceRequiredForPasswordResets: Boolean = false,
    @Json(name = "pkce_required_for_oauth")
    val pkceRequiredForOAuth: Boolean = false,
    @Json(name = "pkce_required_for_sso")
    val pkceRequiredForSso: Boolean = false,
    @Json(name = "slug_pattern")
    val slugPattern: String? = null,
    @Json(name = "create_organization_enabled")
    val createOrganizationEnabled: Boolean = false,
    @Json(name = "dfp_protected_auth_enabled")
    val dfpProtectedAuthEnabled: Boolean = false,
    @Json(name = "dfp_protected_auth_mode")
    val dfpProtectedAuthMode: DFPProtectedAuthMode? = DFPProtectedAuthMode.OBSERVATION,
    @Json(name = "password_config")
    val passwordConfig: PasswordConfig? = null,
    @Json(name = "rbac_policy")
    val rbacPolicy: RBACPolicy? = null,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class RBACPolicy(
    val roles: List<RBACPolicyRole>,
    val resources: List<RBACPolicyResource>,
) : Parcelable {
    public val rolesByID: Map<String, RBACPolicyRole>
        get() {
            val map = mutableMapOf<String, RBACPolicyRole>()
            roles.forEach { role ->
                map[role.roleId] = role
            }
            return map
        }

    public fun callerIsAuthorized(
        memberRoles: List<String>,
        resourceId: String,
        action: String,
    ): Boolean {
        val permission =
            memberRoles
                .mapNotNull { roleId -> rolesByID[roleId] }
                .flatMap { role -> role.permissions }
                .filter { permission -> permission.resourceId == resourceId }
                .find { permission ->
                    permission.actions.contains(action) || permission.actions.contains("*")
                }
        return permission != null
    }

    public fun allPermissionsForCaller(memberRoles: List<String>): Map<String, Map<String, Boolean>> {
        val allPermissionsMap = mutableMapOf<String, Map<String, Boolean>>()
        resources.forEach { resource ->
            val actionMap = mutableMapOf<String, Boolean>()
            resource.actions.forEach { action ->
                actionMap[action] = callerIsAuthorized(memberRoles, resource.resourceId, action)
            }
            allPermissionsMap[resource.resourceId] = actionMap
        }
        return allPermissionsMap
    }
}

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class RBACPolicyRole(
    @Json(name = "role_id")
    val roleId: String,
    val description: String,
    val permissions: List<RBACPermission>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class RBACPermission(
    @Json(name = "resource_id")
    val resourceId: String,
    val actions: List<String>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class RBACPolicyResource(
    @Json(name = "resource_id")
    val resourceId: String,
    val description: String,
    val actions: List<String>,
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class PasswordConfig(
    @Json(name = "luds_complexity")
    val ludsComplexity: Int,
    @Json(name = "luds_minimum_count")
    val ludsMinimumCount: Int,
) : Parcelable

@Keep
@JsonClass(generateAdapter = false)
public enum class DFPProtectedAuthMode {
    OBSERVATION,
    DECISIONING,
}

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public data class CaptchaSettings(
    val enabled: Boolean = false,
    val siteKey: String? = "",
) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
public class NoResponseData : Parcelable

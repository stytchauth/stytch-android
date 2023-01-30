package com.stytch.sdk.network

@Suppress("MaxLineLength")
public enum class StytchErrorType(
    public val stringValue: String,
    public val message: String = "",
) {
    EMAIL_NOT_FOUND("email_not_found"),
    BILLING_NOT_VERIFIED_FOR_EMAIL("billing_not_verified_for_email"),
    UNABLE_TO_AUTH_MAGIC_LINK("unable_to_auth_magic_link"),
    INVALID_USER_ID("invalid_user_id"),
    UNAUTHORIZED_CREDENTIALS("unauthorized_credentials"),
    INTERNAL_SERVER_ERROR("internal_server_error"),
    TOO_MANY_REQUESTS("too_many_requests"),
    INVALID_PHONE_NUMBER("invalid_phone_number"),
    UNABLE_TO_AUTH_OTP_CODE("unable_to_auth_otp_code"),
    OTP_CODE_NOT_FOUND("otp_code_not_found"),
    NO_CURRENT_SESSION(
        stringValue = "no_current_session",
        message = "There is no session currently available. Must authenticate prior to calling this method."
    ),
    BIOMETRICS_ALREADY_EXISTS(
        stringValue = "biometrics_registration_exists",
        message = "There is already a biometric registration saved for this device. Did you mean to authenticate?"
    ),
    NO_BIOMETRICS_REGISTRATIONS_AVAILABLE(
        stringValue = "no_biometric_registrations",
        message = "There are no biometric registrations available. Must authenticate with other methods and add a new biometric registration before calling this method." // ktlint-disable maximum-line-length
    ),
    KEY_GENERATION_FAILED(
        stringValue = "key_generation_failed",
        message = "Failed to generate or retrieve the Ed25519 public key."
    ),
    ERROR_SIGNING_CHALLENGE(
        stringValue = "error_signing_challenge",
        message = "Failed to sign challenge with the Ed25519 key"
    ),
    NOT_USING_KEYSTORE(
        stringValue = "not_using_keystore",
        message = "Biometric keys will be stored in cleartext on this device, but allowFallbackToCleartext was not enabled. Please read documentation around the impact of this usage" // ktlint-disable maximum-line-length
    ),
    ERROR_DERIVING_PUBLIC_KEY(
        stringValue = "error_deriving_public_key",
        message = "There was an error deriving the public key. Try re-registering a new biometric authentication."
    ),
    GOOGLE_ONETAP_MISSING_ID_TOKEN(
        stringValue = "google_onetap_missing_id_token",
        message = "The Google OneTap authorization credential was missing an id_token."
    ),
    GOOGLE_ONETAP_MISSING_MEMBER(
        stringValue = " google_onetap_missing_member",
        message = "Missing a nonce or onetap client. Make sure you are calling StytchClient.oauth.googleOneTap.start() before StytchClient.oauth.googleOneTap.authenticate()", // ktlint-disable maximum-line-length
    ),
    OAUTH_INVALID_START_URL(
        stringValue = "oauth_generic_invalid_start_url",
        message = "The start url was invalid or improperly formatted."
    ),
    OAUTH_MISSING_PKCE(
        stringValue = "oauth_missing_pkce",
        message = "Unable to retrieve the PKCE challenge. This flow must be completed on the same device on which it was started", // ktlint-disable maximum-line-length
    ),
    DEEPLINK_MISSING_TOKEN(
        stringValue = "deeplink_missing_token",
        message = "Magic link missing token"
    ),
    DEEPLINK_UNKNOWN_TOKEN(
        stringValue = "deeplink_unknown_token",
        message = "Unknown magic link type"
    )
}

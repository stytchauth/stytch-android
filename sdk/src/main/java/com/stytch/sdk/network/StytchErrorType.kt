package com.stytch.sdk.network

public enum class StytchErrorType(public val stringValue: String) {
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
}
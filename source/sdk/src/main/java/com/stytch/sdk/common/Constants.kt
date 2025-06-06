package com.stytch.sdk.common

internal const val TEST_BASE_DOMAIN: String = "test.stytch.com"
internal const val LIVE_BASE_DOMAIN: String = "api.stytch.com"
internal const val DEFAULT_DFPPA_DOMAIN: String = "telemetry.stytch.com"
internal const val SDK_URL_PATH: String = "/sdk/v1/"
internal const val API_URL_PATH: String = "/v1/"

// Auth headers
internal const val AUTH_HEADER_SDK_NAME = "stytch-android"

// Query parameters
internal const val QUERY_TOKEN_TYPE = "stytch_token_type"
internal const val QUERY_TOKEN = "token"
internal const val QUERY_REDIRECT_TYPE = "stytch_redirect_type"

internal const val CODE_CHALLENGE_BYTE_COUNT = 32

/**
 * The default time that a created session will be valid. This is used if you do not provide a session duration
 * to an authentication call, and defaults to the shortest valid session duration supported by Stytch (5 minutes)
 */
public const val DEFAULT_SESSION_TIME_MINUTES: Int = 5

/**
 * The default time that an OTP code will be valid. This is used if you do not provide an expiration duration
 * to an OTP loginOrCreate or send call
 */
public const val DEFAULT_OTP_EXPIRATION_TIME_MINUTES: Int = 10

internal const val PREFERENCES_NAME_SESSION_JWT = "session_jwt"
internal const val PREFERENCES_NAME_SESSION_TOKEN = "session_token"
internal const val PREFERENCES_NAME_IST = "intermediate_session_token"
internal const val PREFERENCES_NAME_IST_EXPIRATION = "intermediate_session_token_expiration"
internal const val PREFERENCES_NAME_LAST_VALIDATED_AT = "stytch_last_validated_at"
internal const val PREFERENCES_NAME_SESSION_DATA = "stytch_session_data"
internal const val PREFERENCES_NAME_USER_DATA = "stytch_user_data"
internal const val PREFERENCES_NAME_MEMBER_SESSION_DATA = "stytch_member_session_data"
internal const val PREFERENCES_NAME_MEMBER_DATA = "stytch_member_data"
internal const val PREFERENCES_NAME_ORGANIZATION_DATA = "stytch_organization_data"
internal const val IST_EXPIRATION_TIME = 10 * 60 * 1000L // 10 minutes
internal const val PREFERENCES_NAME_LAST_AUTH_METHOD_USED = "stytch_last_auth_method_used"

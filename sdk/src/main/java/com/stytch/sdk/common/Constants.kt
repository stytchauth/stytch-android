package com.stytch.sdk.common

public object Constants {
    internal const val TEST_SDK_URL: String = "https://test.stytch.com/sdk/v1/"
    internal const val LIVE_SDK_URL: String = "https://api.stytch.com/sdk/v1/"
    internal const val TEST_API_URL: String = "https://test.stytch.com/v1/"
    internal const val LIVE_API_URL: String = "https://api.stytch.com/v1/"

    // Auth headers
    internal const val AUTH_HEADER_SDK_NAME = "stytch-android"

    // Query parameters
    internal const val QUERY_TOKEN_TYPE = "stytch_token_type"
    internal const val QUERY_TOKEN = "token"

    internal const val CODE_CHALLENGE_BYTE_COUNT = 32

    public const val DEFAULT_SESSION_TIME_MINUTES: UInt = 5u
    public const val DEFAULT_OTP_EXPIRATION_TIME_MINUTES: UInt = 10u
    internal const val PREFERENCES_NAME_SESSION_JWT = "session_jwt"
    internal const val PREFERENCES_NAME_SESSION_TOKEN = "session_token"
    internal const val PREFERENCES_NAME_IST = "intermediate_session_token"
    internal const val PREFERENCES_NAME_IST_EXPIRATION = "intermediate_session_token_expiration"
    internal const val IST_EXPIRATION_TIME = 10 * 60 * 1000L // 10 minutes
}

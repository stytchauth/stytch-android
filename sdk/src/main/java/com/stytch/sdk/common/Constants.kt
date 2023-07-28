package com.stytch.sdk.common

public object Constants {
    internal const val WEB_URL: String = "https://web.stytch.com/sdk/v1/"
    internal const val TEST_API_URL: String = "https://test.stytch.com/v1/"
    internal const val LIVE_API_URL: String = "https://api.stytch.com/v1/"

    // Auth headers
    internal const val AUTH_HEADER_SDK_NAME = "stytch-android"

    // Query parameters
    internal const val QUERY_TOKEN_TYPE = "stytch_token_type"
    internal const val QUERY_TOKEN = "token"

    internal const val CODE_CHALLENGE_BYTE_COUNT = 32

    public const val DEFAULT_SESSION_TIME_MINUTES: UInt = 30u
    public const val DEFAULT_OTP_EXPIRATION_TIME_MINUTES: UInt = 10u
    internal const val PREFERENCES_NAME_SESSION_JWT = "session_jwt"
    internal const val PREFERENCES_NAME_SESSION_TOKEN = "session_token"
}

package com.stytch.sdk.common

internal object Constants {
    internal const val WEB_URL: String = "https://web.stytch.com/sdk/v1/"
    internal const val TEST_API_URL: String = "https://test.stytch.com/v1/"
    internal const val LIVE_API_URL: String = "https://api.stytch.com/v1/"

    // Auth headers
    internal const val AUTH_HEADER_SDK_NAME = "stytch-kotlin"

    // Query parameters
    internal const val QUERY_TOKEN_TYPE = "stytch_token_type"
    internal const val QUERY_TOKEN = "token"

    internal const val CODE_CHALLENGE_BYTE_COUNT = 32

    internal const val DEFAULT_SESSION_TIME_MINUTES = 30u
    internal const val DEFAULT_OTP_EXPIRATION_TIME_MINUTES = 10u
}

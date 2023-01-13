package com.stytch.sdk

internal object Constants {
    const val WEB_URL: String = "https://web.stytch.com/sdk/v1/"
    const val TEST_API_URL: String = "https://test.stytch.com/v1/"
    const val LIVE_API_URL: String = "https://api.stytch.com/v1/"

    //  Auth headers
    const val AUTH_HEADER_SDK_NAME = "stytch-kotlin"

//  Query parameters
    const val QUERY_TOKEN_TYPE = "stytch_token_type"
    const val QUERY_TOKEN = "token"

    const val CODE_CHALLENGE_BYTE_COUNT = 32

    const val DEFAULT_SESSION_TIME_MINUTES = 30u
    const val DEFAULT_OTP_EXPIRATION_TIME_MINUTES = 10u
}

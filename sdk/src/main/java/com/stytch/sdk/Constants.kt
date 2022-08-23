package com.stytch.sdk

internal object Constants {
//  Auth headers
    const val AUTH_HEADER_SDK_NAME = "stytch-kotlin"
    const val AUTH_HEADER_SDK_VERSION = "0.1.0"

//  Query parameters
    const val QUERY_TOKEN_TYPE = "stytch_token_type"
    const val QUERY_TOKEN = "token"

    const val CODE_CHALLENGE_BYTE_COUNT = 32

    const val DEFAULT_SESSION_TIME_MINUTES = 30u
}
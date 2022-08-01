package com.stytch.sdk

internal object Constants {
//  Auth headers
    const val AUTH_HEADER_SDK_NAME = "stytch-kotlin"
    const val AUTH_EHADER_SDK_VERSION = "0.4.2"

//  Query parameters
    const val QUERY_PUBLIC_TOKEN = "public_token"
    const val QUERY_TOKEN_TYPE = "stytch_token_type"
    const val QUERY_TOKEN = "token"

//  Token types
    const val TOKEN_TYPE_MAGIC_LINKS = "magic_links"
    const val TOKEN_TYPE_OAUTH = "oauth"
    const val TOKEN_TYPE_PASSWORD_RESET = "password_reset"
}

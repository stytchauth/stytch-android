package com.stytch.sdk.common

/**
 * A data class representing a concrete token type and corresponding token parsed from a deeplink
 */
public data class DeeplinkTokenPair(
    val tokenType: TokenType,
    val token: String?,
)

package com.stytch.sdk

import java.util.Locale

internal enum class TokenType {
    MAGIC_LINKS,
    OAUTH,
    PASSWORD_RESET,
    UNKNOWN;

    companion object {
        fun fromString(typeString: String?): TokenType {
            return try {
                valueOf(typeString?.uppercase(Locale.ENGLISH)!!)
            } catch (_: Exception) {
                UNKNOWN
            }
        }
    }
}

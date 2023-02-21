package com.stytch.sdk.common

import java.util.Locale

public enum class TokenType {
    MAGIC_LINKS,
    MULTI_TENANT_MAGIC_LINKS,
    OAUTH,
    PASSWORD_RESET,
    UNKNOWN;

    internal companion object {
        fun fromString(typeString: String?): TokenType {
            return try {
                valueOf(typeString?.uppercase(Locale.ENGLISH)!!)
            } catch (_: Exception) {
                UNKNOWN
            }
        }
    }
}

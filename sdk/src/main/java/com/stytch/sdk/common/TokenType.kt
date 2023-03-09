package com.stytch.sdk.common

import java.util.Locale

/**
 * An enum representing the supported token types that we can extract from a deeplink
 */
public enum class TokenType {
    /**
     * A Consumer Email Magic Link deeplink
     */
    MAGIC_LINKS,

    /**
     * A B2B Email Magic Link deeplink
     */
    MULTI_TENANT_MAGIC_LINKS,

    /**
     * A Third Party OAuth deeplink
     */
    OAUTH,

    /**
     * A Password Reset deeplink
     */
    PASSWORD_RESET,

    /**
     * An unknown deeplink type. It's possible a non-Stytch deeplink was supplied to the Stytch client's handle() method
     */
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

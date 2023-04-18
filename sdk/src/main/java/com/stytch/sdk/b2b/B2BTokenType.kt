package com.stytch.sdk.b2b

import com.stytch.sdk.common.TokenType
import java.util.Locale

/**
 * An enum representing the supported (B2B) token types that we can extract from a deeplink
 */
public enum class B2BTokenType : TokenType {
    /**
     * A B2B Email Magic Link deeplink
     */
    MULTI_TENANT_MAGIC_LINKS,

    /**
     * A B2B Discovery Magic Link deeplink
     */
    DISCOVERY,

    /**
     * An unknown deeplink type. It's possible a non-Stytch deeplink was supplied to the Stytch client's handle() method
     */
    UNKNOWN;

    internal companion object {
        fun fromString(typeString: String?): B2BTokenType {
            return try {
                valueOf(typeString?.uppercase(Locale.ENGLISH)!!)
            } catch (_: Exception) {
                UNKNOWN
            }
        }
    }
}

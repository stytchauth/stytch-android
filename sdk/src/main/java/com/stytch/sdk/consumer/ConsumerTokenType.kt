package com.stytch.sdk.consumer

import com.stytch.sdk.common.TokenType
import java.util.Locale

/**
 * An enum representing the supported (consumer) token types that we can extract from a deeplink
 */
public enum class ConsumerTokenType : TokenType {
    /**
     * A Consumer Email Magic Link deeplink
     */
    MAGIC_LINKS,

    /**
     * A Third Party OAuth deeplink
     */
    OAUTH,

    /**
     * A Password Reset deeplink
     */
    RESET_PASSWORD,

    /**
     * An unknown deeplink type. It's possible a non-Stytch deeplink was supplied to the Stytch client's handle() method
     */
    UNKNOWN;

    internal companion object {
        fun fromString(typeString: String?): ConsumerTokenType {
            return try {
                valueOf(typeString?.uppercase(Locale.ENGLISH)!!)
            } catch (_: Exception) {
                UNKNOWN
            }
        }
    }
}

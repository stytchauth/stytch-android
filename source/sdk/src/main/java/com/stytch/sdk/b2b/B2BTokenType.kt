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
     * A B2B Password reset deeplink
     */
    MULTI_TENANT_PASSWORDS,

    /**
     * A B2B Discovery deeplink
     */
    DISCOVERY,

    /**
     * A SSO (SAML, OIDC) deeplink
     */
    SSO,

    /**
     * An OAuth deeplink
     */
    OAUTH,

    /**
     * An OAuth Discovery deeplink
     */
    DISCOVERY_OAUTH,

    /**
     * An unknown deeplink type. It's possible a non-Stytch deeplink was supplied to the Stytch client's handle() method
     */
    UNKNOWN,

    ;

    internal companion object {
        fun fromString(typeString: String?): B2BTokenType =
            try {
                valueOf(typeString?.uppercase(Locale.ENGLISH)!!)
            } catch (_: Exception) {
                UNKNOWN
            }
    }
}

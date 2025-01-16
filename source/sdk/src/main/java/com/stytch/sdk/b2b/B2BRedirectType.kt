package com.stytch.sdk.b2b

import java.util.Locale

public enum class B2BRedirectType {
    RESET_PASSWORD,
    UNKNOWN,
    ;

    internal companion object {
        fun fromString(typeString: String?): B2BRedirectType =
            try {
                B2BRedirectType.valueOf(typeString?.uppercase(Locale.ENGLISH)!!)
            } catch (_: Exception) {
                UNKNOWN
            }
    }
}

package com.stytch.sdk

internal enum class TokenType(val raw: String?) {
    MAGIC_LINKS("magic_links"),
    OAUTH("oauth"),
    PASSWORD_RESET("password_reset"),
    UNKNOWN(null);

    companion object{
        fun fromString(typeString: String?): TokenType{
            values().forEach { tokenType ->
                if(tokenType.raw.equals(typeString, true))
                    return tokenType
            }
            return UNKNOWN
        }
    }
}
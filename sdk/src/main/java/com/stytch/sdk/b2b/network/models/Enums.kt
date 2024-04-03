package com.stytch.sdk.b2b.network.models

import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.utils.IEnumValue

@JsonClass(generateAdapter = false)
public enum class SsoJitProvisioning {
    ALL_ALLOWED,
    RESTRICTED,
    NOT_ALLOWED,
}

@JsonClass(generateAdapter = false)
public enum class EmailJitProvisioning {
    RESTRICTED,
    NOT_ALLOWED,
}

@JsonClass(generateAdapter = false)
public enum class EmailInvites {
    ALL_ALLOWED,
    RESTRICTED,
    NOT_ALLOWED,
}

@JsonClass(generateAdapter = false)
public enum class AuthMethods {
    ALL_ALLOWED,
    RESTRICTED,
}

@JsonClass(generateAdapter = false)
public enum class AllowedAuthMethods(override val jsonName: String) : IEnumValue {
    SSO("sso"),
    MAGIC_LINK("magic_link"),
    PASSWORD("password"),
}

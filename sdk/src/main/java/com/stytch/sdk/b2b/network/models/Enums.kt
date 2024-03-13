package com.stytch.sdk.b2b.network.models

import com.stytch.sdk.common.utils.IEnumValue

public enum class SsoJitProvisioning {
    ALL_ALLOWED,
    RESTRICTED,
    NOT_ALLOWED,
}

public enum class EmailJitProvisioning {
    RESTRICTED,
    NOT_ALLOWED,
}

public enum class EmailInvites {
    ALL_ALLOWED,
    RESTRICTED,
    NOT_ALLOWED,
}

public enum class AuthMethods {
    ALL_ALLOWED,
    RESTRICTED,
}

public enum class AllowedAuthMethods(override val jsonName: String) : IEnumValue {
    SSO("sso"),
    MAGIC_LINK("magic_link"),
    PASSWORD("password"),
}

public enum class MfaMethod(override val jsonName: String) : IEnumValue {
    SMS("sms_otp"),
    TOTP("totp")
}
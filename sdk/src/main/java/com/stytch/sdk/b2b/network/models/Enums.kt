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

public enum class MfaMethods {
    ALL_ALLOWED,
    RESTRICTED,
}

public enum class MfaPolicy {
    REQUIRED_FOR_ALL,
    OPTIONAL,
}

public enum class AllowedAuthMethods(override val jsonName: String) : IEnumValue {
    SSO("sso"),
    MAGIC_LINK("magic_link"),
    PASSWORD("password"),
    GOOGLE_OAUTH("google_oauth"),
    MICROSOFT_OAUTH("microsoft_oauth"),
}

public enum class MfaMethod(override val jsonName: String) : IEnumValue {
    SMS("sms_otp"),
    TOTP("totp"),
}

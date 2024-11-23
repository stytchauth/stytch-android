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
public enum class OAuthTenantJitProvisioning {
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
public enum class MfaMethods {
    ALL_ALLOWED,
    RESTRICTED,
}

@JsonClass(generateAdapter = false)
public enum class MfaPolicy {
    REQUIRED_FOR_ALL,
    OPTIONAL,
}

@JsonClass(generateAdapter = false)
public enum class AllowedAuthMethods(
    override val jsonName: String,
) : IEnumValue {
    SSO("sso"),
    MAGIC_LINK("magic_link"),
    PASSWORD("password"),
    GOOGLE_OAUTH("google_oauth"),
    MICROSOFT_OAUTH("microsoft_oauth"),
    HUBSPOT_OAUTH("hubspot_oauth"),
    GITHUB_OAUTH("github_oauth"),
    SLACK_OAUTH("slack_oauth"),
}

@JsonClass(generateAdapter = false)
public enum class MfaMethod(
    override val jsonName: String,
) : IEnumValue {
    SMS("sms_otp"),
    TOTP("totp"),
}

@JsonClass(generateAdapter = false)
public enum class SearchOperator {
    AND,
    OR,
}

@JsonClass(generateAdapter = false)
public enum class SetMFAEnrollment(
    override val jsonName: String,
) : IEnumValue {
    ENROLL("enroll"),
    UNENROLL("unenroll"),
}

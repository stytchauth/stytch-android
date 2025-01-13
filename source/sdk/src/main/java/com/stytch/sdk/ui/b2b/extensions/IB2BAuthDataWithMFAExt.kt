package com.stytch.sdk.ui.b2b.extensions

import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.IB2BAuthDataWithMFA
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.b2b.network.models.MfaMethods

// These are convenience functions that could probably moved to the SDK itself

internal fun IB2BAuthDataWithMFA.getAuthMethodsForMember(): List<AllowedAuthMethods> {
    // If member has no password, don't present password auth as an option
    val memberHasPassword = member.memberPasswordId != null
    val allowedAuthMethods = primaryRequired?.allowedAuthMethods ?: emptyList()
    return if (memberHasPassword) {
        allowedAuthMethods
    } else {
        allowedAuthMethods.filterNot { it == AllowedAuthMethods.PASSWORD }
    }
}

internal fun IB2BAuthDataWithMFA.getEnrolledMfaMethods(): List<MfaMethod> {
    val enrolledMfaMethods = mutableListOf<MfaMethod>()
    if (!member.totpRegistrationId.isNullOrEmpty()) {
        enrolledMfaMethods.add(MfaMethod.TOTP)
    }
    if (member.mfaPhoneNumberVerified) {
        enrolledMfaMethods.add(MfaMethod.SMS)
    }
    return enrolledMfaMethods.toList()
}

internal fun IB2BAuthDataWithMFA.getOrganizationMfaOptionsSupported(): List<MfaMethod> {
    val defaultNoMfaOptionsSupported = emptyList<MfaMethod>()
    return if (organization.mfaMethods == MfaMethods.RESTRICTED && organization.allowedMfaMethods != null) {
        organization.allowedMfaMethods?.toList() ?: defaultNoMfaOptionsSupported
    } else {
        defaultNoMfaOptionsSupported
    }
}

internal fun List<MfaMethod>.supportsMethod(method: MfaMethod): Boolean = isEmpty() || contains(method)

internal fun IB2BAuthDataWithMFA.getDefaultMFAMethod(): MfaMethod? {
    val defaultMethod = member.defaultMfaMethod
    return if (defaultMethod != null &&
        getOrganizationMfaOptionsSupported().supportsMethod(defaultMethod) &&
        getEnrolledMfaMethods().contains(defaultMethod)
    ) {
        defaultMethod
    } else {
        null
    }
}

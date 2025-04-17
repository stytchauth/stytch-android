package com.stytch.sdk.b2b.member

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated

/**
 * A [MemberAuthenticationFactor] represents a primary authentication factor associated with a Stytch Member
 * @param id The string representing the unique ID of this authentication factor
 */

@JacocoExcludeGenerated
public sealed class MemberAuthenticationFactor(
    public open val id: String? = null,
) {
    /**
     * Represents an MFA Phone Number associated with a Stytch Member
     */

    @JacocoExcludeGenerated public data object MfaPhoneNumber : MemberAuthenticationFactor()

    /**
     * Represents an MFA TOTP associated with a Stytch Member
     */

    @JacocoExcludeGenerated public data object MfaTOTP : MemberAuthenticationFactor()

    /**
     * Represents a Password associated with a Stytch Member
     */
    @JacocoExcludeGenerated
    public data class Password(
        override val id: String,
    ) : MemberAuthenticationFactor(id)
}

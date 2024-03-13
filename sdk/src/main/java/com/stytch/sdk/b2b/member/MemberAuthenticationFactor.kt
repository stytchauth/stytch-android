package com.stytch.sdk.b2b.member

/**
 * A [MemberAuthenticationFactor] represents a primary authentication factor associated with a Stytch Member
 * @param id The string representing the unique ID of this authentication factor
 */
public sealed class MemberAuthenticationFactor(public open val id: String? = null) {
    /**
     * Represents an MFA Phone Number associated with a Stytch Member
     */
    public object MfaPhoneNumber : MemberAuthenticationFactor()

    /**
     * Represents an MFA TOTP associated with a Stytch Member
     */
    public object MfaTOTP : MemberAuthenticationFactor()

    /**
     * Represents a phone number associated with a Stytch User
     */
    public data class Password(override val id: String) : MemberAuthenticationFactor(id)
}

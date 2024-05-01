package com.stytch.sdk.consumer.userManagement

/**
 * A [UserAuthenticationFactor] represents a primary authentication factor associated with a Stytch User
 * @param id The string representing the unique ID of this authentication factor
 */
public sealed class UserAuthenticationFactor(public open val id: String) {
    /**
     * Represents an email address associated with a Stytch User
     */
    public data class Email(override val id: String) : UserAuthenticationFactor(id)

    /**
     * Represents a phone number associated with a Stytch User
     */
    public data class PhoneNumber(override val id: String) : UserAuthenticationFactor(id)

    /**
     * Represents a biometric registration associated with a Stytch User
     */
    public data class BiometricRegistration(override val id: String) : UserAuthenticationFactor(id)

    /**
     * Represents a Web3 login associated with a Stytch User
     */
    public data class CryptoWallet(override val id: String) : UserAuthenticationFactor(id)

    /**
     * Represents a WebAuthn registration associated with a Stytch User
     */
    public data class WebAuthn(override val id: String) : UserAuthenticationFactor(id)

    /**
     * Represents a TOTP registration associated with a Stytch User
     */
    public data class TOTP(override val id: String) : UserAuthenticationFactor(id)

    /**
     * Represents an OAuth registration associated with a Stytch User
     */
    public data class OAuth(override val id: String) : UserAuthenticationFactor(id)
}

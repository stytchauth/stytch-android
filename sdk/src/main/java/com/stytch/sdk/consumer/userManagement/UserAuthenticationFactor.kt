package com.stytch.sdk.consumer.userManagement

public sealed class UserAuthenticationFactor(public open val id: String) {
    public data class Email(override val id: String) : UserAuthenticationFactor(id)
    public data class PhoneNumber(override val id: String) : UserAuthenticationFactor(id)
    public data class BiometricRegistration(override val id: String) : UserAuthenticationFactor(id)
    public data class CryptoWallet(override val id: String) : UserAuthenticationFactor(id)
    public data class WebAuthn(override val id: String) : UserAuthenticationFactor(id)
}

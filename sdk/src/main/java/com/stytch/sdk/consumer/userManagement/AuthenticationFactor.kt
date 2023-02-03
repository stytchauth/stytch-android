package com.stytch.sdk.consumer.userManagement

public sealed class AuthenticationFactor(public open val id: String) {
    public data class Email(override val id: String) : AuthenticationFactor(id)
    public data class PhoneNumber(override val id: String) : AuthenticationFactor(id)
    public data class BiometricRegistration(override val id: String) : AuthenticationFactor(id)
    public data class CryptoWallet(override val id: String) : AuthenticationFactor(id)
    public data class WebAuthn(override val id: String) : AuthenticationFactor(id)
}

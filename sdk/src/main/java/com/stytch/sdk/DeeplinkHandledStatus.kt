package com.stytch.sdk

public sealed interface DeeplinkHandledStatus {
    public data class Handled(val response: AuthResponse) : DeeplinkHandledStatus
    public data class NotHandled(val reason: String) : DeeplinkHandledStatus
    public data class ManualHandlingRequired(val type: TokenType, val token: String) : DeeplinkHandledStatus
}

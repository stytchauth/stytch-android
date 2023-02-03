package com.stytch.sdk.common

public sealed interface DeeplinkHandledStatus {
    public data class Handled(val response: StytchResult<Any>) : DeeplinkHandledStatus
    public data class NotHandled(val reason: String) : DeeplinkHandledStatus
    public data class ManualHandlingRequired(val type: TokenType, val token: String) : DeeplinkHandledStatus
}

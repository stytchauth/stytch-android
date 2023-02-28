package com.stytch.sdk.common

import com.stytch.sdk.common.network.models.CommonAuthenticationData

public sealed interface DeeplinkHandledStatus {
    public data class Handled(val response: StytchResult<CommonAuthenticationData>) : DeeplinkHandledStatus
    public data class NotHandled(val reason: String) : DeeplinkHandledStatus
    public data class ManualHandlingRequired(val type: TokenType, val token: String) : DeeplinkHandledStatus
}

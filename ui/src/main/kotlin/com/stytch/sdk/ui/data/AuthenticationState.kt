package com.stytch.sdk.ui.data

import com.stytch.sdk.common.StytchResult

internal sealed class AuthenticationState {
    object Idle : AuthenticationState()

    data class Result(val response: StytchResult<*>) : AuthenticationState()
}

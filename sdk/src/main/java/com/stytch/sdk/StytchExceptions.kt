package com.stytch.sdk

internal sealed class StytchExceptions: Exception() {
    internal object NoCodeChallengeFound: StytchExceptions()
}
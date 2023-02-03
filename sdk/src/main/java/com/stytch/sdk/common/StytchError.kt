package com.stytch.sdk.common

internal fun stytchError(message: String): Nothing {
    error("Stytch error: $message")
}

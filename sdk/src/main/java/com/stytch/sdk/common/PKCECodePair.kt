package com.stytch.sdk.common

public data class PKCECodePair(
    val codeChallenge: String,
    val codeVerifier: String,
    val method: String = "S256",
)

package com.stytch.sdk.common

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated

/**
 * A data class representing the most recent PKCE code pair generated on this device. You may find this useful if you
 * use a hybrid (frontend and backend) authentication flow, where you need to complete a PKCE flow on the backend
 * @property codeChallenge the challenge that was generated
 * @property codeVerifier the verifier of the challenge
 * @property method a string identifying the encryption method used. This will always be "S256"
 */
@JacocoExcludeGenerated
public data class PKCECodePair(
    val codeChallenge: String,
    val codeVerifier: String,
    val method: String = "S256",
)

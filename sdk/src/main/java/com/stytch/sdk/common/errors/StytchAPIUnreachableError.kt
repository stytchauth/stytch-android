package com.stytch.sdk.common.errors

/**
 * An error class that occurs when Stytch SDK cannot reach the API
 */
public class StytchAPIUnreachableError(description: String, url: String? = null) : StytchError(
    name = "StytchAPIUnreachableError",
    description = description,
    url = url
)

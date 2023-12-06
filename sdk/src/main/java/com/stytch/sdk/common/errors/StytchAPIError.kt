package com.stytch.sdk.common.errors

/**
 * An error class representing non-schema error that occurs in Stytch API
 */
public class StytchAPIError(description: String, url: String? = null) : StytchError(
    name = "StytchAPIError",
    description = description,
    url = url
)

package com.stytch.sdk.common.errors

/**
 * An error that occurs when an SDK method is called with invalid input
 */
public class StytchSDKUsageError(description: String, url: String? = null) : StytchError(
    name = "StytchSDKUsageError",
    description = description,
    url = url
)

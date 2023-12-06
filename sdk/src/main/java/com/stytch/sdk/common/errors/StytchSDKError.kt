package com.stytch.sdk.common.errors

/**
 * A base class representing SDK specific errors or exceptions that may occur. This class should not be used directly,
 * rather we should be creating implementations for each of the known/expected errors we return.
 */
public sealed class StytchSDKError(
    name: String,
    description: String,
    url: String? = null,
    exception: Throwable? = null,
) : StytchError(
    name = name,
    description = description,
    url = url,
    exception = exception,
)

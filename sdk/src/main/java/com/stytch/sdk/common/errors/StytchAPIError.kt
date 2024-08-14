package com.stytch.sdk.common.errors

/**
 * An error class representing a non-schema error that occurs in the Stytch API
 * @property requestId the request id of the request that triggered this error
 * @property errorType a Stytch-specific string representing the type of error that occurred
 * @property message a string providing more information about what went wrong. This may or may not be user-friendly
 * @property url a url linking to the Stytch documentation that describes this error
 */
public data class StytchAPIError(
    public val requestId: String? = null,
    public val errorType: String,
    public override val message: String,
    public val url: String? = null,
) : StytchError(message = message)

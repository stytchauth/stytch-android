package com.stytch.sdk.common.errors

/**
 * An error class representing a schema error that occurs in Stytch API
 */
public data class StytchAPISchemaError(
    public override val description: String,
    public override val url: String? = null,
) : StytchError(
    name = "StytchAPISchemaError",
    description = description,
    url = url
)

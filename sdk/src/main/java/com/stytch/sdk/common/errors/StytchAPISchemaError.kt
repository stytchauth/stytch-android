package com.stytch.sdk.common.errors

/**
 * An error class representing a schema error that occurs in Stytch API
 */
public class StytchAPISchemaError(description: String, url: String? = null) : StytchError(
    name = "StytchAPISchemaError",
    description = description,
    url = url
)

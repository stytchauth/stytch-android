package com.stytch.sdk.common.errors

/**
 * An error class representing a schema error that occurs in Stytch API
 */
public data class StytchAPISchemaError(
    public override val message: String,
) : StytchError(message = message)

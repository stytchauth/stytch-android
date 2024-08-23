package com.stytch.sdk.common.errors

/**
 * An error class representing a schema error that occurs in the Stytch API
 * @property message a string explaining what was wrong with the schema that was sent
 */
public data class StytchAPISchemaError(
    public override val message: String,
) : StytchError(message = message)

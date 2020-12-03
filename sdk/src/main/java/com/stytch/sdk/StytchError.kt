package com.stytch.sdk

public enum class StytchError(val messageId: Int) {
    InvalidEmail(R.string.stytch_error_invalid_input),
    Connection(R.string.stytch_error_no_internet),
    Unknown(R.string.stytch_error_unknown),
    InvalidMagicToken(R.string.stytch_error_invalid_magic_token),
    InvalidConfiguration(R.string.stytch_error_bad_token)
}
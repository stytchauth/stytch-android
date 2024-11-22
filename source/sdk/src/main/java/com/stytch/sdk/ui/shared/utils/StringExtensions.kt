package com.stytch.sdk.ui.shared.utils

import java.util.regex.Pattern

private val EMAIL_ADDRESS_PATTERN =
    Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+",
    )

internal fun String.isValidEmailAddress(): Boolean = EMAIL_ADDRESS_PATTERN.matcher(this).matches()

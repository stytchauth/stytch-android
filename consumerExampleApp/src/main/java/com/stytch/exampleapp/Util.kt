package com.stytch.exampleapp

import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import java.util.regex.Pattern

private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
)

private val PHONE_NUMBER_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}\$")

fun isValidEmail(str: String): Boolean {
    return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
}

fun isPhoneNumberValid(str: String): Boolean {
    return PHONE_NUMBER_PATTERN.matcher(str).matches()
}

fun <T : Any> StytchResult<T>.toFriendlyDisplay() = when (this) {
    is StytchResult.Success<*> -> this.toString()
    is StytchResult.Error -> {
        var message = "Name: ${exception.name}\nDescription: ${exception.description}"
        if (exception is StytchAPIError) {
            message += "\nURL: ${(exception as StytchAPIError).url}"
        }
        message
    }
}

package com.stytch.exampleapp

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

private val PHONE_NUMBER_PATTERN = Pattern.compile("^\\+[0-9]{1,3}\\.[0-9]{4,14}(?:x.+)?$")


fun isValidEmail(str: String): Boolean {
    return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
}

fun isPhoneNumberValid(str: String): Boolean {
    return PHONE_NUMBER_PATTERN.matcher(str).matches()
}

package com.stytch.sdk.helpers

internal object Constants {
    val HOST = "stytch.com"
    val NOT_INITIALIZED_WARNING = "Stytch is not configured. call Stytch.instance.configure(...) first"
    val LOGIN_PATH = "login_magic_link"
    val INVITE_PATH = "invite_magic_link"
    val SIGN_UP_PATH = "signup_magic_link"

    val LOGIN_EXPIDARTION = 60L
    val INVITE_EXPIRATION = 7*24*60L
    val SIGNUP_EXPIRATION = 7*24*60L

}
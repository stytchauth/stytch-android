package com.stytch.sdk

import android.util.Log

internal object Constants {
    const val HOST = "stytch.com"
    const val NOT_INITIALIZED_WARNING = "Stytch is not configured. call Stytch.instance.configure(...) first"
    const val LOGIN_PATH = "login_magic_link"
    const val INVITE_PATH = "invite_magic_link"
    const val SIGN_UP_PATH = "signup_magic_link"

    const val LOGIN_EXPIRATION = 60L
    const val INVITE_EXPIRATION = 7 * 24 * 60L
    const val SIGNUP_EXPIRATION = 7 * 24 * 60L
}

public class LoggerLocal {

    public companion object {
        public fun d(tag: String, msg: String, throwable: Throwable? = null) {
            if (BuildConfig.DEBUG) {
                Log.d(tag, msg, throwable)
            }
        }

        public fun e(tag: String, msg: String, throwable: Throwable? = null) {
            if (BuildConfig.DEBUG) {
                Log.e(tag, msg, throwable)
            }
        }

        public fun w(tag: String, msg: String, throwable: Throwable? = null) {
            if (BuildConfig.DEBUG) {
                Log.w(tag, msg, throwable)
            }
        }
    }
}

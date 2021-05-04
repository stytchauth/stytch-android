package com.stytch.sdk.helpers

import android.util.Log
import com.stytch.sdk.BuildConfig

class LoggerLocal {

    companion object {
        fun d(tag: String, msg: String, throwable: Throwable? = null) {
            if (BuildConfig.DEBUG) {
                Log.d(tag, msg, throwable)
            }
        }

        fun e(tag: String, msg: String, throwable: Throwable? = null) {
            if (BuildConfig.DEBUG) {
                Log.e(tag, msg, throwable)
            }
        }

        fun w(tag: String, msg: String, throwable: Throwable? = null) {
            if (BuildConfig.DEBUG) {
                Log.w(tag, msg, throwable)
            }
        }
    }
}
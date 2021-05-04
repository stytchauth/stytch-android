package com.stytch.sdk.helpers

import android.util.Log
import com.stytch.sdk.BuildConfig

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

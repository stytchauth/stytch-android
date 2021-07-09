package com.stytch.testapp

import android.content.Context
import android.widget.Toast
import timber.log.Timber

fun Context.showToast(message: String) {
    Timber.tag("StytchAndroidTestApp").i("Showing toast: '$message'")
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

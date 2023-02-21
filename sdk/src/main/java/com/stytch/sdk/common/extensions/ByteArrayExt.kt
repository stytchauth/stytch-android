package com.stytch.sdk.common.extensions

import android.util.Base64

internal fun ByteArray.toBase64EncodedString(): String = Base64.encodeToString(this, Base64.NO_WRAP)

internal fun ByteArray.toHexString(): String = joinToString(separator = "") { byte -> "%02x".format(byte) }

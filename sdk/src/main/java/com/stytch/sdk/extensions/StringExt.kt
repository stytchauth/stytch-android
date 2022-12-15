package com.stytch.sdk.extensions

import android.util.Base64

private const val HEX_RADIX = 16

internal fun String.toBase64DecodedByteArray(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

internal fun String.hexStringToByteArray(): ByteArray =
    chunked(2).map { it.toInt(HEX_RADIX).toByte() }.toByteArray()

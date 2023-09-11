package com.stytch.sdk.common.extensions

import okhttp3.Response

private const val HTTP_UNAUTHORIZED = 403

internal fun Response.requiresCaptcha(): Boolean {
    return code == HTTP_UNAUTHORIZED
}

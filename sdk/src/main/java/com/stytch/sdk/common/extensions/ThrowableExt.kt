package com.stytch.sdk.common.extensions

import com.stytch.sdk.common.StytchLog
import com.stytch.sdk.common.errors.StytchAPIUnreachableError
import com.stytch.sdk.common.errors.StytchError
import retrofit2.HttpException

internal fun Throwable.toStytchError(): StytchError =
    when (this) {
        is StytchError -> this
        is HttpException -> toStytchError()
        else -> {
            printStackTrace()
            StytchLog.w("Network Error")
            StytchAPIUnreachableError(
                message = message ?: "Invalid or no response from server",
                exception = this,
            )
        }
    }

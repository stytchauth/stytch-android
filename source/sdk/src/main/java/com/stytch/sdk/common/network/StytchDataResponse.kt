package com.stytch.sdk.common.network

import androidx.annotation.Keep

@Keep
internal open class StytchDataResponse<T>(internal val data: T)

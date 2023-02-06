package com.stytch.sdk.b2b.network

import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.StytchDataResponse

internal object B2BResponses {
    @JsonClass(generateAdapter = true)
    class AuthenticateResponse(data: B2BAuthData) : StytchDataResponse<B2BAuthData>(data)
}

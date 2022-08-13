package com.stytch.sdk.network

import com.squareup.moshi.JsonClass
import com.stytch.sdk.network.responseData.BasicData

internal object StytchResponses {

    @JsonClass(generateAdapter = true)
    internal class LoginOrCreateUserByEmailResponse(data: BasicData): StytchDataResponse<BasicData>(data)

    @JsonClass(generateAdapter = true)
    internal class BasicResponse(data: BasicData): StytchDataResponse<BasicData>(data)

    internal open class StytchDataResponse<T>(
        val data: T
    )




}
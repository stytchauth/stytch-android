package com.stytch.sdk.network

import com.squareup.moshi.JsonClass
import com.stytch.sdk.network.responseData.BasicData

internal object StytchResponses {

    object MagicLinks{
        object Email{
            @JsonClass(generateAdapter = true)
            class LoginOrCreateUserByEmailResponse(data: BasicData): StytchDataResponse<BasicData>(data)
        }
    }

    @JsonClass(generateAdapter = true)
    class BasicResponse(data: BasicData): StytchDataResponse<BasicData>(data)

    open class StytchDataResponse<T>(
        val data: T
    )
}
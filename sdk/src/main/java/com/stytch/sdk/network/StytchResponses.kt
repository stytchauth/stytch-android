package com.stytch.sdk.network

import com.squareup.moshi.JsonClass

public object StytchResponses {

    @JsonClass(generateAdapter = true)
    public class LoginOrCreateUserByEmailResponse(data: BasicData): StytchDataResponse<BasicData>(data)

    @JsonClass(generateAdapter = true)
    public class BasicResponse(data: BasicData): StytchDataResponse<BasicData>(data)

    public open class StytchDataResponse<T>(
        public val data: T
    )

    @JsonClass(generateAdapter = true)
    public data class BasicData(
        val request_id: String
    )

    @JsonClass(generateAdapter = true)
    public data class StytchErrorResponse(
        val status_code: Int,
        val request_id: String,
        val error_type: String,
        val error_message: String?,
        val error_url: String,
    )


}
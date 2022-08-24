package com.stytch.sdk.network

import com.squareup.moshi.JsonClass
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sdk.network.responseData.BasicData

internal object StytchResponses {

    object MagicLinks {
        object Email {
            @JsonClass(generateAdapter = true)
            class LoginOrCreateUserResponse(data: BasicData) : StytchDataResponse<BasicData>(data)
        }
    }

    object Sessions {
        @JsonClass(generateAdapter = true)
        class RevokeResponse(data: BasicData) : StytchDataResponse<BasicData>(data)
    }

    object Passwords {
        @JsonClass(generateAdapter = true)
        class CreateResponse(
            val request_id: String,
            val status_code: Int,
            val email_id: Int,
            val user_id: Int,
        )

        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            val request_id: String,
            val status_code: Int,
        )

        @JsonClass(generateAdapter = true)
        class ResetByEmailStartResponse(
            val request_id: String,
            val status_code: Int,
        )

        @JsonClass(generateAdapter = true)
        class RestByEmailResponse(
            val request_id: String,
            val status_code: Int,
        )

        @JsonClass(generateAdapter = true)
        class StrengthCheckResponse()
    }

    @JsonClass(generateAdapter = true)
    class BasicResponse(data: BasicData) : StytchDataResponse<BasicData>(data)

    @JsonClass(generateAdapter = true)
    class AuthenticateResponse(data: AuthData) : StytchDataResponse<AuthData>(data)

    open class StytchDataResponse<T>(
        val data: T,
    )
}

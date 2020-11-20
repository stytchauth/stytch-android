package com.stytch.sdk.api


import com.stytch.sdk.api.requests.CreateUserRequest
import com.stytch.sdk.api.requests.SendMagicLingRequest
import com.stytch.sdk.api.requests.VerifyTokenRequest
import com.stytch.sdk.api.responses.CreateUserResponse
import com.stytch.sdk.api.responses.SendMagicLingResponse
import com.stytch.sdk.api.responses.VerifyTokenResponse
import retrofit2.Call
import retrofit2.http.*


interface StytchService {

    @POST("magic_links/send_by_email")
    fun sendMagicLink(
        @Body request: SendMagicLingRequest
    ): Call<SendMagicLingResponse>

    @POST("users")
    fun createUser(
        @Body request: CreateUserRequest
    ): Call<CreateUserResponse>

    @POST("magic_links/{token}/authenticate")
    fun verifyToken(
        @Path("token") token: String,
        @Body request: VerifyTokenRequest
    ): Call<VerifyTokenResponse>

}
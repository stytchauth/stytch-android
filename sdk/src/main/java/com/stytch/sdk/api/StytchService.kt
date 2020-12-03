package com.stytch.sdk.api


import com.stytch.sdk.api.requests.*
import com.stytch.sdk.api.responses.*
import retrofit2.Call
import retrofit2.http.*


internal interface StytchService {

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

    @POST("emails/{email_id}/send_verification")
    fun sendEmailVerification(
        @Path("email_id") emailId: String,
        @Body request: SendEmailVerificationRequest
    ): Call<SendEmailVerificationResponse>


    @POST("users/{user_id}")
    fun deleteUser(
        @Path("user_id") userId: String,
        @Body request: DeleteUserRequest
    ): Call<DeleteUserResponse>

}
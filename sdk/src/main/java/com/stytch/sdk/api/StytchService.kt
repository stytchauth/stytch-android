package com.stytch.sdk.api

import com.stytch.sdk.api.requests.CreateUserRequest
import com.stytch.sdk.api.requests.DeleteUserRequest
import com.stytch.sdk.api.requests.LoginOrInviteRequest
import com.stytch.sdk.api.requests.LoginOrSignUpRequest
import com.stytch.sdk.api.requests.SendEmailVerificationRequest
import com.stytch.sdk.api.requests.SendMagicLinkRequest
import com.stytch.sdk.api.requests.VerifyTokenRequest
import com.stytch.sdk.api.responses.CreateUserResponse
import com.stytch.sdk.api.responses.DeleteUserResponse
import com.stytch.sdk.api.responses.SendEmailVerificationResponse
import com.stytch.sdk.api.responses.SendMagicLingResponse
import com.stytch.sdk.api.responses.VerifyTokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

internal interface StytchService {

    @POST("magic_links/send_by_email")
    fun sendMagicLink(
        @Body request: SendMagicLinkRequest
    ): Call<SendMagicLingResponse>

    @POST("magic_links/login_or_invite")
    fun loginOrInvite(
        @Body request: LoginOrInviteRequest
    ): Call<SendMagicLingResponse>

    @POST("magic_links/login_or_create")
    fun loginOrSignUp(
        @Body request: LoginOrSignUpRequest
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

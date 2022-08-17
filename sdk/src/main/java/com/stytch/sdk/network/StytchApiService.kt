package com.stytch.sdk.network

import retrofit2.http.Body
import retrofit2.http.POST

internal interface StytchApiService {

    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest,
    ): StytchResponses.MagicLinks.Email.LoginOrCreateUserResponse

    @POST("magic_links/authenticate")
    suspend fun authenticate(
        @Body request: StytchRequests.MagicLinks.AuthenticateRequest
    ): StytchResponses.BasicResponse
}
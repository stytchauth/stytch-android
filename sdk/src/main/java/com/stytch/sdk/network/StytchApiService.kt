package com.stytch.sdk.network

import retrofit2.http.Body
import retrofit2.http.POST

internal interface StytchApiService {

    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequests.MagicLinks.Email.LoginOrCreateUserByEmailRequest,
    ): StytchResponses.MagicLinks.Email.LoginOrCreateUserByEmailResponse

    @POST("magic_links/authenticate")
    suspend fun authenticate(
        @Body request: StytchRequests.MagicLinks.Authenticate
    ): StytchResponses.BasicResponse

    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequests.MagicLinks.Email.LoginOrCreateUserByEmailRequest,
    ): StytchResponses.MagicLinks.Email.LoginOrCreateUserByEmailResponse

    @POST("magic_links/authenticate")
    suspend fun authenticateWithSession(
        @Body request: StytchRequests.MagicLinks.Authenticate
    ): StytchResponses.BasicResponse



}
package com.stytch.sdk.network

import retrofit2.http.Body
import retrofit2.http.POST

internal interface StytchApiService {

    //region Magic Links
    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest,
    ): StytchResponses.MagicLinks.Email.LoginOrCreateUserResponse

    @POST("magic_links/authenticate")
    suspend fun authenticate(
        @Body request: StytchRequests.MagicLinks.AuthenticateRequest,
    ): StytchResponses.AuthenticateResponse
    //endregion Magic Links

    //region Sessions
    @POST("sessions/authenticate")
    suspend fun authenticateSessions(@Body request: StytchRequests.Sessions.AuthenticateRequest): StytchResponses.Sessions.AuthenticateResponse

    @POST("sessions/revoke")
    suspend fun revokeSessions(@Body request: StytchRequests.Sessions.RevokeRequest): StytchResponses.Sessions.RevokeResponse
    //endregion Sessions
}
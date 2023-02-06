package com.stytch.sdk.b2b.network

import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.CommonRequests
import com.stytch.sdk.common.network.CommonResponses
import retrofit2.http.Body
import retrofit2.http.POST

internal interface StytchB2BApiService : ApiService {
    @POST("b2b/magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: B2BRequests.MagicLinks.Email.LoginOrCreateUserRequest
    ): CommonResponses.MagicLinks.Email.LoginOrCreateUserResponse

    @POST("b2b/magic_links/email/send/primary")
    suspend fun sendEmailMagicLinkPrimary(
        @Body request: B2BRequests.MagicLinks.SendRequest
    ): CommonResponses.SendResponse

    @POST("b2b/magic_links/email/send/secondary")
    suspend fun sendEmailMagicLinkSecondary(
        @Body request: B2BRequests.MagicLinks.SendRequest
    ): CommonResponses.SendResponse

    @POST("b2b/magic_links/authenticate")
    suspend fun authenticate(
        @Body request: CommonRequests.MagicLinks.AuthenticateRequest
    ): B2BResponses.AuthenticateResponse
    //endregion Magic Links

    //region Sessions
    @POST("b2b/sessions/authenticate")
    suspend fun authenticateSessions(
        @Body request: CommonRequests.Sessions.AuthenticateRequest
    ): B2BResponses.AuthenticateResponse

    @POST("sessions/revoke")
    suspend fun revokeSessions(): CommonResponses.Sessions.RevokeResponse
    //endregion Sessions
}

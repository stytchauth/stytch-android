package com.stytch.sdk.b2b.network

import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.CommonRequests
import com.stytch.sdk.common.network.CommonResponses
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface StytchB2BApiService : ApiService {
    @POST("b2b/magic_links/email/login_or_signup")
    suspend fun loginOrCreateUserByEmail(
        @Body request: B2BRequests.MagicLinks.Email.LoginOrCreateUserRequest
    ): CommonResponses.MagicLinks.Email.LoginOrCreateUserResponse

    @POST("b2b/magic_links/authenticate")
    suspend fun authenticate(
        @Body request: B2BRequests.MagicLinks.AuthenticateRequest
    ): B2BResponses.MagicLinks.AuthenticateResponse
    //endregion Magic Links

    //region Sessions
    @POST("b2b/sessions/authenticate")
    suspend fun authenticateSessions(
        @Body request: CommonRequests.Sessions.AuthenticateRequest
    ): B2BResponses.Sessions.AuthenticateResponse

    @POST("b2b/sessions/revoke")
    suspend fun revokeSessions(): CommonResponses.Sessions.RevokeResponse
    //endregion Sessions

    @GET("b2b/organizations/me")
    suspend fun getOrganization(): B2BResponses.Organizations.GetOrganizationResponse

    @GET("b2b/organizations/members/me")
    suspend fun getMember(): B2BResponses.Organizations.GetMemberResponse
}

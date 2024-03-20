package com.stytch.sdk.b2b.network

import com.stytch.sdk.b2b.network.models.B2BRequests
import com.stytch.sdk.b2b.network.models.B2BResponses
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.common.network.models.CommonResponses
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface StytchB2BApiService : ApiService {
    //region Magic Links
    @POST("b2b/magic_links/email/login_or_signup")
    suspend fun loginOrSignupByEmail(
        @Body request: B2BRequests.MagicLinks.Email.LoginOrSignupRequest,
    ): CommonResponses.BasicResponse

    @POST("b2b/magic_links/authenticate")
    suspend fun authenticate(
        @Body request: B2BRequests.MagicLinks.AuthenticateRequest,
    ): B2BResponses.MagicLinks.AuthenticateResponse

    @POST("b2b/magic_links/email/discovery/send")
    suspend fun sendDiscoveryMagicLink(
        @Body request: B2BRequests.MagicLinks.Discovery.SendRequest,
    ): CommonResponses.BasicResponse

    @POST("b2b/magic_links/discovery/authenticate")
    suspend fun authenticateDiscoveryMagicLink(
        @Body request: B2BRequests.MagicLinks.Discovery.AuthenticateRequest,
    ): B2BResponses.MagicLinks.DiscoveryAuthenticateResponse

    @POST("b2b/magic_links/email/invite")
    suspend fun sendInviteMagicLink(
        @Body request: B2BRequests.MagicLinks.Invite.InviteRequest,
    ): B2BResponses.MagicLinks.InviteResponse
    //endregion Magic Links

    //region Sessions
    @POST("b2b/sessions/authenticate")
    suspend fun authenticateSessions(
        @Body request: CommonRequests.Sessions.AuthenticateRequest,
    ): B2BResponses.Sessions.AuthenticateResponse

    @POST("b2b/sessions/revoke")
    suspend fun revokeSessions(): CommonResponses.Sessions.RevokeResponse

    @POST("b2b/sessions/exchange")
    suspend fun exchangeSession(
        @Body request: B2BRequests.Session.ExchangeRequest,
    ): B2BResponses.Sessions.ExchangeResponse
    //endregion Sessions

    //region Organizations
    @GET("b2b/organizations/me")
    suspend fun getOrganization(): B2BResponses.Organizations.GetOrganizationResponse

    @PUT("b2b/organizations/me")
    suspend fun updateOrganization(
        @Body request: B2BRequests.Organization.UpdateRequest,
    ): B2BResponses.Organizations.UpdateOrganizationResponse

    @DELETE("b2b/organizations/me")
    suspend fun deleteOrganization(): B2BResponses.Organizations.DeleteOrganizationResponse

    @DELETE("b2b/organizations/members/{memberId}")
    suspend fun deleteOrganizationMember(
        @Path(value = "memberId") memberId: String,
    ): B2BResponses.Organizations.DeleteMemberResponse

    @PUT("b2b/organizations/members/{memberId}/reactivate")
    suspend fun reactivateOrganizationMember(
        @Path(value = "memberId") memberId: String,
    ): B2BResponses.Organizations.ReactivateMemberResponse

    @DELETE("b2b/organizations/members/mfa_phone_numbers/{memberId}")
    suspend fun deleteOrganizationMemberMFAPhoneNumber(
        @Path(value = "memberId") memberId: String,
    ): B2BResponses.Organizations.DeleteOrganizationMemberAuthenticationFactorResponse

    @DELETE("b2b/organizations/members/totp/{memberId}")
    suspend fun deleteOrganizationMemberMFATOTP(
        @Path(value = "memberId") memberId: String,
    ): B2BResponses.Organizations.DeleteOrganizationMemberAuthenticationFactorResponse

    @DELETE("b2b/organizations/members/passwords/{passwordId}")
    suspend fun deleteOrganizationMemberPassword(
        @Path(value = "passwordId") passwordId: String,
    ): B2BResponses.Organizations.DeleteOrganizationMemberAuthenticationFactorResponse

    @GET("b2b/organizations/members/me")
    suspend fun getMember(): B2BResponses.Organizations.GetMemberResponse

    @PUT("b2b/organizations/members/update")
    suspend fun updateMember(
        @Body request: B2BRequests.Member.UpdateRequest,
    ): B2BResponses.Organizations.UpdateMemberResponse

    @DELETE("b2b/organizations/members/deletePhoneNumber")
    suspend fun deleteMFAPhoneNumber(): B2BResponses.Organizations.DeleteMemberAuthenticationFactorResponse

    @DELETE("b2b/organizations/members/deleteTOTP")
    suspend fun deleteMFATOTP(): B2BResponses.Organizations.DeleteMemberAuthenticationFactorResponse

    @DELETE("b2b/organizations/members/passwords/{id}")
    suspend fun deletePassword(
        @Path(value = "id") id: String,
    ): B2BResponses.Organizations.DeleteMemberAuthenticationFactorResponse

    @POST("b2b/organizations/members")
    suspend fun createMember(
        @Body request: B2BRequests.Organization.CreateMemberRequest,
    ): B2BResponses.Organizations.CreateMemberResponse

    @PUT("b2b/organizations/members/{memberId}")
    suspend fun updateOrganizationMember(
        @Path(value = "memberId") memberId: String,
        @Body request: B2BRequests.Organization.UpdateMemberRequest,
    ): B2BResponses.Organizations.UpdateOrganizationMemberResponse

    @POST("b2b/organizations/me/members/search")
    suspend fun searchMembers(
        @Body request: B2BRequests.Organization.SearchMembersRequest,
    ): B2BResponses.Organizations.MemberSearchResponse

    //endregion Organizations

    //region Passwords
    @POST("b2b/passwords/authenticate")
    suspend fun authenticatePassword(
        @Body request: B2BRequests.Passwords.AuthenticateRequest,
    ): B2BResponses.Passwords.AuthenticateResponse

    @POST("b2b/passwords/email/reset/start")
    suspend fun resetPasswordByEmailStart(
        @Body request: B2BRequests.Passwords.ResetByEmailStartRequest,
    ): B2BResponses.Passwords.ResetByEmailStartResponse

    @POST("b2b/passwords/email/reset")
    suspend fun resetPasswordByEmail(
        @Body request: B2BRequests.Passwords.ResetByEmailRequest,
    ): B2BResponses.Passwords.ResetByEmailResponse

    @POST("b2b/passwords/existing_password/reset")
    suspend fun resetPasswordByExisting(
        @Body request: B2BRequests.Passwords.ResetByExistingPasswordRequest,
    ): B2BResponses.Passwords.ResetByExistingPasswordResponse

    @POST("b2b/passwords/session/reset")
    suspend fun resetPasswordBySession(
        @Body request: B2BRequests.Passwords.ResetBySessionRequest,
    ): B2BResponses.Passwords.ResetBySessionResponse

    @POST("b2b/passwords/strength_check")
    suspend fun passwordStrengthCheck(
        @Body request: B2BRequests.Passwords.StrengthCheckRequest,
    ): B2BResponses.Passwords.StrengthCheckResponse
    //endregion Passwords

    //region Discovery
    @POST("b2b/discovery/organizations")
    suspend fun discoverOrganizations(
        @Body request: B2BRequests.Discovery.MembershipsRequest,
    ): B2BResponses.Discovery.DiscoverOrganizationsResponse

    @POST("b2b/discovery/intermediate_sessions/exchange")
    suspend fun intermediateSessionExchange(
        @Body request: B2BRequests.Discovery.SessionExchangeRequest,
    ): B2BResponses.Discovery.SessionExchangeResponse

    @POST("b2b/discovery/organizations/create")
    suspend fun createOrganization(
        @Body request: B2BRequests.Discovery.CreateRequest,
    ): B2BResponses.Discovery.CreateOrganizationResponse
    //endregion Discovery

    //region SSO
    @POST("b2b/sso/authenticate")
    suspend fun ssoAuthenticate(
        @Body request: B2BRequests.SSO.AuthenticateRequest,
    ): B2BResponses.SSO.AuthenticateResponse
    //endregion SSO

    //region Bootstrap
    @GET("projects/bootstrap/{publicToken}")
    suspend fun getBootstrapData(
        @Path(value = "publicToken") publicToken: String,
    ): CommonResponses.Bootstrap.BootstrapResponse
    //endregion Bootstrap

    //region Events
    @POST("events")
    suspend fun logEvent(
        // endpoint expects a list of events because JS SDK batches them
        @Body request: List<CommonRequests.Events.Event>,
    ): Response<Unit>
    //endregion Events

    //region OTP
    @POST("b2b/otps/sms/send")
    suspend fun sendSMSOTP(
        @Body request: B2BRequests.OTP.SMS.SendRequest,
    ): CommonResponses.BasicResponse

    @POST("b2b/otps/sms/authenticate")
    suspend fun authenticateSMSOTP(
        @Body request: B2BRequests.OTP.SMS.AuthenticateRequest,
    ): B2BResponses.OTP.SMS.AuthenticateResponse
    //endregion OTP
}

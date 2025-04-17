package com.stytch.sdk.b2b.network

import com.stytch.sdk.b2b.network.models.B2BRequests
import com.stytch.sdk.b2b.network.models.B2BResponses
import com.stytch.sdk.common.annotations.DFPPAEnabled
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
import retrofit2.http.Query

internal interface StytchB2BApiService : ApiService.ApiEndpoints {
    //region Magic Links
    @POST("b2b/magic_links/email/login_or_signup")
    suspend fun loginOrSignupByEmail(
        @Body request: B2BRequests.MagicLinks.Email.LoginOrSignupRequest,
    ): CommonResponses.BasicResponse

    @POST("b2b/magic_links/authenticate")
    @DFPPAEnabled
    suspend fun authenticate(
        @Body request: B2BRequests.MagicLinks.AuthenticateRequest,
    ): B2BResponses.MagicLinks.AuthenticateResponse

    @POST("b2b/magic_links/email/discovery/send")
    @DFPPAEnabled
    suspend fun sendDiscoveryMagicLink(
        @Body request: B2BRequests.MagicLinks.Discovery.SendRequest,
    ): CommonResponses.BasicResponse

    @POST("b2b/magic_links/discovery/authenticate")
    @DFPPAEnabled
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
    @DFPPAEnabled
    suspend fun authenticatePassword(
        @Body request: B2BRequests.Passwords.AuthenticateRequest,
    ): B2BResponses.Passwords.AuthenticateResponse

    @POST("b2b/passwords/email/reset/start")
    @DFPPAEnabled
    suspend fun resetPasswordByEmailStart(
        @Body request: B2BRequests.Passwords.ResetByEmailStartRequest,
    ): B2BResponses.Passwords.ResetByEmailStartResponse

    @POST("b2b/passwords/email/reset")
    @DFPPAEnabled
    suspend fun resetPasswordByEmail(
        @Body request: B2BRequests.Passwords.ResetByEmailRequest,
    ): B2BResponses.Passwords.ResetByEmailResponse

    @POST("b2b/passwords/existing_password/reset")
    @DFPPAEnabled
    suspend fun resetPasswordByExisting(
        @Body request: B2BRequests.Passwords.ResetByExistingPasswordRequest,
    ): B2BResponses.Passwords.ResetByExistingPasswordResponse

    @POST("b2b/passwords/session/reset")
    @DFPPAEnabled
    suspend fun resetPasswordBySession(
        @Body request: B2BRequests.Passwords.ResetBySessionRequest,
    ): B2BResponses.Passwords.ResetBySessionResponse

    @POST("b2b/passwords/strength_check")
    suspend fun passwordStrengthCheck(
        @Body request: B2BRequests.Passwords.StrengthCheckRequest,
    ): B2BResponses.Passwords.StrengthCheckResponse

    @POST("b2b/passwords/discovery/reset/start")
    suspend fun passwordDiscoveryResetByEmailStart(
        @Body request: B2BRequests.Passwords.Discovery.ResetByEmailStartRequest,
    ): B2BResponses.Passwords.Discovery.ResetByEmailStartResponse

    @POST("b2b/passwords/discovery/reset")
    suspend fun passwordDiscoveryResetByEmail(
        @Body request: B2BRequests.Passwords.Discovery.ResetByEmailRequest,
    ): B2BResponses.Passwords.Discovery.ResetByEmailResponse

    @POST("b2b/passwords/discovery/authenticate")
    suspend fun passwordDiscoveryAuthenticate(
        @Body request: B2BRequests.Passwords.Discovery.AuthenticateRequest,
    ): B2BResponses.Passwords.Discovery.AuthenticateResponse
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
    @DFPPAEnabled
    suspend fun ssoAuthenticate(
        @Body request: B2BRequests.SSO.AuthenticateRequest,
    ): B2BResponses.SSO.AuthenticateResponse

    @GET("b2b/sso")
    suspend fun ssoGetConnections(): B2BResponses.SSO.B2BSSOGetConnectionsResponse

    @DELETE("b2b/sso/{connectionId}")
    suspend fun ssoDeleteConnection(
        @Path(value = "connectionId") connectionId: String,
    ): B2BResponses.SSO.B2BSSODeleteConnectionResponse

    @POST("b2b/sso/saml")
    suspend fun ssoSamlCreate(
        @Body request: B2BRequests.SSO.SAMLCreateRequest,
    ): B2BResponses.SSO.B2BSSOSAMLCreateConnectionResponse

    @PUT("b2b/sso/saml/{connectionId}")
    suspend fun ssoSamlUpdate(
        @Path(value = "connectionId") connectionId: String,
        @Body request: B2BRequests.SSO.SAMLUpdateRequest,
    ): B2BResponses.SSO.B2BSSOSAMLUpdateConnectionResponse

    @PUT("b2b/sso/saml/{connectionId}/url")
    suspend fun ssoSamlUpdateByUrl(
        @Path(value = "connectionId") connectionId: String,
        @Body request: B2BRequests.SSO.B2BSSOSAMLUpdateConnectionByURLRequest,
    ): B2BResponses.SSO.B2BSSOSAMLUpdateConnectionByURLResponse

    @DELETE("b2b/sso/saml/{connectionId}/verification_certificates/{certificateId}")
    suspend fun ssoSamlDeleteVerificationCertificate(
        @Path(value = "connectionId") connectionId: String,
        @Path(value = "certificateId") certificateId: String,
    ): B2BResponses.SSO.B2BSSOSAMLDeleteVerificationCertificateResponse

    @POST("b2b/sso/oidc")
    suspend fun ssoOidcCreate(
        @Body request: B2BRequests.SSO.OIDCCreateRequest,
    ): B2BResponses.SSO.B2BSSOOIDCCreateConnectionResponse

    @PUT("b2b/sso/oidc/{connectionId}")
    suspend fun ssoOidcUpdate(
        @Path(value = "connectionId") connectionId: String,
        @Body request: B2BRequests.SSO.OIDCUpdateRequest,
    ): B2BResponses.SSO.B2BSSOOIDCUpdateConnectionResponse

    @GET("b2b/sso/discovery/connections")
    suspend fun ssoDiscoveryConnections(
        @Query(value = "email_address") emailAddress: String,
    ): B2BResponses.SSO.B2BSSODiscoveryConnectionResponse
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
    @DFPPAEnabled
    suspend fun sendSMSOTP(
        @Body request: B2BRequests.OTP.SMS.SendRequest,
    ): CommonResponses.BasicResponse

    @POST("b2b/otps/sms/authenticate")
    @DFPPAEnabled
    suspend fun authenticateSMSOTP(
        @Body request: B2BRequests.OTP.SMS.AuthenticateRequest,
    ): B2BResponses.OTP.SMS.AuthenticateResponse

    @POST("b2b/otps/email/login_or_signup")
    @DFPPAEnabled
    suspend fun otpEmailLoginOrSignup(
        @Body request: B2BRequests.OTP.Email.LoginOrSignupRequest,
    ): B2BResponses.OTP.Email.LoginOrSignupResponse

    @POST("b2b/otps/email/authenticate")
    @DFPPAEnabled
    suspend fun otpEmailAuthenticate(
        @Body request: B2BRequests.OTP.Email.AuthenticateRequest,
    ): B2BResponses.OTP.Email.AuthenticateResponse

    @POST("b2b/otps/email/discovery/send")
    @DFPPAEnabled
    suspend fun otpEmailDiscoverySend(
        @Body request: B2BRequests.OTP.Email.Discovery.SendRequest,
    ): B2BResponses.OTP.Email.Discovery.SendResponse

    @POST("b2b/otps/email/discovery/authenticate")
    @DFPPAEnabled
    suspend fun otpEmailDiscoveryAuthenticate(
        @Body request: B2BRequests.OTP.Email.Discovery.AuthenticateRequest,
    ): B2BResponses.OTP.Email.Discovery.AuthenticateResponse
    //endregion OTP

    //region TOTP
    @POST("b2b/totp")
    @DFPPAEnabled
    suspend fun createTOTP(
        @Body request: B2BRequests.TOTP.CreateRequest,
    ): B2BResponses.TOTP.CreateResponse

    @POST("b2b/totp/authenticate")
    @DFPPAEnabled
    suspend fun authenticateTOTP(
        @Body request: B2BRequests.TOTP.AuthenticateRequest,
    ): B2BResponses.TOTP.AuthenticateResponse
    //endregion TOTP

    //region RecoveryCodes
    @GET("b2b/recovery_codes")
    suspend fun getRecoveryCodes(): B2BResponses.RecoveryCodes.GetResponse

    @POST("b2b/recovery_codes/rotate")
    @DFPPAEnabled
    suspend fun rotateRecoveryCodes(): B2BResponses.RecoveryCodes.RotateResponse

    @POST("b2b/recovery_codes/recover")
    @DFPPAEnabled
    suspend fun recoverRecoveryCodes(
        @Body request: B2BRequests.RecoveryCodes.RecoverRequest,
    ): B2BResponses.RecoveryCodes.RecoverResponse
    //endregion RecoveryCodes

    //region OAuth
    @POST("b2b/oauth/authenticate")
    @DFPPAEnabled
    suspend fun oauthAuthenticate(
        @Body request: B2BRequests.OAuth.AuthenticateRequest,
    ): B2BResponses.OAuth.AuthenticateResponse

    @POST("b2b/oauth/discovery/authenticate")
    @DFPPAEnabled
    suspend fun oauthDiscoveryAuthenticate(
        @Body request: B2BRequests.OAuth.DiscoveryAuthenticateRequest,
    ): B2BResponses.OAuth.DiscoveryAuthenticateResponse
    //endregion OAuth

    //region SearchManager
    @POST("b2b/organizations/search")
    suspend fun searchOrganizations(
        @Body request: B2BRequests.SearchManager.SearchOrganization,
    ): B2BResponses.SearchManager.SearchOrganizationResponse

    @POST("b2b/organizations/members/search")
    suspend fun searchOrganizationMembers(
        @Body request: B2BRequests.SearchManager.SearchMember,
    ): B2BResponses.SearchManager.SearchMemberResponse
    //endregion SearchManager

    //region SCIM
    @POST("b2b/scim")
    suspend fun scimCreateConnection(
        @Body request: B2BRequests.SCIM.B2BSCIMCreateConnection,
    ): B2BResponses.SCIM.B2BSCIMCreateConnectionResponse

    @PUT("b2b/scim/{connectionId}")
    suspend fun scimUpdateConnection(
        @Path(value = "connectionId") connectionId: String,
        @Body request: B2BRequests.SCIM.B2BSCIMUpdateConnection,
    ): B2BResponses.SCIM.B2BSCIMUpdateConnectionResponse

    @DELETE("b2b/scim/{connectionId}")
    suspend fun scimDeleteConnection(
        @Path(value = "connectionId") connectionId: String,
    ): B2BResponses.SCIM.B2BSCIMDeleteConnectionResponse

    @GET("b2b/scim")
    suspend fun scimGetConnection(): B2BResponses.SCIM.B2BSCIMGetConnectionResponse

    @POST("b2b/scim/groups")
    suspend fun scimGetConnectionGroups(
        @Body request: B2BRequests.SCIM.B2BSCIMGetConnectionGroups,
    ): B2BResponses.SCIM.B2BSCIMGetConnectionGroupsResponse

    @POST("b2b/scim/rotate/start")
    suspend fun scimRotateStart(
        @Body request: B2BRequests.SCIM.B2BSCIMRotateConnectionRequest,
    ): B2BResponses.SCIM.B2BSCIMRotateStartResponse

    @POST("b2b/scim/rotate/complete")
    suspend fun scimRotateComplete(
        @Body request: B2BRequests.SCIM.B2BSCIMRotateConnectionRequest,
    ): B2BResponses.SCIM.B2BSCIMRotateCompleteResponse

    @POST("b2b/scim/rotate/cancel")
    suspend fun scimRotateCancel(
        @Body request: B2BRequests.SCIM.B2BSCIMRotateConnectionRequest,
    ): B2BResponses.SCIM.B2BSCIMRotateCancelResponse
    //endregion SCIM
}

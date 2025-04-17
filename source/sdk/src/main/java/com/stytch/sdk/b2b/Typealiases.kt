package com.stytch.sdk.b2b

import com.stytch.sdk.b2b.network.models.B2BDiscoveryOTPEmailAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.B2BDiscoveryOTPEmailSendResponseData
import com.stytch.sdk.b2b.network.models.B2BEMLAuthenticateData
import com.stytch.sdk.b2b.network.models.B2BOTPsEmailAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.B2BOTPsEmailLoginOrSignupResponseData
import com.stytch.sdk.b2b.network.models.B2BPasswordDiscoveryAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.B2BPasswordDiscoveryResetByEmailResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMCreateConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMDeleteConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMGetConnectionGroupsResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMGetConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMRotateCancelResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMRotateCompleteResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMRotateStartResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMUpdateConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSSODeleteConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSSODiscoveryConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSSOGetConnectionsResponseData
import com.stytch.sdk.b2b.network.models.B2BSSOOIDCCreateConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSSOOIDCUpdateConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSSOSAMLCreateConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSSOSAMLDeleteVerificationCertificateResponseData
import com.stytch.sdk.b2b.network.models.B2BSSOSAMLUpdateConnectionByURLResponseData
import com.stytch.sdk.b2b.network.models.B2BSSOSAMLUpdateConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSearchMemberResponseData
import com.stytch.sdk.b2b.network.models.B2BSearchOrganizationResponseData
import com.stytch.sdk.b2b.network.models.DiscoveredOrganizationsResponseData
import com.stytch.sdk.b2b.network.models.DiscoveryAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.EmailResetResponseData
import com.stytch.sdk.b2b.network.models.IntermediateSessionExchangeResponseData
import com.stytch.sdk.b2b.network.models.MemberDeleteAuthenticationFactorData
import com.stytch.sdk.b2b.network.models.MemberResponseCommonData
import com.stytch.sdk.b2b.network.models.MemberResponseData
import com.stytch.sdk.b2b.network.models.MemberSearchResponseData
import com.stytch.sdk.b2b.network.models.OAuthAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.OrganizationCreateResponseData
import com.stytch.sdk.b2b.network.models.OrganizationDeleteResponseData
import com.stytch.sdk.b2b.network.models.OrganizationMemberDeleteResponseData
import com.stytch.sdk.b2b.network.models.OrganizationResponseData
import com.stytch.sdk.b2b.network.models.OrganizationUpdateResponseData
import com.stytch.sdk.b2b.network.models.PasswordResetByExistingPasswordResponseData
import com.stytch.sdk.b2b.network.models.PasswordsAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.RecoveryCodeGetResponseData
import com.stytch.sdk.b2b.network.models.RecoveryCodeRecoverResponseData
import com.stytch.sdk.b2b.network.models.RecoveryCodeRotateResponseData
import com.stytch.sdk.b2b.network.models.SMSAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.SSOAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.SessionExchangeResponseData
import com.stytch.sdk.b2b.network.models.SessionResetResponseData
import com.stytch.sdk.b2b.network.models.SessionsAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.StrengthCheckResponseData
import com.stytch.sdk.b2b.network.models.TOTPAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.TOTPCreateResponseData
import com.stytch.sdk.b2b.network.models.UpdateMemberResponseData
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.BasicData

/**
 * Type alias for StytchResult<OrganizationResponseData> used for organization.get() responses
 */
public typealias OrganizationResponse = StytchResult<OrganizationResponseData>

/**
 * Type alias for StytchResult<MemberResponseData> used for member.get() responses
 */
public typealias MemberResponse = StytchResult<MemberResponseData>

/**
 * Type alias for StytchResult<EmailResetResponseData> used for passwords.resetByEmail() responses
 */
public typealias EmailResetResponse = StytchResult<EmailResetResponseData>

/**
 * Type alias for StytchResult<StrengthCheckResponseData> used for passwords.strengthCheck() responses
 */
public typealias PasswordStrengthCheckResponse = StytchResult<StrengthCheckResponseData>

/**
 * Type alias for StytchResult<SessionResetResponseData> used for passwords.resetBySession() responses
 */
public typealias SessionResetResponse = StytchResult<SessionResetResponseData>

/**
 *
 * Type alias for StytchResult<DiscoveredOrganizationsResponseData> used for discovery.organizations() responses
 */
public typealias DiscoverOrganizationsResponse = StytchResult<DiscoveredOrganizationsResponseData>

/**
 * Type alias for StytchResult<IntermediateSessionExchangeResponseData> used for discovery.exchangeSession() responses
 */
public typealias IntermediateSessionExchangeResponse = StytchResult<IntermediateSessionExchangeResponseData>

/**
 * Type alias for StytchResult<OrganizationCreateResponseData> used for discovery.create() responses
 */
public typealias OrganizationCreateResponse = StytchResult<OrganizationCreateResponseData>

/**
 * Type alias for StytchResult<DiscoveryAuthenticateResponseData> used for magicLinks.discovery.authenticate() responses
 */
public typealias DiscoveryEMLAuthResponse = StytchResult<DiscoveryAuthenticateResponseData>

/**
 * Type alias for StytchResult<SSOAuthenticateResponseData> used for sso.authenticate() responses
 */
public typealias SSOAuthenticateResponse = StytchResult<SSOAuthenticateResponseData>

/**
 * Type alias for StytchResult<SessionExchangeResponseData> used for session.exchange() responses
 */
public typealias SessionExchangeResponse = StytchResult<SessionExchangeResponseData>

/**
 * Type alias for StytchResult<UpdateMemberResponseData> used for UpdateMember responses
 */
public typealias UpdateMemberResponse = StytchResult<UpdateMemberResponseData>

/**
 * Type alias for StytchResult<MemberDeleteAuthenticationFactorData> used for MemberDeleteAuthenticationFactor responses
 */
public typealias DeleteMemberAuthenticationFactorResponse = StytchResult<MemberDeleteAuthenticationFactorData>

/**
 * Type alias for StytchResult<OrganizationUpdateResponseData> used for Organization Update responses
 */
public typealias UpdateOrganizationResponse = StytchResult<OrganizationUpdateResponseData>

/**
 * Type alias for StytchResult<OrganizationDeleteResponseData> used for Organization Delete responses
 */
public typealias DeleteOrganizationResponse = StytchResult<OrganizationDeleteResponseData>

/**
 * Type alias for StytchResult<OrganizationMemberDeleteResponseData> used for Organization Delete Member responses
 */
public typealias DeleteMemberResponse = StytchResult<OrganizationMemberDeleteResponseData>

/**
 * Type alias for StytchResult<MemberResponseCommonData> used for Reactivate Organization Member responses
 */
public typealias ReactivateMemberResponse = StytchResult<MemberResponseCommonData>

/**
 * Type alias for StytchResult<MemberResponseCommonData> used for Delete Organization Member authentication factor
 * responses
 */
public typealias DeleteOrganizationMemberAuthenticationFactorResponse = StytchResult<MemberResponseCommonData>

/**
 * Type alias for StytchResult<MemberResponseCommonData> used for Create Organization Member responses
 */
public typealias CreateMemberResponse = StytchResult<MemberResponseCommonData>

/**
 * Type alias for StytchResult<MemberResponseCommonData> used for Update Organization Member responses
 */
public typealias UpdateOrganizationMemberResponse = StytchResult<MemberResponseCommonData>

/**
 * Type alias for StytchResult<MemberSearchResponseData> used for Member Search responses
 */
public typealias MemberSearchResponse = StytchResult<MemberSearchResponseData>

/**
 * Type alias for StytchResult<BasicData> used for non-specific responses
 */
public typealias BasicResponse = StytchResult<BasicData>

/**
 * Type alias for StytchResult<TOTPCreateResponseData> used for TOTP Create responses
 */
public typealias TOTPCreateResponse = StytchResult<TOTPCreateResponseData>

/**
 * Type alias for StytchResult<TOTPAuthenticateResponseData> used for TOTP Authenticate responses
 */
public typealias TOTPAuthenticateResponse = StytchResult<TOTPAuthenticateResponseData>

/**
 * Type alias for StytchResult<RecoveryCodeGetResponseData> used for Recovery Code Get responses
 */
public typealias RecoveryCodesGetResponse = StytchResult<RecoveryCodeGetResponseData>

/**
 * Type alias for StytchResult<RecoveryCodeRotateResponseData> used for Recovery Code Rotate responses
 */
public typealias RecoveryCodesRotateResponse = StytchResult<RecoveryCodeRotateResponseData>

/**
 * Type alias for StytchResult<RecoveryCodeRecoverResponseData> used for Recovery Code Recover responses
 */
public typealias RecoveryCodesRecoverResponse = StytchResult<RecoveryCodeRecoverResponseData>

/**
 * Type alias for StytchResult<OAuthAuthenticateResponseData> used for OAuth Authentication responses
 */
public typealias OAuthAuthenticateResponse = StytchResult<OAuthAuthenticateResponseData>

/**
 * Type alias for StytchResult<DiscoveryAuthenticateResponseData> used for Discovery Authentication responses
 */
public typealias OAuthDiscoveryAuthenticateResponse = StytchResult<DiscoveryAuthenticateResponseData>

/**
 * Type alias for StytchResult<B2BSSOGetConnectionsResponseData> used for SSO Get Connection responses
 */
public typealias B2BSSOGetConnectionsResponse = StytchResult<B2BSSOGetConnectionsResponseData>

/**
 * Type alias for StytchResult<B2BSSODeleteConnectionResponseData> used for SSO Connection Deletion responses
 */
public typealias B2BSSODeleteConnectionResponse = StytchResult<B2BSSODeleteConnectionResponseData>

/**
 * Type alias for StytchResult<B2BSSOSAMLCreateConnectionResponseData> used for SSO SAML Connection Creation responses
 */
public typealias B2BSSOSAMLCreateConnectionResponse = StytchResult<B2BSSOSAMLCreateConnectionResponseData>

/**
 * Type alias for StytchResult<B2BSSOSAMLUpdateConnectionResponseData> used for SSO SAML Connection Update responses
 */
public typealias B2BSSOSAMLUpdateConnectionResponse = StytchResult<B2BSSOSAMLUpdateConnectionResponseData>

/**
 * Type alias for StytchResult<B2BSSOSAMLUpdateConnectionByURLResponseData> used for SSO SAML Connection Update by
 * URL responses
 */
public typealias B2BSSOSAMLUpdateConnectionByURLResponse = StytchResult<B2BSSOSAMLUpdateConnectionByURLResponseData>

/**
 * Type alias for StytchResult<B2BSSOSAMLDeleteVerificationCertificateResponseData> used for SSO SAML Verification
 * Certificate Deletion responses
 */
public typealias B2BSSOSAMLDeleteVerificationCertificateResponse =
    StytchResult<B2BSSOSAMLDeleteVerificationCertificateResponseData>

/**
 * Type alias for StytchResult<B2BSSOOIDCCreateConnectionResponseData> used for SSO OIDC Connection Create responses
 */
public typealias B2BSSOOIDCCreateConnectionResponse = StytchResult<B2BSSOOIDCCreateConnectionResponseData>

/**
 * Type alias for StytchResult<B2BSSOOIDCUpdateConnectionResponseData> used for SSO OIDC Connection Update responses
 */
public typealias B2BSSOOIDCUpdateConnectionResponse = StytchResult<B2BSSOOIDCUpdateConnectionResponseData>

/**
 * Type alias for StytchResult<B2BSearchOrganizationResponseData> used for Search Organization responses
 */
public typealias B2BSearchOrganizationResponse = StytchResult<B2BSearchOrganizationResponseData>

/**
 * Type alias for StytchResult<B2BSearchMemberResponseData> used for Search Member responses
 */
public typealias B2BSearchMemberResponse = StytchResult<B2BSearchMemberResponseData>

/**
 * Type alias for StytchResult<PasswordsAuthenticateResponseData> used for Passwords Authenticate responses
 */
public typealias PasswordsAuthenticateResponse = StytchResult<PasswordsAuthenticateResponseData>

/**
 * Type alias for StytchResult<PasswordResetByExistingPasswordResponseData> used for Password Reset By Existing
 * Password responses
 */
public typealias PasswordResetByExistingPasswordResponse = StytchResult<PasswordResetByExistingPasswordResponseData>

/**
 * Type alias for StytchResult<SessionsAuthenticateResponseData> used for Sessions Authenticate responses
 */
public typealias SessionsAuthenticateResponse = StytchResult<SessionsAuthenticateResponseData>

/**
 * Type alias for StytchResult<SMSAuthenticateResponseData> used for SMS OTP Authenticate responses
 */
public typealias SMSAuthenticateResponse = StytchResult<SMSAuthenticateResponseData>

/**
 * Type alias for StytchResult<B2BEMLAuthenticateData> used for Email Magic Link Authenticate responses
 */
public typealias EMLAuthenticateResponse = StytchResult<B2BEMLAuthenticateData>

/**
 * Type alias for StytchResult<B2BSCIMCreateConnectionResponseData> used for SCIM Create Connection responses
 */
public typealias SCIMCreateConnectionResponse = StytchResult<B2BSCIMCreateConnectionResponseData>

/**
 * Type alias for StytchResult<B2BSCIMUpdateConnectionResponseData> used for SCIM Update Connection responses
 */
public typealias SCIMUpdateConnectionResponse = StytchResult<B2BSCIMUpdateConnectionResponseData>

/**
 * Type alias for StytchResult<B2BSCIMDeleteConnectionResponseData> used for SCIM Delete Connection responses
 */
public typealias SCIMDeleteConnectionResponse = StytchResult<B2BSCIMDeleteConnectionResponseData>

/**
 * Type alias for StytchResult<B2BSCIMGetConnectionResponseData> used for SCIM Get Connection responses
 */
public typealias SCIMGetConnectionResponse = StytchResult<B2BSCIMGetConnectionResponseData>

/**
 * Type alias for StytchResult<B2BSCIMGetConnectionGroupsResponseData> used for SCIM Get Connection Groups responses
 */
public typealias SCIMGetConnectionGroupsResponse = StytchResult<B2BSCIMGetConnectionGroupsResponseData>

/**
 * Type alias for StytchResult<B2BSCIMRotateStartResponseData> used for SCIM Rotate Start responses
 */
public typealias SCIMRotateStartResponse = StytchResult<B2BSCIMRotateStartResponseData>

/**
 * Type alias for StytchResult<B2BSCIMRotateCompleteResponseData> used for SCIM Rotate Complete responses
 */
public typealias SCIMRotateCompleteResponse = StytchResult<B2BSCIMRotateCompleteResponseData>

/**
 * Type alias for StytchResult<B2BSCIMRotateCancelResponseData> used for SCIM Rotate Cancel responses
 */
public typealias SCIMRotateCancelResponse = StytchResult<B2BSCIMRotateCancelResponseData>

/**
 * Type alias for StytchResult<B2BOTPsEmailLoginOrSignupResponseData> used for Email OTP LoginOrSignup responses
 */
public typealias EmailOTPLoginOrSignupResponse = StytchResult<B2BOTPsEmailLoginOrSignupResponseData>

/**
 * Type alias for StytchResult<B2BOTPsEmailAuthenticateResponseData> used for Email OTP Authenticate responses
 */
public typealias EmailOTPAuthenticateResponse = StytchResult<B2BOTPsEmailAuthenticateResponseData>

/**
 * Type alias for StytchResult<B2BDiscoveryOTPEmailSendResponseData> used for Discovery Email OTP Send responses
 */
public typealias EmailOTPDiscoverySendResponse = StytchResult<B2BDiscoveryOTPEmailSendResponseData>

/**
 * Type alias for StytchResult<DiscoveryAuthenticateResponseData> used for Discovery Email OTP Authenticate responses
 */
public typealias EmailOTPDiscoveryAuthenticateResponse = StytchResult<B2BDiscoveryOTPEmailAuthenticateResponseData>

/**
 * Type alias for StytchResult<B2BPasswordDiscoveryResetByEmailResponseData> used for Password Discovery
 * Reset By Email responses
 */
public typealias B2BPasswordDiscoveryResetByEmailResponse = StytchResult<B2BPasswordDiscoveryResetByEmailResponseData>

/**
 * Type alias for StytchResult<B2BPasswordDiscoveryAuthenticateResponseData> used for Password Discovery
 * Authenticate responses
 */
public typealias B2BPasswordDiscoveryAuthenticateResponse = StytchResult<B2BPasswordDiscoveryAuthenticateResponseData>

/**
 * Type alias for StytchResult<B2BSSODiscoveryConnectionResponseData> used for SSO Discover Connections responses
 */
public typealias B2BSSODiscoveryConnectionResponse = StytchResult<B2BSSODiscoveryConnectionResponseData>

package com.stytch.sdk.b2b

import com.stytch.sdk.b2b.network.models.B2BEMLAuthenticateData
import com.stytch.sdk.b2b.network.models.B2BSSODeleteConnectionResponseData
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

public typealias UpdateOrganizationResponse = StytchResult<OrganizationUpdateResponseData>

public typealias DeleteOrganizationResponse = StytchResult<OrganizationDeleteResponseData>

public typealias DeleteMemberResponse = StytchResult<OrganizationMemberDeleteResponseData>

public typealias ReactivateMemberResponse = StytchResult<MemberResponseCommonData>

public typealias DeleteOrganizationMemberAuthenticationFactorResponse = StytchResult<MemberResponseCommonData>

public typealias CreateMemberResponse = StytchResult<MemberResponseCommonData>

public typealias UpdateOrganizationMemberResponse = StytchResult<MemberResponseCommonData>

public typealias MemberSearchResponse = StytchResult<MemberSearchResponseData>

public typealias BasicResponse = StytchResult<BasicData>

public typealias TOTPCreateResponse = StytchResult<TOTPCreateResponseData>

public typealias TOTPAuthenticateResponse = StytchResult<TOTPAuthenticateResponseData>

public typealias RecoveryCodesGetResponse = StytchResult<RecoveryCodeGetResponseData>

public typealias RecoveryCodesRotateResponse = StytchResult<RecoveryCodeRotateResponseData>

public typealias RecoveryCodesRecoverResponse = StytchResult<RecoveryCodeRecoverResponseData>

public typealias OAuthAuthenticateResponse = StytchResult<OAuthAuthenticateResponseData>

public typealias OAuthDiscoveryAuthenticateResponse = StytchResult<DiscoveryAuthenticateResponseData>

public typealias B2BSSOGetConnectionsResponse = StytchResult<B2BSSOGetConnectionsResponseData>

public typealias B2BSSODeleteConnectionResponse = StytchResult<B2BSSODeleteConnectionResponseData>

public typealias B2BSSOSAMLCreateConnectionResponse = StytchResult<B2BSSOSAMLCreateConnectionResponseData>

public typealias B2BSSOSAMLUpdateConnectionResponse = StytchResult<B2BSSOSAMLUpdateConnectionResponseData>

public typealias B2BSSOSAMLUpdateConnectionByURLResponse = StytchResult<B2BSSOSAMLUpdateConnectionByURLResponseData>

public typealias B2BSSOSAMLDeleteVerificationCertificateResponse =
    StytchResult<B2BSSOSAMLDeleteVerificationCertificateResponseData>

public typealias B2BSSOOIDCCreateConnectionResponse = StytchResult<B2BSSOOIDCCreateConnectionResponseData>

public typealias B2BSSOOIDCUpdateConnectionResponse = StytchResult<B2BSSOOIDCUpdateConnectionResponseData>

public typealias B2BSearchOrganizationResponse = StytchResult<B2BSearchOrganizationResponseData>

public typealias B2BSearchMemberResponse = StytchResult<B2BSearchMemberResponseData>

public typealias PasswordsAuthenticateResponse = StytchResult<PasswordsAuthenticateResponseData>

public typealias PasswordResetByExistingPasswordResponse = StytchResult<PasswordResetByExistingPasswordResponseData>

public typealias SessionsAuthenticateResponse = StytchResult<SessionsAuthenticateResponseData>

public typealias SMSAuthenticateResponse = StytchResult<SMSAuthenticateResponseData>

public typealias EMLAuthenticateResponse = StytchResult<B2BEMLAuthenticateData>

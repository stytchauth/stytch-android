package com.stytch.sdk.b2b

import com.stytch.sdk.b2b.network.models.DiscoveredOrganizationsResponseData
import com.stytch.sdk.b2b.network.models.DiscoveryAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.EmailResetResponseData
import com.stytch.sdk.b2b.network.models.IB2BAuthData
import com.stytch.sdk.b2b.network.models.IntermediateSessionExchangeResponseData
import com.stytch.sdk.b2b.network.models.MemberResponseData
import com.stytch.sdk.b2b.network.models.OrganizationCreateResponseData
import com.stytch.sdk.b2b.network.models.OrganizationResponseData
import com.stytch.sdk.b2b.network.models.SSOAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.SessionExchangeResponseData
import com.stytch.sdk.b2b.network.models.SessionResetResponseData
import com.stytch.sdk.b2b.network.models.StrengthCheckResponseData
import com.stytch.sdk.common.StytchResult

/**
 * Type alias for StytchResult<IB2BAuthData> used for authentication responses
 */
public typealias AuthResponse = StytchResult<IB2BAuthData>

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

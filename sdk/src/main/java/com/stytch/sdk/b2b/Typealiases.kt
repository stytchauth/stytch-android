package com.stytch.sdk.b2b

import com.stytch.sdk.b2b.network.models.EmailResetResponseData
import com.stytch.sdk.b2b.network.models.IB2BAuthData
import com.stytch.sdk.b2b.network.models.MemberResponseData
import com.stytch.sdk.b2b.network.models.OrganizationResponseData
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

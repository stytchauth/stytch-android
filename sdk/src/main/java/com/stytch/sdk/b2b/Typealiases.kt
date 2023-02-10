package com.stytch.sdk.b2b

import com.stytch.sdk.b2b.network.IB2BAuthData
import com.stytch.sdk.b2b.network.Organization
import com.stytch.sdk.common.StytchResult

/**
 * Type alias for StytchResult<AuthData> used for authentication responses
 */
public typealias AuthResponse = StytchResult<IB2BAuthData>

public typealias OrganizationResponse = StytchResult<Organization>

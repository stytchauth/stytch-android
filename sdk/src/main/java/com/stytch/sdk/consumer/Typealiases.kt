package com.stytch.sdk.consumer

import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.network.BiometricsAuthData
import com.stytch.sdk.consumer.network.CreateResponse
import com.stytch.sdk.consumer.network.DeleteAuthenticationFactorData
import com.stytch.sdk.consumer.network.IAuthData
import com.stytch.sdk.consumer.network.OAuthData
import com.stytch.sdk.consumer.network.UserData

/**
 * Type alias for StytchResult<AuthData> used for authentication responses
 */
public typealias AuthResponse = StytchResult<IAuthData>

/**
 * Type alias for StytchResult<CreateResponse> used for PasswordsCreate responses
 */
public typealias PasswordsCreateResponse = StytchResult<CreateResponse>

/**
 * Type alias for StytchResult<UserData> used for GetUser responses
 */
public typealias UserResponse = StytchResult<UserData>

/**
 * Type alias for StytchResult<DeleteAuthenticationFactorData> used for deleting authentication factors from a user
 */
public typealias DeleteFactorResponse = StytchResult<DeleteAuthenticationFactorData>

/**
 * Type alias for StytchResult<BiometricsAuthData> used for Biometrics authentication responses
 */
public typealias BiometricsAuthResponse = StytchResult<BiometricsAuthData>

/**
 * Type alias for StytchResult<OAuthData> used for Third Party OAuth authentication responses
 */
public typealias OAuthAuthenticatedResponse = StytchResult<OAuthData>

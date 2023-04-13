package com.stytch.sdk.consumer

import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.common.network.models.StrengthCheckResponse
import com.stytch.sdk.consumer.network.models.BiometricsAuthData
import com.stytch.sdk.consumer.network.models.CreateResponse
import com.stytch.sdk.consumer.network.models.DeleteAuthenticationFactorData
import com.stytch.sdk.consumer.network.models.IAuthData
import com.stytch.sdk.consumer.network.models.INativeOAuthData
import com.stytch.sdk.consumer.network.models.OAuthData
import com.stytch.sdk.consumer.network.models.UserData

/**
 * Type alias for StytchResult<IAuthData> used for authentication responses
 */
public typealias AuthResponse = StytchResult<IAuthData>

/**
 * Type alias for StytchResult<INativeOAuthData> used for Native OAuth (Google One Tap) authentication responses
 */
public typealias NativeOAuthResponse = StytchResult<INativeOAuthData>

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

/**
 * Type alias for StytchResult<LoginOrCreateOTPData> used for loginOrCreateOTP responses
 */
public typealias LoginOrCreateOTPResponse = StytchResult<LoginOrCreateOTPData>

/**
 * Type alias for StytchResult<StrengthCheckResponse> used for PasswordsStrengthCheck responses
 */
public typealias PasswordsStrengthCheckResponse = StytchResult<StrengthCheckResponse>

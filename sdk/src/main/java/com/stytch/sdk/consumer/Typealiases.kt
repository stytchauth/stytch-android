package com.stytch.sdk.consumer

import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.common.network.models.OTPSendResponseData
import com.stytch.sdk.consumer.network.models.BiometricsAuthData
import com.stytch.sdk.consumer.network.models.CreateResponse
import com.stytch.sdk.consumer.network.models.DeleteAuthenticationFactorData
import com.stytch.sdk.consumer.network.models.IAuthData
import com.stytch.sdk.consumer.network.models.INativeOAuthData
import com.stytch.sdk.consumer.network.models.OAuthData
import com.stytch.sdk.consumer.network.models.StrengthCheckResponse
import com.stytch.sdk.consumer.network.models.UpdateUserResponseData
import com.stytch.sdk.consumer.network.models.UserData
import com.stytch.sdk.consumer.network.models.UserSearchResponseData
import com.stytch.sdk.consumer.network.models.WebAuthnAuthenticateStartData
import com.stytch.sdk.consumer.network.models.WebAuthnRegisterData
import com.stytch.sdk.consumer.network.models.WebAuthnRegisterStartData
import com.stytch.sdk.consumer.network.models.WebAuthnUpdateResponseData

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
 * Type alias for StytchResult<OTPSendResponseData> used for OTPSend responses
 */
public typealias OTPSendResponse = StytchResult<OTPSendResponseData>

/**
 * Type alias for StytchResult<StrengthCheckResponse> used for PasswordsStrengthCheck responses
 */
public typealias PasswordsStrengthCheckResponse = StytchResult<StrengthCheckResponse>

/**
 * Type alias for StytchResult<UpdateUserResponseData> used for UpdateUser responses
 */
public typealias UpdateUserResponse = StytchResult<UpdateUserResponseData>

/**
 * Type alias for StytchResult<UserSearchResponseData>
 */
public typealias SearchUserResponse = StytchResult<UserSearchResponseData>

/**
 * Type alias for StytchResult<WebAuthnRegisterData> used for WebAuthn registration responses
 */
public typealias WebAuthnRegisterResponse = StytchResult<WebAuthnRegisterData>

/**
 * Type alias for StytchResult<WebAuthnAuthenticateData> used for WebAuthn authentication responses
 */
public typealias WebAuthnRegisterStartResponse = StytchResult<WebAuthnRegisterStartData>

/**
 * Type alias for StytchResult<WebAuthnAuthenticateData> used for WebAuthn authentication responses
 */
public typealias WebAuthnAuthenticateStartResponse = StytchResult<WebAuthnAuthenticateStartData>

/**
 * Type alias for StytchResult<WebAuthnUpdateResponseData> used for WebAuthn update responses
 */
public typealias WebAuthnUpdateResponse = StytchResult<WebAuthnUpdateResponseData>

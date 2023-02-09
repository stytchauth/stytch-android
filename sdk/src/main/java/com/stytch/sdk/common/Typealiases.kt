package com.stytch.sdk.common

import com.stytch.sdk.common.network.BasicData
import com.stytch.sdk.common.network.LoginOrCreateOTPData
import com.stytch.sdk.common.network.StrengthCheckResponse

/**
 * Type alias for StytchResult<BasicData> used for basic responses
 */
public typealias BaseResponse = StytchResult<BasicData>

/**
 * Type alias for StytchResult<LoginOrCreateOTPData> used for loginOrCreateOTP responses
 */
public typealias LoginOrCreateOTPResponse = StytchResult<LoginOrCreateOTPData>

/**
 * Type alias for StytchResult<StrengthCheckResponse> used for PasswordsStrengthCheck responses
 */
public typealias PasswordsStrengthCheckResponse = StytchResult<StrengthCheckResponse>


@file:Suppress("ktlint:standard:filename")

package com.stytch.sdk.common

import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.NoResponseData

/**
 * Type alias for StytchResult<BasicData> used for basic responses
 */
public typealias BaseResponse = StytchResult<BasicData>

/**
 * Type alias for StytchResult<NoResponseData> used for empty responses
 */
public typealias NoResponseResponse = StytchResult<NoResponseData>

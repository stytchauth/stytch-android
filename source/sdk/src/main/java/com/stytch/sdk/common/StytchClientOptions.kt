package com.stytch.sdk.common

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.dfp.DFPType

/**
 * Options for configuring the StytchClient
 * @property endpointOptions Defines custom endpoints used by the SDK
 * @property dfpType Determines if the webview or native DFP implementation is used. Defaults to Native.
 * @property defaultSessionDuration Determines the default session duration for all authentication requests.
 * Defaults to 5 minutes.
 * @property enableAutomaticSessionExtension If true, the session heartbeat will attempt to extend the session duration,
 * instead of only checking the validity.
 */
@JacocoExcludeGenerated
public data class StytchClientOptions
    @JvmOverloads
    constructor(
        val endpointOptions: EndpointOptions = EndpointOptions(),
        val dfpType: DFPType = DFPType.Native,
        val defaultSessionDuration: Int = DEFAULT_SESSION_TIME_MINUTES,
        val enableAutomaticSessionExtension: Boolean = false,
    )

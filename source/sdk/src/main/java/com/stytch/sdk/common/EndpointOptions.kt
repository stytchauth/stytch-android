package com.stytch.sdk.common

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated

/**
 * Defines custom endpoints used by the SDK
 * @property dfppaDomain the domain that should be used for DFPPA
 */
@JacocoExcludeGenerated
public data class EndpointOptions
    @JvmOverloads
    constructor(
        val dfppaDomain: String = "telemetry.stytch.com",
    )

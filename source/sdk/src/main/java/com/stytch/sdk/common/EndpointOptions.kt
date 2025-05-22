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
        val testDomain: String = TEST_BASE_DOMAIN,
        val liveDomain: String = LIVE_BASE_DOMAIN,
        val dfppaDomain: String = DEFAULT_DFPPA_DOMAIN,
    )

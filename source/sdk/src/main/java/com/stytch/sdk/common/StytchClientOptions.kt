package com.stytch.sdk.common

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated

/**
 * Options for configuring the StytchClient
 * @property endpointOptions Defines custom endpoints used by the SDK
 */
@JacocoExcludeGenerated
public data class StytchClientOptions
    @JvmOverloads
    constructor(
        val endpointOptions: EndpointOptions = EndpointOptions(),
    )

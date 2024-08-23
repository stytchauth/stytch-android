package com.stytch.sdk.common

/**
 * Options for configuring the StytchClient
 * @property endpointOptions Defines custom endpoints used by the SDK
 */
public data class StytchClientOptions(
    val endpointOptions: EndpointOptions = EndpointOptions(),
)

package com.stytch.sdk.common

/**
 * Defines custom endpoints used by the SDK
 * @property dfppaDomain the domain that should be used for DFPPA
 */
public data class EndpointOptions(
    val dfppaDomain: String = "https://telemetry.stytch.com",
)

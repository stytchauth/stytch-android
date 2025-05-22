package com.stytch.sdk.common.utils

import com.stytch.sdk.common.API_URL_PATH
import com.stytch.sdk.common.EndpointOptions

internal fun getApiUrl(
    cnameDomain: String?,
    endpointOptions: EndpointOptions,
    isTestToken: Boolean,
): String {
    val domain = cnameDomain ?: if (isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain
    return "https://$domain$API_URL_PATH"
}

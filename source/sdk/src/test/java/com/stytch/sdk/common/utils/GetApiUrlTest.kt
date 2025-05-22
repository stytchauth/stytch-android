package com.stytch.sdk.common.utils

import com.stytch.sdk.common.API_URL_PATH
import com.stytch.sdk.common.EndpointOptions
import com.stytch.sdk.common.LIVE_BASE_DOMAIN
import com.stytch.sdk.common.TEST_BASE_DOMAIN
import org.junit.Test

internal class GetApiUrlTest {
    @Test
    fun `getApiUrl returns as expected`() {
        // with cname
        var apiUrl = getApiUrl("my.custom.domain", EndpointOptions(), true)
        assert(apiUrl == "https://my.custom.domain$API_URL_PATH")

        // no cname, no custom domain, test token
        apiUrl = getApiUrl(null, EndpointOptions(), true)
        assert(apiUrl == "https://${TEST_BASE_DOMAIN}$API_URL_PATH")

        // no cname, no custom domain, live token
        apiUrl = getApiUrl(null, EndpointOptions(), false)
        assert(apiUrl == "https://${LIVE_BASE_DOMAIN}$API_URL_PATH")

        val customDomains =
            EndpointOptions(
                testDomain = "my-test-domain.com",
                liveDomain = "my-live-domain.com",
            )
        // no cname, with custom domains, test token
        apiUrl = getApiUrl(null, customDomains, true)
        assert(apiUrl == "https://${customDomains.testDomain}$API_URL_PATH")

        // no cname, with custom domains, live token
        apiUrl = getApiUrl(null, customDomains, false)
        assert(apiUrl == "https://${customDomains.liveDomain}$API_URL_PATH")
    }
}

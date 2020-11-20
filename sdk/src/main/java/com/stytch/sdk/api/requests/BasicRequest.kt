package com.stytch.sdk.api.requests

import com.stytch.sdk.api.Api

open class BasicRequest {

    val attributes: Attributes

    init {
        attributes = Attributes(
            Api.getIPAddress(true),
            System.getProperty("http.agent")
        )
    }

    class Attributes(
        val ip_address: String?,
        val user_agent: String?
    )
}
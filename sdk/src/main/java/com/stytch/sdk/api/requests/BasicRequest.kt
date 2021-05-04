package com.stytch.sdk.api.requests

import com.google.gson.annotations.SerializedName
import com.stytch.sdk.api.Api

public open class BasicRequest {

    @SerializedName("attributes")
    public val attributes: Attributes = Attributes(
        Api.getIPAddress(true),
        System.getProperty("http.agent"),
    )

    public class Attributes(
        @SerializedName("ip_address") public val ip_address: String?,
        @SerializedName("user_agent") public val user_agent: String?,
    )
}

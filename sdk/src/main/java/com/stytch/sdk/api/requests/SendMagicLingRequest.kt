package com.stytch.sdk.api.requests

import com.google.gson.annotations.SerializedName

public class SendMagicLingRequest(
    @SerializedName("email")
    public val email: String,
    @SerializedName("magic_link_url")
    public val magic_link_url: String,
    @SerializedName("expiration_minutes")
    public val expiration_minutes: Long
) : BasicRequest() {
}
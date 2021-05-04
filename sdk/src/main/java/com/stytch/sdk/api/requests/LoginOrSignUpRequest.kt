package com.stytch.sdk.api.requests

import com.google.gson.annotations.SerializedName

public class LoginOrSignUpRequest(
    @SerializedName("email") public val email: String,
    @SerializedName("login_magic_link_url") public val login_magic_link_url: String,
    @SerializedName("signup_magic_link_url") public val signup_magic_link_url: String,
    @SerializedName("login_expiration_minutes") public val login_expiration_minutes: Long,
    @SerializedName("signup_expiration_minutes") public val signup_expiration_minutes: Long,
) : BasicRequest() {
}

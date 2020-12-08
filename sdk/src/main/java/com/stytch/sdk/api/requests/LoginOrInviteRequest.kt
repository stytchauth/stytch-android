package com.stytch.sdk.api.requests

import com.google.gson.annotations.SerializedName

public class LoginOrInviteRequest(
    @SerializedName("email")
    public val email: String,
    @SerializedName("login_magic_link_url")
    public val login_magic_link_url: String,
    @SerializedName("invite_magic_link_url")
    public val invite_magic_link_url: String,
    @SerializedName("login_expiration_minutes")
    public val login_expiration_minutes: Long,
    @SerializedName("invite_expiration_minutes")
    public val invite_expiration_minutes: Long
) : BasicRequest() {
}
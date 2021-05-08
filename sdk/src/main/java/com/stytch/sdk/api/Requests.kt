package com.stytch.sdk.api

import com.google.gson.annotations.SerializedName

public open class BasicRequest {

    @SerializedName("attributes")
    public val attributes: Attributes = Attributes(
        StytchApi.getIPAddress(true),
        System.getProperty("http.agent"),
    )

    public class Attributes(
        @SerializedName("ip_address") public val ip_address: String?,
        @SerializedName("user_agent") public val user_agent: String?,
    )
}

public class CreateUserRequest(
    @SerializedName("email") public val email: String,
) : BasicRequest()

public class DeleteUserRequest : BasicRequest()

public class LoginOrSignUpRequest(
    @SerializedName("email") public val email: String,
    @SerializedName("login_magic_link_url") public val login_magic_link_url: String,
    @SerializedName("signup_magic_link_url") public val signup_magic_link_url: String,
    @SerializedName("login_expiration_minutes") public val login_expiration_minutes: Long,
    @SerializedName("signup_expiration_minutes") public val signup_expiration_minutes: Long,
    @SerializedName("create_user_as_pending") public val create_user_as_pending: Boolean,
) : BasicRequest()

public class SendEmailVerificationRequest() : BasicRequest()

public class SendMagicLinkRequest(
    @SerializedName("email") public val email: String,
    @SerializedName("magic_link_url") public val magic_link_url: String,
    @SerializedName("expiration_minutes") public val expiration_minutes: Long,
) : BasicRequest()

public class VerifyTokenRequest(
    @SerializedName("ip_match_required") public val ip_match_required: Boolean? = null,
    @SerializedName("user_agent_match_required") public val user_agent_match_required: Boolean? = null,
) : BasicRequest()

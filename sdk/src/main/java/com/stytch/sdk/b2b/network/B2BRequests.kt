package com.stytch.sdk.b2b.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal object B2BRequests {
    object MagicLinks {
        object Email {
            @JsonClass(generateAdapter = true)
            data class LoginOrCreateUserRequest(
                val email: String,
                @Json(name = "organization_id")
                val organizationId: String,
                @Json(name = "login_magic_link_url")
                val loginMagicLinkUrl: String?,
                @Json(name = "code_challenge")
                val codeChallenge: String,
                @Json(name = "code_challenge_method")
                val codeChallengeMethod: String,
                @Json(name = "login_template_id")
                val loginTemplateId: String? = null,
                @Json(name = "signup_template_id")
                val signupTemplateId: String? = null,
            )
        }

        @JsonClass(generateAdapter = true)
        data class SendRequest(
            val email: String,
            @Json(name = "organization_id")
            val organizationId: String,
            @Json(name = "login_magic_link_url")
            val loginMagicLinkUrl: String?,
            @Json(name = "signup_magic_link_url")
            val signupMagicLinkUrl: String?,
            @Json(name = "login_expiration_minutes")
            val loginExpirationMinutes: Int?,
            @Json(name = "signup_expiration_minutes")
            val signupExpirationMinutes: Int?,
            @Json(name = "login_template_id")
            val loginTemplateId: String?,
            @Json(name = "signup_template_id")
            val signupTemplateId: String?,
            @Json(name = "code_challenge")
            val codeChallenge: String?,
        )
    }
}

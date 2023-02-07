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
                @Json(name = "login_redirect_url")
                val loginRedirectUrl: String?,
                @Json(name = "signup_redirect_url")
                val signupRedirectUrl: String?,
                @Json(name = "pkce_code_challenge")
                val codeChallenge: String,
                @Json(name = "login_template_id")
                val loginTemplateId: String? = null,
                @Json(name = "signup_template_id")
                val signupTemplateId: String? = null,
            )
        }

        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "magic_links_token")
            val token: String,
            @Json(name = "pkce_code_verifier")
            val codeVerifier: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }
}

package com.stytch.sdk.network

import com.squareup.moshi.JsonClass
import com.stytch.sdk.StytchDataTypes

internal object StytchRequests {
    @JsonClass(generateAdapter = true)
    data class LoginOrCreateUserByEmailRequest(
        val email: String,
        val login_magic_link_url: String?,
        val signup_magic_link_url: String?,
        val login_expiration_minutes: Int?,
        val signup_expiration_minutes: Int?,
        val create_user_as_pending: Boolean?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    data class Authenticate(
        val token: String,
        val session_duration_minutes: Int
    )
}
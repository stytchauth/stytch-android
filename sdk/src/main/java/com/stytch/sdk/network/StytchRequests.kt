package com.stytch.sdk.network

import com.squareup.moshi.JsonClass

internal object StytchRequests {

    object MagicLinks{
        object Email{
            @JsonClass(generateAdapter = true)
            data class LoginOrCreateUserByEmailRequest(
                val email: String,
                val login_magic_link_url: String?
            )
        }

        @JsonClass(generateAdapter = true)
        data class Authenticate(
            val token: String,
            val session_duration_minutes: Int
        )
    }
}
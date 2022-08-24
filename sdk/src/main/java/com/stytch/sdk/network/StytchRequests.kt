package com.stytch.sdk.network

import com.squareup.moshi.JsonClass

internal object StytchRequests {

    object MagicLinks {
        object Email {
            @JsonClass(generateAdapter = true)
            data class LoginOrCreateUserRequest(
                val email: String,
                val login_magic_link_url: String?,
                val code_challenge: String,
                val code_challenge_method: String,
            )
        }

        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val token: String,
            val code_verifier: String,
            val session_duration_minutes: Int,
        )
    }

    object Sessions {
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val session_duration_minutes: Int?,
        )
    }

    object Passwords {
        @JsonClass(generateAdapter = true)
        data class CreateRequest(
            val email: String,
            val password: String,
            val session_duration_minutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val email: String,
            val password: String,
            val session_duration_minutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class ResetByEmailStartRequest(
            val email: String,
            val code_challenge: String,
            val code_challenge_method: String,
            val login_redirect_url: String?,
            val login_expiration_minutes: Int?,
            val reset_password_redirect_url: String?,
            val reset_password_expiration_minutes: Int?,
        )

        @JsonClass(generateAdapter = true)
        data class RestByEmailRequest(
            val token: String,
            val password: String,
            val session_duration_minutes: Int,
            val code_verifier: String,
        )

        @JsonClass(generateAdapter = true)
        data class StrengthCheckRequest(
            val email: String?,
            val password: String,
        )

    }

    object OTP {
        @JsonClass(generateAdapter = true)
        data class SMS(
            val phone_number: String,
            val expiration_minutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class WhatsApp(
            val phone_number: String,
            val expiration_minutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class Email(
            val email: String,
            val expiration_minutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class Authenticate(
            val token: String,
            val session_duration_minutes: Int,
        )
    }
}

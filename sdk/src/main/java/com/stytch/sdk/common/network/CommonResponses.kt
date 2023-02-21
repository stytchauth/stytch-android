package com.stytch.sdk.common.network

import com.squareup.moshi.JsonClass

internal object CommonResponses {
    object MagicLinks {
        object Email {
            @JsonClass(generateAdapter = true)
            class LoginOrCreateUserResponse(data: BasicData) : StytchDataResponse<BasicData>(data)
        }
    }

    object Passwords {
        @JsonClass(generateAdapter = true)
        class PasswordsStrengthCheckResponse(data: StrengthCheckResponse) :
            StytchDataResponse<StrengthCheckResponse>(data)
    }

    object Biometrics {
        @JsonClass(generateAdapter = true)
        class RegisterStartResponse(data: BiometricsStartResponse) :
            StytchDataResponse<BiometricsStartResponse>(data)

        @JsonClass(generateAdapter = true)
        class AuthenticateStartResponse(data: BiometricsStartResponse) :
            StytchDataResponse<BiometricsStartResponse>(data)
    }

    object Sessions {
        @JsonClass(generateAdapter = true)
        class RevokeResponse(data: BasicData) : StytchDataResponse<BasicData>(data)
    }

    @JsonClass(generateAdapter = true)
    class BasicResponse(data: BasicData) : StytchDataResponse<BasicData>(data)

    @JsonClass(generateAdapter = true)
    class SendResponse(data: BasicData) : StytchDataResponse<BasicData>(data)
}

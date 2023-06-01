package com.stytch.sdk.consumer.network.models

import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData

internal object ConsumerResponses {
    object Passwords {

        @JsonClass(generateAdapter = true)
        class PasswordsCreateResponse(data: CreateResponse) : StytchDataResponse<CreateResponse>(data)

        @JsonClass(generateAdapter = true)
        class PasswordsStrengthCheckResponse(data: StrengthCheckResponse) :
            StytchDataResponse<StrengthCheckResponse>(data)
    }

    object Biometrics {
        @JsonClass(generateAdapter = true)
        class RegisterResponse(data: BiometricsAuthData) :
            StytchDataResponse<BiometricsAuthData>(data)

        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(data: BiometricsAuthData) :
            StytchDataResponse<BiometricsAuthData>(data)
    }

    object User {
        @JsonClass(generateAdapter = true)
        class UserResponse(data: UserData) : StytchDataResponse<UserData>(data)

        @JsonClass(generateAdapter = true)
        class DeleteFactorResponse(data: DeleteAuthenticationFactorData) :
            StytchDataResponse<DeleteAuthenticationFactorData>(data)
    }

    object OAuth {
        @JsonClass(generateAdapter = true)
        class OAuthAuthenticateResponse(data: OAuthData) : StytchDataResponse<OAuthData>(data)

        @JsonClass(generateAdapter = true)
        class NativeOAuthAuthenticateResponse(data: NativeOAuthData) : StytchDataResponse<NativeOAuthData>(data)
    }

    @JsonClass(generateAdapter = true)
    class AuthenticateResponse(data: AuthData) : StytchDataResponse<AuthData>(data)

    @JsonClass(generateAdapter = true)
    class LoginOrCreateOTPResponse(data: LoginOrCreateOTPData) : StytchDataResponse<LoginOrCreateOTPData>(data)
}

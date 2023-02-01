package com.stytch.sdk.network

import com.squareup.moshi.JsonClass
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sdk.network.responseData.BasicData
import com.stytch.sdk.network.responseData.BiometricsAuthData
import com.stytch.sdk.network.responseData.BiometricsStartResponse
import com.stytch.sdk.network.responseData.CreateResponse
import com.stytch.sdk.network.responseData.DeleteAuthenticationFactorData
import com.stytch.sdk.network.responseData.LoginOrCreateOTPData
import com.stytch.sdk.network.responseData.OAuthData
import com.stytch.sdk.network.responseData.StrengthCheckResponse
import com.stytch.sdk.network.responseData.UserData

internal object StytchResponses {

    object MagicLinks {
        object Email {
            @JsonClass(generateAdapter = true)
            class LoginOrCreateUserResponse(data: BasicData) : StytchDataResponse<BasicData>(data)
        }
    }

    object Sessions {
        @JsonClass(generateAdapter = true)
        class RevokeResponse(data: BasicData) : StytchDataResponse<BasicData>(data)
    }

    object Passwords {

        @JsonClass(generateAdapter = true)
        class PasswordsCreateResponse(data: CreateResponse) : StytchDataResponse<CreateResponse>(data)

        @JsonClass(generateAdapter = true)
        class PasswordsStrengthCheckResponse(data: StrengthCheckResponse) :
            StytchDataResponse<StrengthCheckResponse>(data)
    }

    object Biometrics {
        @JsonClass(generateAdapter = true)
        class RegisterStartResponse(data: BiometricsStartResponse) :
            StytchDataResponse<BiometricsStartResponse>(data)

        @JsonClass(generateAdapter = true)
        class RegisterResponse(data: BiometricsAuthData) :
            StytchDataResponse<BiometricsAuthData>(data)

        @JsonClass(generateAdapter = true)
        class AuthenticateStartResponse(data: BiometricsStartResponse) :
            StytchDataResponse<BiometricsStartResponse>(data)

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
    }

    @JsonClass(generateAdapter = true)
    class BasicResponse(data: BasicData) : StytchDataResponse<BasicData>(data)

    @JsonClass(generateAdapter = true)
    class AuthenticateResponse(data: AuthData) : StytchDataResponse<AuthData>(data)

    @JsonClass(generateAdapter = true)
    class LoginOrCreateOTPResponse(data: LoginOrCreateOTPData) : StytchDataResponse<LoginOrCreateOTPData>(data)

    @JsonClass(generateAdapter = true)
    class SendResponse(data: BasicData) : StytchDataResponse<BasicData>(data)

    open class StytchDataResponse<T>(
        val data: T,
    )
}

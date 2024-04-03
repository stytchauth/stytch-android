package com.stytch.sdk.consumer.network.models

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.common.network.models.OTPSendResponseData

internal object ConsumerResponses {
    object Passwords {
        @Keep
        @JsonClass(generateAdapter = true)
        class PasswordsCreateResponse(data: CreateResponse) : StytchDataResponse<CreateResponse>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class PasswordsStrengthCheckResponse(data: StrengthCheckResponse) :
            StytchDataResponse<StrengthCheckResponse>(data)
    }

    object Biometrics {
        @Keep
        @JsonClass(generateAdapter = true)
        class RegisterResponse(data: BiometricsAuthData) :
            StytchDataResponse<BiometricsAuthData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(data: BiometricsAuthData) :
            StytchDataResponse<BiometricsAuthData>(data)
    }

    object User {
        @Keep
        @JsonClass(generateAdapter = true)
        class UserResponse(data: UserData) : StytchDataResponse<UserData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class DeleteFactorResponse(data: DeleteAuthenticationFactorData) :
            StytchDataResponse<DeleteAuthenticationFactorData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class UpdateUserResponse(data: UpdateUserResponseData) : StytchDataResponse<UpdateUserResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class UserSearchResponse(data: UserSearchResponseData) : StytchDataResponse<UserSearchResponseData>(data)
    }

    object OAuth {
        @Keep
        @JsonClass(generateAdapter = true)
        class OAuthAuthenticateResponse(data: OAuthData) : StytchDataResponse<OAuthData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class NativeOAuthAuthenticateResponse(data: NativeOAuthData) : StytchDataResponse<NativeOAuthData>(data)
    }

    object WebAuthn {
        @Keep
        @JsonClass(generateAdapter = true)
        class RegisterStartResponse(data: WebAuthnRegisterStartData) :
            StytchDataResponse<WebAuthnRegisterStartData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class RegisterResponse(data: WebAuthnRegisterData) : StytchDataResponse<WebAuthnRegisterData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(data: WebAuthnAuthenticateStartData) :
            StytchDataResponse<WebAuthnAuthenticateStartData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class UpdateResponse(data: WebAuthnUpdateResponseData) : StytchDataResponse<WebAuthnUpdateResponseData>(data)
    }

    @Keep
    @JsonClass(generateAdapter = true)
    class AuthenticateResponse(data: AuthData) : StytchDataResponse<AuthData>(data)

    @Keep
    @JsonClass(generateAdapter = true)
    class LoginOrCreateOTPResponse(data: LoginOrCreateOTPData) : StytchDataResponse<LoginOrCreateOTPData>(data)

    @Keep
    @JsonClass(generateAdapter = true)
    class OTPSendResponse(data: OTPSendResponseData) : StytchDataResponse<OTPSendResponseData>(data)
}

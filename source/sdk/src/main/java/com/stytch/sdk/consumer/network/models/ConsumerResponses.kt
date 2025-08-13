package com.stytch.sdk.consumer.network.models

import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.common.network.models.OTPSendResponseData

internal object ConsumerResponses {
    object Passwords {
        @JsonClass(generateAdapter = true)
        class PasswordsCreateResponse(
            data: CreateResponse,
        ) : StytchDataResponse<CreateResponse>(data)

        @JsonClass(generateAdapter = true)
        class PasswordsStrengthCheckResponse(
            data: StrengthCheckResponse,
        ) : StytchDataResponse<StrengthCheckResponse>(data)

        @JsonClass(generateAdapter = true)
        class B2CPasswordsAuthenticateResponse(
            data: B2CPasswordsAuthenticateResponseData,
        ) : StytchDataResponse<B2CPasswordsAuthenticateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class PasswordsEmailResetResponse(
            data: PasswordsEmailResetResponseData,
        ) : StytchDataResponse<PasswordsEmailResetResponseData>(data)

        @JsonClass(generateAdapter = true)
        class PasswordsExistingPasswordResetResponse(
            data: PasswordsExistingPasswordResetResponseData,
        ) : StytchDataResponse<PasswordsExistingPasswordResetResponseData>(data)

        @JsonClass(generateAdapter = true)
        class PasswordsSessionResetResponse(
            data: PasswordsSessionResetResponseData,
        ) : StytchDataResponse<PasswordsSessionResetResponseData>(data)
    }

    object Biometrics {
        @JsonClass(generateAdapter = true)
        class RegisterResponse(
            data: BiometricsAuthData,
        ) : StytchDataResponse<BiometricsAuthData>(data)

        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: BiometricsAuthData,
        ) : StytchDataResponse<BiometricsAuthData>(data)
    }

    object User {
        @JsonClass(generateAdapter = true)
        class UserResponse(
            data: UserData,
        ) : StytchDataResponse<UserData>(data)

        @JsonClass(generateAdapter = true)
        class DeleteFactorResponse(
            data: DeleteAuthenticationFactorData,
        ) : StytchDataResponse<DeleteAuthenticationFactorData>(data)

        @JsonClass(generateAdapter = true)
        class UpdateUserResponse(
            data: UpdateUserResponseData,
        ) : StytchDataResponse<UpdateUserResponseData>(data)

        @JsonClass(generateAdapter = true)
        class UserSearchResponse(
            data: UserSearchResponseData,
        ) : StytchDataResponse<UserSearchResponseData>(data)
    }

    object OAuth {
        @JsonClass(generateAdapter = true)
        class OAuthAuthenticateResponse(
            data: OAuthData,
        ) : StytchDataResponse<OAuthData>(data)

        @JsonClass(generateAdapter = true)
        class NativeOAuthAuthenticateResponse(
            data: NativeOAuthData,
        ) : StytchDataResponse<NativeOAuthData>(data)
    }

    object WebAuthn {
        @JsonClass(generateAdapter = true)
        class RegisterStartResponse(
            data: WebAuthnRegisterStartData,
        ) : StytchDataResponse<WebAuthnRegisterStartData>(data)

        @JsonClass(generateAdapter = true)
        class RegisterResponse(
            data: WebAuthnRegisterData,
        ) : StytchDataResponse<WebAuthnRegisterData>(data)

        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: WebAuthnAuthenticateStartData,
        ) : StytchDataResponse<WebAuthnAuthenticateStartData>(data)

        @JsonClass(generateAdapter = true)
        class UpdateResponse(
            data: WebAuthnUpdateResponseData,
        ) : StytchDataResponse<WebAuthnUpdateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class WebAuthnAuthenticateResponse(
            data: WebAuthnAuthenticateResponseData,
        ) : StytchDataResponse<WebAuthnAuthenticateResponseData>(data)
    }

    object Crypto {
        @JsonClass(generateAdapter = true)
        class AuthenticateStartResponse(
            data: CryptoWalletAuthenticateStartResponseData,
        ) : StytchDataResponse<CryptoWalletAuthenticateStartResponseData>(data)

        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: CryptoWalletsAuthenticateResponseData,
        ) : StytchDataResponse<CryptoWalletsAuthenticateResponseData>(data)
    }

    object OTP {
        @JsonClass(generateAdapter = true)
        class OTPsAuthenticateResponse(
            data: OTPsAuthenticateResponseData,
        ) : StytchDataResponse<OTPsAuthenticateResponseData>(data)
    }

    object TOTP {
        @JsonClass(generateAdapter = true)
        class TOTPCreateResponse(
            data: TOTPCreateResponseData,
        ) : StytchDataResponse<TOTPCreateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class TOTPAuthenticateResponse(
            data: TOTPAuthenticateResponseData,
        ) : StytchDataResponse<TOTPAuthenticateResponseData>(data)

        @JsonClass(generateAdapter = true)
        class TOTPRecoveryCodesResponse(
            data: TOTPRecoveryCodesResponseData,
        ) : StytchDataResponse<TOTPRecoveryCodesResponseData>(data)

        @JsonClass(generateAdapter = true)
        class TOTPRecoverResponse(
            data: TOTPRecoverResponseData,
        ) : StytchDataResponse<TOTPRecoverResponseData>(data)
    }

    @JsonClass(generateAdapter = true)
    class AuthenticateResponse(
        data: AuthData,
    ) : StytchDataResponse<AuthData>(data)

    @JsonClass(generateAdapter = true)
    class LoginOrCreateOTPResponse(
        data: LoginOrCreateOTPData,
    ) : StytchDataResponse<LoginOrCreateOTPData>(data)

    @JsonClass(generateAdapter = true)
    class OTPSendResponse(
        data: OTPSendResponseData,
    ) : StytchDataResponse<OTPSendResponseData>(data)
}

package com.stytch.sdk.b2b.network.models

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.BasicData

internal object B2BResponses {
    object MagicLinks {
        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(data: B2BEMLAuthenticateData) : StytchDataResponse<B2BEMLAuthenticateData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class DiscoveryAuthenticateResponse(
            data: DiscoveryAuthenticateResponseData,
        ) : StytchDataResponse<DiscoveryAuthenticateResponseData>(data)
    }

    object Sessions {
        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(data: B2BAuthData) : StytchDataResponse<B2BAuthData>(data)
    }

    object Organizations {
        @Keep
        @JsonClass(generateAdapter = true)
        class GetOrganizationResponse(
            data: OrganizationResponseData,
        ) : StytchDataResponse<OrganizationResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class GetMemberResponse(data: MemberResponseData) : StytchDataResponse<MemberResponseData>(data)
    }

    object Passwords {
        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: PasswordsAuthenticateResponseData,
        ) : StytchDataResponse<PasswordsAuthenticateResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class ResetByEmailStartResponse(data: BasicData) : StytchDataResponse<BasicData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class ResetByEmailResponse(data: EmailResetResponseData) : StytchDataResponse<EmailResetResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class ResetByExistingPasswordResponse(data: B2BAuthData) : StytchDataResponse<B2BAuthData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class ResetBySessionResponse(
            data: SessionResetResponseData,
        ) : StytchDataResponse<SessionResetResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class StrengthCheckResponse(
            data: StrengthCheckResponseData,
        ) : StytchDataResponse<StrengthCheckResponseData>(data)
    }

    object Discovery {
        @Keep
        @JsonClass(generateAdapter = true)
        class DiscoverOrganizationsResponse(
            data: DiscoveredOrganizationsResponseData,
        ) : StytchDataResponse<DiscoveredOrganizationsResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class SessionExchangeResponse(
            data: IntermediateSessionExchangeResponseData,
        ) : StytchDataResponse<IntermediateSessionExchangeResponseData>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class CreateOrganizationResponse(
            data: OrganizationCreateResponseData,
        ) : StytchDataResponse<OrganizationCreateResponseData>(data)
    }

    object SSO {
        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(
            data: SSOAuthenticateResponseData,
        ) : StytchDataResponse<SSOAuthenticateResponseData>(data)
    }
}

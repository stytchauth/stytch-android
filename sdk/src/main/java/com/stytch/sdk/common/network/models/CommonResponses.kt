package com.stytch.sdk.common.network.models

import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.StytchDataResponse

internal object CommonResponses {
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

    object Bootstrap {
        @JsonClass(generateAdapter = true)
        class BootstrapResponse(data: BootstrapData) : StytchDataResponse<BootstrapData>(data)
    }

    @JsonClass(generateAdapter = true)
    class BasicResponse(data: BasicData) : StytchDataResponse<BasicData>(data)

    @JsonClass(generateAdapter = true)
    class SendResponse(data: BasicData) : StytchDataResponse<BasicData>(data)

    @JsonClass(generateAdapter = true)
    class NoResponse(data: NoResponseData) : StytchDataResponse<NoResponseData>(data)
}

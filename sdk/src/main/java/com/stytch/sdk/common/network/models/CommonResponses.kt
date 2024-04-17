package com.stytch.sdk.common.network.models

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.StytchDataResponse

internal object CommonResponses {
    object Biometrics {
        @Keep
        @JsonClass(generateAdapter = true)
        class RegisterStartResponse(data: BiometricsStartResponse) :
            StytchDataResponse<BiometricsStartResponse>(data)

        @Keep
        @JsonClass(generateAdapter = true)
        class AuthenticateStartResponse(data: BiometricsStartResponse) :
            StytchDataResponse<BiometricsStartResponse>(data)
    }

    object Sessions {
        @Keep
        @JsonClass(generateAdapter = true)
        class RevokeResponse(data: BasicData) : StytchDataResponse<BasicData>(data)
    }

    object Bootstrap {
        @Keep
        @JsonClass(generateAdapter = true)
        class BootstrapResponse(data: BootstrapData) : StytchDataResponse<BootstrapData>(data)
    }

    @Keep
    @JsonClass(generateAdapter = true)
    class BasicResponse(data: BasicData) : StytchDataResponse<BasicData>(data)

    @Keep
    @JsonClass(generateAdapter = true)
    class SendResponse(data: BasicData) : StytchDataResponse<BasicData>(data)

    @Keep
    @JsonClass(generateAdapter = true)
    class NoResponse(data: NoResponseData) : StytchDataResponse<NoResponseData>(data)
}

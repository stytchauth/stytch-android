package com.stytch.sdk.b2b.network

import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.StytchDataResponse

internal object B2BResponses {
    object MagicLinks {
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(data: B2BEMLAuthenticateData) : StytchDataResponse<B2BEMLAuthenticateData>(data)
    }

    object Sessions {
        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(data: B2BAuthData) : StytchDataResponse<B2BAuthData>(data)
    }

    object Organizations {
        @JsonClass(generateAdapter = true)
        class GetOrganizationResponse(data: Organization) : StytchDataResponse<Organization>(data)

        @JsonClass(generateAdapter = true)
        class GetMemberResponse(data: MemberResponseData) : StytchDataResponse<MemberResponseData>(data)
    }
}

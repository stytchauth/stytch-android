package com.stytch.sdk.common

import com.stytch.sdk.b2b.network.models.DiscoveryAuthenticateResponseData
import com.stytch.sdk.common.network.models.CommonAuthenticationData

public sealed class DeeplinkResponse(public open val result: StytchResult<Any>) {
    public data class Auth(
        override val result: StytchResult<CommonAuthenticationData>
    ) : DeeplinkResponse(result)
    public data class Discovery(
        override val result: StytchResult<DiscoveryAuthenticateResponseData>
    ) : DeeplinkResponse(result)
}

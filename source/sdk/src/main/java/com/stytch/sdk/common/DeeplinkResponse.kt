package com.stytch.sdk.common

import com.stytch.sdk.b2b.network.models.DiscoveryAuthenticateResponseData
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.network.models.CommonAuthenticationData

/**
 * A class representing the types of Deeplink responses that the Stytch client has handled
 * @property result A [StytchResult] representing either the authenticated or discovery response, or an error.
 */
public sealed class DeeplinkResponse(
    public open val result: StytchResult<Any>,
) {
    /**
     * An authenticated user response
     * @property result A [StytchResult] representing either the authenticated response or an error.
     */
    @JacocoExcludeGenerated
    public data class Auth(
        override val result: StytchResult<CommonAuthenticationData>,
    ) : DeeplinkResponse(result)

    /**
     * A B2B discovery response
     * @property result A [StytchResult] representing either the authenticated intermediate session or an error.
     */
    @JacocoExcludeGenerated
    public data class Discovery(
        override val result: StytchResult<DiscoveryAuthenticateResponseData>,
    ) : DeeplinkResponse(result)
}

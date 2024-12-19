package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.discovery.Discovery
import com.stytch.sdk.b2b.network.models.IntermediateSessionExchangeResponseData
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig

internal class UseDiscoveryIntermediateSessionExchange(
    private val productConfig: StytchB2BProductConfig,
    private val request: PerformRequest<IntermediateSessionExchangeResponseData>,
) {
    suspend operator fun invoke(organizationId: String): Result<IntermediateSessionExchangeResponseData> =
        request {
            StytchB2BClient.discovery.exchangeIntermediateSession(
                Discovery.SessionExchangeParameters(
                    organizationId = organizationId,
                    sessionDurationMinutes = productConfig.sessionOptions.sessionDurationMinutes,
                ),
            )
        }
}

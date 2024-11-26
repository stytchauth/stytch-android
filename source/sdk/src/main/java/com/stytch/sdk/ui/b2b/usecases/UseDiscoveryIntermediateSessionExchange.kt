package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.discovery.Discovery
import com.stytch.sdk.b2b.network.models.IntermediateSessionExchangeResponseData
import com.stytch.sdk.ui.b2b.PerformRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class UseDiscoveryIntermediateSessionExchange(
    private val scope: CoroutineScope,
    private val request: PerformRequest<IntermediateSessionExchangeResponseData>,
) {
    operator fun invoke(organizationId: String) {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.discovery.exchangeIntermediateSession(
                    Discovery.SessionExchangeParameters(
                        organizationId = organizationId,
                    ),
                )
            }
        }
    }
}

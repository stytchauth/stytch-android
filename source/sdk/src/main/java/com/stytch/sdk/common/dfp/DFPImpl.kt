package com.stytch.sdk.common.dfp

import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class DFPImpl(
    private val dfpProvider: DFPProvider,
    private val dispatchers: StytchDispatchers,
    private val externalScope: CoroutineScope,
) : DFP {
    override suspend fun getTelemetryId(): String =
        withContext(dispatchers.io) {
            dfpProvider.getTelemetryId()
        }

    override fun getTelemetryId(callback: (String) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(getTelemetryId())
        }
    }
}

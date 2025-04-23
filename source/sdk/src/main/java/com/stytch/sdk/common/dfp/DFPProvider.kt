package com.stytch.sdk.common.dfp

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.stytch.dfp.DFP as StytchDFP

internal interface DFPProvider {
    suspend fun getTelemetryId(): String
}

internal class DFPProviderImpl(
    scope: CoroutineScope,
    context: Context,
    publicToken: String,
    dfppaDomain: String,
) : DFPProvider {
    private var dfp: StytchDFP? = null

    init {
        scope.launch(Dispatchers.IO) {
            dfp =
                try {
                    StytchDFP(context = context, publicToken = publicToken, submissionUrl = dfppaDomain)
                } catch (_: UnsatisfiedLinkError) {
                    null
                } catch (_: NoClassDefFoundError) {
                    null
                }
        }
    }

    override suspend fun getTelemetryId(): String =
        suspendCancellableCoroutine { continuation ->
            dfp?.getTelemetryId { telemetryId ->
                continuation.resume(telemetryId)
            } ?: run {
                continuation.resume("")
            }
        }
}

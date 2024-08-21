package com.stytch.sdk.common.dfp

import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.stytch.dfp.DFP as NativeDFP

internal interface DFPProvider {
    suspend fun getTelemetryId(): String
}

internal class DFPProviderImpl(
    context: Context,
    publicToken: String,
    dfppaDomain: String,
) : DFPProvider {
    // We have to do this to allow unit tests to work, as they don't load native libs :( Alternatively, we'd need to
    // rewrite all of our affected tests to be instrumented tests, which isn't ideal
    private val dfp: NativeDFP? =
        try {
            NativeDFP(context = context, publicToken = publicToken, submissionUrl = dfppaDomain)
        } catch (_: UnsatisfiedLinkError) {
            null
        } catch (_: NoClassDefFoundError) {
            null
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

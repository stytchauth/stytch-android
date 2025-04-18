package com.stytch.sdk.common.network

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.common.dfp.DFPConfiguration
import com.stytch.sdk.common.extensions.isDFPPAEnabled
import com.stytch.sdk.common.extensions.requiresCaptcha
import com.stytch.sdk.common.extensions.toNewRequestWithParams
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

internal const val DFP_TELEMETRY_ID_KEY = "dfp_telemetry_id"
internal const val CAPTCHA_TOKEN_KEY = "captcha_token"

internal interface DFPInterceptor : Interceptor {
    fun handleDisabledDFPStatus(chain: Interceptor.Chain): Response

    fun handleDFPDecisioningMode(chain: Interceptor.Chain): Response

    fun handleDFPObservationMode(chain: Interceptor.Chain): Response
}

internal class StytchDFPInterceptor(
    internal var dfpConfiguration: DFPConfiguration,
) : DFPInterceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!request.isDFPPAEnabled()) return chain.proceed(request)
        if (!dfpConfiguration.dfpProtectedAuthEnabled) return handleDisabledDFPStatus(chain)
        return when (dfpConfiguration.dfpProtectedAuthMode) {
            DFPProtectedAuthMode.DECISIONING -> handleDFPDecisioningMode(chain)
            DFPProtectedAuthMode.OBSERVATION -> handleDFPObservationMode(chain)
        }
    }

    @VisibleForTesting
    override fun handleDisabledDFPStatus(chain: Interceptor.Chain): Response {
        // DISABLED = if captcha client is configured, add a captcha token, else do nothing
        val originalRequest = chain.request()
        val newRequest =
            if (dfpConfiguration.captchaProvider.captchaIsConfigured) {
                originalRequest.toNewRequestWithParams(
                    mapOf(
                        CAPTCHA_TOKEN_KEY to runBlocking { dfpConfiguration.captchaProvider.executeRecaptcha() },
                    ),
                )
            } else {
                originalRequest
            }
        return chain.proceed(newRequest)
    }

    @VisibleForTesting
    override fun handleDFPDecisioningMode(chain: Interceptor.Chain): Response {
        // DECISIONING = add DFP Id, proceed; if request 403s, add a captcha token
        val request = chain.request()
        val response =
            chain.proceed(
                request.toNewRequestWithParams(
                    mapOf(
                        DFP_TELEMETRY_ID_KEY to runBlocking { dfpConfiguration.dfpProvider.getTelemetryId() },
                    ),
                ),
            )
        return if (response.requiresCaptcha()) {
            response.close()
            chain.proceed(
                request.toNewRequestWithParams(
                    mapOf(
                        DFP_TELEMETRY_ID_KEY to runBlocking { dfpConfiguration.dfpProvider.getTelemetryId() },
                        CAPTCHA_TOKEN_KEY to runBlocking { dfpConfiguration.captchaProvider.executeRecaptcha() },
                    ),
                ),
            )
        } else {
            response
        }
    }

    @VisibleForTesting
    override fun handleDFPObservationMode(chain: Interceptor.Chain): Response {
        // OBSERVATION = Always DFP; CAPTCHA if configured
        val request = chain.request()
        val newParams =
            mutableMapOf(
                DFP_TELEMETRY_ID_KEY to runBlocking { dfpConfiguration.dfpProvider.getTelemetryId() },
            )
        if (dfpConfiguration.captchaProvider.captchaIsConfigured) {
            newParams[CAPTCHA_TOKEN_KEY] = runBlocking { dfpConfiguration.captchaProvider.executeRecaptcha() }
        }
        return chain.proceed(request.toNewRequestWithParams(newParams))
    }
}

package com.stytch.sdk.common.network

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.common.dfp.CaptchaProvider
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.extensions.requiresCaptcha
import com.stytch.sdk.common.extensions.toNewRequestWithParams
import com.stytch.sdk.common.network.models.DFPProtectedAuthEnabled
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

internal const val DFP_TELEMETRY_ID_KEY = "dfp_telemetry_id"
internal const val CAPTCHA_TOKEN_KEY = "captcha_token"

internal interface DFPInterceptor : Interceptor {
    fun handleDisabledDFPStatus(chain: Interceptor.Chain): Response

    fun handleEnabledDFPStatus(chain: Interceptor.Chain): Response

    fun handlePassiveDFPStatus(chain: Interceptor.Chain): Response
}

internal class StytchDFPInterceptor(
    private val dfpProvider: DFPProvider,
    private val captchaProvider: CaptchaProvider,
    private val dfpProtectedAuthEnabled: DFPProtectedAuthEnabled,
) : DFPInterceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method == "GET" || request.method == "DELETE") return chain.proceed(request)
        return when (dfpProtectedAuthEnabled) {
            DFPProtectedAuthEnabled.DISABLED -> handleDisabledDFPStatus(chain)
            DFPProtectedAuthEnabled.ENABLED -> handleEnabledDFPStatus(chain)
            DFPProtectedAuthEnabled.PASSIVE -> handlePassiveDFPStatus(chain)
        }
    }

    @VisibleForTesting
    override fun handleDisabledDFPStatus(chain: Interceptor.Chain): Response {
        // DISABLED = if captcha client is configured, add a captcha token, else do nothing
        val originalRequest = chain.request()
        val newRequest = if (captchaProvider.captchaIsConfigured) {
            originalRequest.toNewRequestWithParams(
                mapOf(
                    CAPTCHA_TOKEN_KEY to runBlocking { captchaProvider.executeRecaptcha() }
                )
            )
        } else {
            originalRequest
        }
        return chain.proceed(newRequest)
    }

    @VisibleForTesting
    override fun handleEnabledDFPStatus(chain: Interceptor.Chain): Response {
        // ENABLED = add DFP Id, proceed; if request 403s, add a captcha token
        val request = chain.request()
        val response = chain.proceed(
            request.toNewRequestWithParams(
                mapOf(
                    DFP_TELEMETRY_ID_KEY to runBlocking { dfpProvider.getTelemetryId() }
                )
            )
        )
        return if (response.requiresCaptcha()) {
            response.close()
            chain.proceed(
                request.toNewRequestWithParams(
                    mapOf(
                        DFP_TELEMETRY_ID_KEY to runBlocking { dfpProvider.getTelemetryId() },
                        CAPTCHA_TOKEN_KEY to runBlocking { captchaProvider.executeRecaptcha() }
                    )
                )
            )
        } else {
            response
        }
    }

    @VisibleForTesting
    override fun handlePassiveDFPStatus(chain: Interceptor.Chain): Response {
        // PASSIVE = Always DFP; CAPTCHA if configured
        val request = chain.request()
        val newParams = mutableMapOf(
            DFP_TELEMETRY_ID_KEY to runBlocking { dfpProvider.getTelemetryId() }
        )
        if (captchaProvider.captchaIsConfigured) {
            newParams[CAPTCHA_TOKEN_KEY] = runBlocking { captchaProvider.executeRecaptcha() }
        }
        return chain.proceed(request.toNewRequestWithParams(newParams))
    }
}

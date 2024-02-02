package com.stytch.sdk.common.network

import com.stytch.sdk.common.dfp.CaptchaProvider
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.extensions.toNewRequestWithParams
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Before
import org.junit.Test

internal class StytchDFPInterceptorTest {
    @MockK
    private lateinit var dfpProvider: DFPProvider

    @MockK
    private lateinit var captchaProvider: CaptchaProvider

    @MockK
    private lateinit var dfpProtectedAuthMode: DFPProtectedAuthMode

    private lateinit var interceptor: DFPInterceptor

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic("com.stytch.sdk.common.extensions.RequestExtKt")
        interceptor = spyk(
            StytchDFPInterceptor(dfpProvider, captchaProvider, true, dfpProtectedAuthMode),
            recordPrivateCalls = true
        )
    }

    @Test
    fun `get requests do not inject DFP`() {
        val request: Request = mockk {
            every { method } returns "GET"
            every { url } returns mockk {
                every { toUrl() } returns mockk(relaxed = true)
            }
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns mockk()
        }
        interceptor.intercept(chain)
        verify(exactly = 0) { request.toNewRequestWithParams(any()) }
    }

    @Test
    fun `delete requests do not inject DFP`() {
        val request: Request = mockk {
            every { method } returns "DELETE"
            every { url } returns mockk {
                every { toUrl() } returns mockk(relaxed = true)
            }
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns mockk()
        }
        interceptor.intercept(chain)
        verify(exactly = 0) { request.toNewRequestWithParams(any()) }
    }

    @Test
    fun `event logs do not inject DFP or CAPTCHA`() {
        val request: Request = mockk {
            every { method } returns "POST"
            every { url } returns mockk {
                every { toUrl() } returns mockk {
                    every { path } returns "/events"
                }
            }
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns mockk()
        }
        interceptor.intercept(chain)
        verify(exactly = 0) { request.toNewRequestWithParams(any()) }
    }

    @Test
    fun `intercept calls the appropriate method based on the DFPProtectedAuth type`() {
        val request: Request = mockk {
            every { method } returns "POST"
            every { url } returns mockk {
                every { toUrl() } returns mockk(relaxed = true)
            }
            every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
        }
        // DISABLED (need to reset the spy)
        interceptor = spyk(
            StytchDFPInterceptor(dfpProvider, captchaProvider, false, dfpProtectedAuthMode),
            recordPrivateCalls = true
        )
        every { interceptor.handleDisabledDFPStatus(any()) } returns mockk()
        interceptor.intercept(chain)
        verify { interceptor.handleDisabledDFPStatus(any()) }

        // ENABLED + DECISIONING (need to reset the spy)
        interceptor = spyk(
            StytchDFPInterceptor(dfpProvider, captchaProvider, true, dfpProtectedAuthMode),
            recordPrivateCalls = true
        )
        every { interceptor.handleDFPDecisioningMode(any()) } returns mockk()
        every { dfpProtectedAuthMode.name } returns DFPProtectedAuthMode.DECISIONING.name
        every { dfpProtectedAuthMode.ordinal } returns DFPProtectedAuthMode.DECISIONING.ordinal
        interceptor.intercept(chain)
        verify { interceptor.handleDFPDecisioningMode(any()) }

        // ENABLED + OBSERVATION
        every { interceptor.handleDFPObservationMode(any()) } returns mockk()
        every { dfpProtectedAuthMode.name } returns DFPProtectedAuthMode.OBSERVATION.name
        every { dfpProtectedAuthMode.ordinal } returns DFPProtectedAuthMode.OBSERVATION.ordinal
        interceptor.intercept(chain)
        verify { interceptor.handleDFPObservationMode(any()) }
    }

    @Test
    fun `handleDisabledDFPStatus adds captcha token if captcha is configured`() {
        val newParamsSlot = slot<Map<String, String>>()
        val request: Request = mockk {
            every { method } returns "POST"
            every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
            every { toNewRequestWithParams(capture(newParamsSlot)) } returns this
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns mockk {
                every { code } returns 200
            }
        }
        val expectedNewParams = mapOf(
            CAPTCHA_TOKEN_KEY to "captcha-token"
        )
        coEvery { captchaProvider.executeRecaptcha() } returns "captcha-token"
        every { captchaProvider.captchaIsConfigured } returns true
        interceptor.handleDisabledDFPStatus(chain)
        assert(newParamsSlot.captured == expectedNewParams)
    }

    @Test
    fun `handleDisabledDFPStatus does not modify the request if captcha is not configured`() {
        val request: Request = mockk {
            every { method } returns "POST"
            every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns mockk {
                every { code } returns 200
            }
        }
        coEvery { captchaProvider.executeRecaptcha() } returns "captcha-token"
        every { captchaProvider.captchaIsConfigured } returns false
        interceptor.handleDisabledDFPStatus(chain)
        verify(exactly = 0) { request.toNewRequestWithParams(any()) }
    }

    @Test
    fun `handleEnabledDFPStatus adds a dfp telemetry ID, and does not add a captcha token if not required`() {
        val request: Request = mockk {
            every { method } returns "POST"
            every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
            every { toNewRequestWithParams(any()) } returns this
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns mockk {
                every { code } returns 200
            }
        }
        coEvery { dfpProvider.getTelemetryId() } returns "dfp-telemetry-id"
        interceptor.handleDFPDecisioningMode(chain)
        verify(exactly = 1) { request.toNewRequestWithParams(any()) }
    }

    @Test
    fun `handleEnabledDFPStatus adds a dfp telemetry ID, and adds a captcha token if required`() {
        val request: Request = mockk {
            every { method } returns "POST"
            every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
            every { toNewRequestWithParams(any()) } returns this
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns mockk {
                every { code } returns 403
                every { close() } just runs
            }
        }
        coEvery { dfpProvider.getTelemetryId() } returns "dfp-telemetry-id"
        coEvery { captchaProvider.executeRecaptcha() } returns "captcha-token"
        interceptor.handleDFPDecisioningMode(chain)
        verify(exactly = 1) { request.toNewRequestWithParams(mapOf(DFP_TELEMETRY_ID_KEY to "dfp-telemetry-id")) }
        verify(exactly = 1) {
            request.toNewRequestWithParams(
                mapOf(
                    DFP_TELEMETRY_ID_KEY to "dfp-telemetry-id",
                    CAPTCHA_TOKEN_KEY to "captcha-token",
                )
            )
        }
    }

    @Test
    fun `handlePassiveDFPStatus adds a dfp telemetry ID, and adds a captcha token if captcha is configured`() {
        val request: Request = mockk {
            every { method } returns "POST"
            every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
            every { toNewRequestWithParams(any()) } returns this
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns mockk {
                every { code } returns 403
                every { close() } just runs
            }
        }
        coEvery { dfpProvider.getTelemetryId() } returns "dfp-telemetry-id"
        every { captchaProvider.captchaIsConfigured } returns true
        coEvery { captchaProvider.executeRecaptcha() } returns "captcha-token"
        interceptor.handleDFPObservationMode(chain)
        verify(exactly = 1) {
            request.toNewRequestWithParams(
                mapOf(
                    DFP_TELEMETRY_ID_KEY to "dfp-telemetry-id",
                    CAPTCHA_TOKEN_KEY to "captcha-token",
                )
            )
        }
    }

    @Test
    fun `handlePassiveDFPStatus adds a dfp telemetry ID, but not add a captcha token if captcha is not configured`() {
        val request: Request = mockk {
            every { method } returns "POST"
            every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
            every { toNewRequestWithParams(any()) } returns this
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns mockk {
                every { code } returns 403
                every { close() } just runs
            }
        }
        coEvery { dfpProvider.getTelemetryId() } returns "dfp-telemetry-id"
        every { captchaProvider.captchaIsConfigured } returns false
        interceptor.handleDFPObservationMode(chain)
        verify(exactly = 1) {
            request.toNewRequestWithParams(
                mapOf(
                    DFP_TELEMETRY_ID_KEY to "dfp-telemetry-id",
                )
            )
        }
    }
}

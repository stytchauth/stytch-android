package com.stytch.sdk.common.network

import com.stytch.sdk.common.dfp.CaptchaProvider
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.extensions.toNewRequestWithParams
import com.stytch.sdk.common.network.models.DFPProtectedAuthEnabled
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
    private lateinit var dfpProtectedAuthEnabled: DFPProtectedAuthEnabled

    private lateinit var interceptor: DFPInterceptor

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic("com.stytch.sdk.common.extensions.RequestExtKt")
        interceptor = spyk(
            StytchDFPInterceptor(dfpProvider, captchaProvider, dfpProtectedAuthEnabled),
            recordPrivateCalls = true
        )
    }

    @Test
    fun `get requests do not inject DFP`() {
        val request: Request = mockk {
            every { method } returns "GET"
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
            every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
        }
        // DISABLED
        every { interceptor.handleDisabledDFPStatus(any()) } returns mockk()
        every { dfpProtectedAuthEnabled.name } returns DFPProtectedAuthEnabled.DISABLED.name
        every { dfpProtectedAuthEnabled.ordinal } returns DFPProtectedAuthEnabled.DISABLED.ordinal
        interceptor.intercept(chain)
        verify { interceptor.handleDisabledDFPStatus(any()) }
        // ENABLED
        every { interceptor.handleEnabledDFPStatus(any()) } returns mockk()
        every { dfpProtectedAuthEnabled.name } returns DFPProtectedAuthEnabled.ENABLED.name
        every { dfpProtectedAuthEnabled.ordinal } returns DFPProtectedAuthEnabled.ENABLED.ordinal
        interceptor.intercept(chain)
        verify { interceptor.handleEnabledDFPStatus(any()) }
        // PASSIVE
        every { interceptor.handlePassiveDFPStatus(any()) } returns mockk()
        every { dfpProtectedAuthEnabled.name } returns DFPProtectedAuthEnabled.PASSIVE.name
        every { dfpProtectedAuthEnabled.ordinal } returns DFPProtectedAuthEnabled.PASSIVE.ordinal
        interceptor.intercept(chain)
        verify { interceptor.handlePassiveDFPStatus(any()) }
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
        interceptor.handleEnabledDFPStatus(chain)
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
        interceptor.handleEnabledDFPStatus(chain)
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
        interceptor.handlePassiveDFPStatus(chain)
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
        interceptor.handlePassiveDFPStatus(chain)
        verify(exactly = 1) {
            request.toNewRequestWithParams(
                mapOf(
                    DFP_TELEMETRY_ID_KEY to "dfp-telemetry-id",
                )
            )
        }
    }
}

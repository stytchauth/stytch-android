package com.stytch.sdk.common.network

import com.stytch.sdk.common.dfp.CaptchaProvider
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.extensions.toNewRequest
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
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

    private lateinit var interceptor: StytchDFPInterceptor

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic("com.stytch.sdk.common.extensions.RequestExtKt")
        interceptor = StytchDFPInterceptor(dfpProvider, captchaProvider)
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
        verify(exactly = 0) { request.toNewRequest(any()) }
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
        verify(exactly = 0) { request.toNewRequest(any()) }
    }

    @Test
    fun `dfp requests inject DFP, but not captcha, if not required`() {
        val request: Request = mockk {
            every { method } returns "POST"
            every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
            every { toNewRequest(any()) } returns this
        }
        val chain: Interceptor.Chain = mockk {
            every { request() } returns request
            every { proceed(any()) } returns mockk {
                every { code } returns 200
            }
        }
        coEvery { dfpProvider.getTelemetryId() } returns "dfp-telemetry-id"
        interceptor.intercept(chain)
        verify(exactly = 1) { request.toNewRequest(any()) }
    }

    @Test
    fun `dfp requests inject DFP and captcha, if required`() {
        val request: Request = mockk {
            every { method } returns "POST"
            every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
            every { toNewRequest(any()) } returns this
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
        interceptor.intercept(chain)
        verify(exactly = 1) { request.toNewRequest(mapOf(DFP_TELEMETRY_ID_KEY to "dfp-telemetry-id")) }
        verify(exactly = 1) {
            request.toNewRequest(
                mapOf(
                    DFP_TELEMETRY_ID_KEY to "dfp-telemetry-id",
                    CAPTCHA_TOKEN_KEY to "captcha-token",
                )
            )
        }
    }
}

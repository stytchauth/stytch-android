package com.stytch.sdk.common.network

import com.stytch.sdk.common.annotations.DFPPAEnabled
import com.stytch.sdk.common.dfp.CaptchaProvider
import com.stytch.sdk.common.dfp.DFPConfiguration
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.extensions.toNewRequestWithParams
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode
import com.stytch.sdk.consumer.network.StytchApiService
import io.mockk.MockKAnnotations
import io.mockk.MockKSettings.recordPrivateCalls
import io.mockk.coEvery
import io.mockk.coVerify
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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Invocation
import java.lang.reflect.Method

internal class StytchDFPInterceptorTest {
    @get:Rule
    val server: MockWebServer = MockWebServer()

    @MockK
    private lateinit var dfpProvider: DFPProvider

    @MockK
    private lateinit var captchaProvider: CaptchaProvider

    @MockK
    private lateinit var dfpProtectedAuthMode: DFPProtectedAuthMode

    private lateinit var interceptor: StytchDFPInterceptor

    private lateinit var client: OkHttpClient

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic("com.stytch.sdk.common.extensions.RequestExtKt")
        interceptor =
            spyk(
                StytchDFPInterceptor(
                    DFPConfiguration(dfpProvider, captchaProvider, true, DFPProtectedAuthMode.OBSERVATION),
                ),
                recordPrivateCalls = true,
            )
        client = OkHttpClient().newBuilder().addInterceptor(interceptor).build()
        server.enqueue(MockResponse())
    }

    private fun firstMethodWithDFPPAEnabledAnnotation(): Method =
        StytchApiService::class.java.methods
            .toList()
            .first { it.getAnnotation(DFPPAEnabled::class.java) != null }

    private fun firstMethodWithoutDFPPAEnabledAnnotation(): Method =
        StytchApiService::class.java.methods
            .toList()
            .first { it.getAnnotation(DFPPAEnabled::class.java) == null }

    @Test
    fun `A DFPPAEnabled annotated method should invoke the DFP logic`() {
        // Given
        coEvery { dfpProvider.getTelemetryId() } returns "mock-telemetry-id"
        coEvery { captchaProvider.captchaIsConfigured } returns true
        coEvery { captchaProvider.executeRecaptcha() } returns "mock-captcha-token"
        val request =
            Request
                .Builder()
                .url(server.url("/"))
                .post(mockk(relaxed = true))
                .tag(
                    Invocation::class.java,
                    Invocation.of(
                        DFPPAEnabled::class.java,
                        DFPPAEnabled(),
                        firstMethodWithDFPPAEnabledAnnotation(),
                        mutableListOf("args"),
                    ),
                ).build()

        // When
        client.newCall(request).execute()

        // Then
        coVerify { dfpProvider.getTelemetryId() }
        coVerify { captchaProvider.executeRecaptcha() }
    }

    @Test
    fun `A non-DFPPAEnabled annotated method should invoke the DFP logic`() {
        // Given
        coEvery { dfpProvider.getTelemetryId() } returns "mock-telemetry-id"
        coEvery { captchaProvider.captchaIsConfigured } returns true
        coEvery { captchaProvider.executeRecaptcha() } returns "mock-captcha-token"
        val request =
            Request
                .Builder()
                .url(server.url("/"))
                .post(mockk(relaxed = true))
                .tag(
                    Invocation::class.java,
                    Invocation.of(
                        DFPPAEnabled::class.java,
                        DFPPAEnabled(),
                        firstMethodWithoutDFPPAEnabledAnnotation(),
                        mutableListOf("args"),
                    ),
                ).build()

        // When
        client.newCall(request).execute()

        // Then
        coVerify(exactly = 0) { dfpProvider.getTelemetryId() }
        coVerify(exactly = 0) { captchaProvider.executeRecaptcha() }
    }

    @Test
    fun `intercept calls the appropriate method based on the DFPProtectedAuth type`() {
        val request: Request =
            mockk {
                every { method } returns "POST"
                every { tag(Invocation::class.java) } returns
                    mockk {
                        every { method() } returns
                            mockk {
                                every { getAnnotation(DFPPAEnabled::class.java) } returns DFPPAEnabled()
                            }
                    }
                every { url } returns
                    mockk {
                        every { toUrl() } returns mockk(relaxed = true)
                    }
                every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
            }
        val chain: Interceptor.Chain =
            mockk {
                every { request() } returns request
            }
        // DISABLED (need to reset the spy)
        every { interceptor.handleDisabledDFPStatus(any()) } returns mockk()
        interceptor.dfpConfiguration = DFPConfiguration(dfpProvider, captchaProvider, false, dfpProtectedAuthMode)
        interceptor.intercept(chain)
        verify { interceptor.handleDisabledDFPStatus(any()) }

        // ENABLED + DECISIONING (need to reset the spy)
        every { interceptor.handleDFPDecisioningMode(any()) } returns mockk()
        every { dfpProtectedAuthMode.name } returns DFPProtectedAuthMode.DECISIONING.name
        every { dfpProtectedAuthMode.ordinal } returns DFPProtectedAuthMode.DECISIONING.ordinal
        interceptor.dfpConfiguration = DFPConfiguration(dfpProvider, captchaProvider, true, dfpProtectedAuthMode)
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
        val request: Request =
            mockk {
                every { method } returns "POST"
                every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
                every { toNewRequestWithParams(capture(newParamsSlot)) } returns this
            }
        val chain: Interceptor.Chain =
            mockk {
                every { request() } returns request
                every { proceed(any()) } returns
                    mockk {
                        every { code } returns 200
                    }
            }
        val expectedNewParams =
            mapOf(
                CAPTCHA_TOKEN_KEY to "captcha-token",
            )
        coEvery { captchaProvider.executeRecaptcha() } returns "captcha-token"
        every { captchaProvider.captchaIsConfigured } returns true
        interceptor.handleDisabledDFPStatus(chain)
        assert(newParamsSlot.captured == expectedNewParams)
    }

    @Test
    fun `handleDisabledDFPStatus does not modify the request if captcha is not configured`() {
        val request: Request =
            mockk {
                every { method } returns "POST"
                every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
            }
        val chain: Interceptor.Chain =
            mockk {
                every { request() } returns request
                every { proceed(any()) } returns
                    mockk {
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
        val request: Request =
            mockk {
                every { method } returns "POST"
                every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
                every { toNewRequestWithParams(any()) } returns this
            }
        val chain: Interceptor.Chain =
            mockk {
                every { request() } returns request
                every { proceed(any()) } returns
                    mockk {
                        every { code } returns 200
                    }
            }
        coEvery { dfpProvider.getTelemetryId() } returns "dfp-telemetry-id"
        interceptor.handleDFPDecisioningMode(chain)
        verify(exactly = 1) { request.toNewRequestWithParams(any()) }
    }

    @Test
    fun `handleEnabledDFPStatus adds a dfp telemetry ID, and adds a captcha token if required`() {
        val request: Request =
            mockk {
                every { method } returns "POST"
                every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
                every { toNewRequestWithParams(any()) } returns this
            }
        val chain: Interceptor.Chain =
            mockk {
                every { request() } returns request
                every { proceed(any()) } returns
                    mockk {
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
                ),
            )
        }
    }

    @Test
    fun `handlePassiveDFPStatus adds a dfp telemetry ID, and adds a captcha token if captcha is configured`() {
        val request: Request =
            mockk {
                every { method } returns "POST"
                every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
                every { toNewRequestWithParams(any()) } returns this
            }
        val chain: Interceptor.Chain =
            mockk {
                every { request() } returns request
                every { proceed(any()) } returns
                    mockk {
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
                ),
            )
        }
    }

    @Test
    fun `handlePassiveDFPStatus adds a dfp telemetry ID, but not add a captcha token if captcha is not configured`() {
        val request: Request =
            mockk {
                every { method } returns "POST"
                every { body } returns "".toRequestBody("application/json".toMediaTypeOrNull())
                every { toNewRequestWithParams(any()) } returns this
            }
        val chain: Interceptor.Chain =
            mockk {
                every { request() } returns request
                every { proceed(any()) } returns
                    mockk {
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
                ),
            )
        }
    }
}

package com.stytch.sdk.consumer.totp

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.consumer.TOTPAuthenticateResponse
import com.stytch.sdk.consumer.TOTPCreateResponse
import com.stytch.sdk.consumer.TOTPRecoverResponse
import com.stytch.sdk.consumer.TOTPRecoveryCodesResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.models.TOTPAuthenticateResponseData
import com.stytch.sdk.consumer.network.models.TOTPRecoverResponseData
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class TOTPImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.TOTP

    @MockK
    private lateinit var mockSessionStorage: ConsumerSessionStorage

    private lateinit var impl: TOTP
    private val dispatcher = Dispatchers.Unconfined
    private val successfulAuthResponse = StytchResult.Success<TOTPAuthenticateResponseData>(mockk(relaxed = true))
    private val successfulRecoverResponse = StytchResult.Success<TOTPRecoverResponseData>(mockk(relaxed = true))

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.consumer.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        every { mockSessionStorage.lastAuthMethodUsed = any() } just runs
        impl =
            TOTPImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockSessionStorage,
                api = mockApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `TOTP Create delegates to appropriate api method`() =
        runBlocking {
            coEvery { mockApi.create(any()) } returns mockk(relaxed = true)
            impl.create(mockk(relaxed = true))
            coVerify(exactly = 1) { mockApi.create(any()) }
        }

    @Test
    fun `TOTP Create with callback calls callback`() {
        coEvery { mockApi.create(any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(TOTPCreateResponse) -> Unit>()
        impl.create(mockk(relaxed = true), mockCallback)
        coVerify(exactly = 1) { mockApi.create(any()) }
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `TOTP Authenticate delegates to appropriate api method`() =
        runBlocking {
            coEvery { mockApi.authenticate(any(), any()) } returns successfulAuthResponse
            impl.authenticate(mockk(relaxed = true))
            coVerify(exactly = 1) { mockApi.authenticate(any(), any()) }
            verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `TOTP Authenticate with callback calls callback`() {
        coEvery { mockApi.authenticate(any(), any()) } returns successfulAuthResponse
        val mockCallback = spyk<(TOTPAuthenticateResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        coVerify(exactly = 1) { mockApi.authenticate(any(), any()) }
        verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `TOTP RecoveryCodes delegates to appropriate api method`() =
        runBlocking {
            coEvery { mockApi.recoveryCodes() } returns mockk(relaxed = true)
            impl.recoveryCodes()
            coVerify(exactly = 1) { mockApi.recoveryCodes() }
        }

    @Test
    fun `TOTP RecoveryCodes with callback calls callback`() {
        coEvery { mockApi.recoveryCodes() } returns mockk(relaxed = true)
        val mockCallback = spyk<(TOTPRecoveryCodesResponse) -> Unit>()
        impl.recoveryCodes(mockCallback)
        coVerify(exactly = 1) { mockApi.recoveryCodes() }
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `TOTP Recover delegates to appropriate api method`() =
        runBlocking {
            coEvery { mockApi.recover(any(), any()) } returns successfulRecoverResponse
            impl.recover(mockk(relaxed = true))
            coVerify(exactly = 1) { mockApi.recover(any(), any()) }
            verify { successfulRecoverResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `TOTP Recover with callback calls callback`() {
        coEvery { mockApi.recover(any(), any()) } returns successfulRecoverResponse
        val mockCallback = spyk<(TOTPRecoverResponse) -> Unit>()
        impl.recover(mockk(relaxed = true), mockCallback)
        coVerify(exactly = 1) { mockApi.recover(any(), any()) }
        verify { successfulRecoverResponse.launchSessionUpdater(any(), any()) }
        verify { mockCallback.invoke(any()) }
    }
}

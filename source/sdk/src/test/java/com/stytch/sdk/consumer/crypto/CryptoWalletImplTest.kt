package com.stytch.sdk.consumer.crypto

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.CryptoWalletAuthenticateStartResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.models.AuthData
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
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class CryptoWalletImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.Crypto

    @MockK
    private lateinit var mockSessionStorage: ConsumerSessionStorage

    private lateinit var impl: CryptoWalletImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulAuthResponse = StytchResult.Success<AuthData>(mockk(relaxed = true))

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.consumer.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        impl =
            CryptoWalletImpl(
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
    fun `CryptoWallet authenticateStart with no active session delegates to appropriate api method`() =
        runTest {
            every { mockSessionStorage.persistedSessionIdentifiersExist } returns false
            coEvery { mockApi.authenticateStartPrimary(any(), any()) } returns mockk(relaxed = true)
            impl.authenticateStart(mockk(relaxed = true))
            coVerify(exactly = 1) { mockApi.authenticateStartPrimary(any(), any()) }
        }

    @Test
    fun `CryptoWallet authenticateStart with an active session delegates to appropriate api method`() =
        runTest {
            every { mockSessionStorage.persistedSessionIdentifiersExist } returns true
            coEvery { mockApi.authenticateStartSecondary(any(), any()) } returns mockk(relaxed = true)
            impl.authenticateStart(mockk(relaxed = true))
            coVerify(exactly = 1) { mockApi.authenticateStartSecondary(any(), any()) }
        }

    @Test
    fun `CryptoWallet authenticateStart with callback calls callback`() {
        every { mockSessionStorage.persistedSessionIdentifiersExist } returns true
        coEvery { mockApi.authenticateStartSecondary(any(), any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(CryptoWalletAuthenticateStartResponse) -> Unit>()
        impl.authenticateStart(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `CryptoWallet authenticate delegates to appropriate api method`() =
        runTest {
            coEvery { mockApi.authenticate(any(), any(), any(), any()) } returns successfulAuthResponse
            impl.authenticate(mockk(relaxed = true))
            coVerify(exactly = 1) { mockApi.authenticate(any(), any(), any(), any()) }
            verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `CryptoWallet authenticate with callback calls callback`() {
        coEvery { mockApi.authenticate(any(), any(), any(), any()) } returns successfulAuthResponse
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
        verify { mockCallback.invoke(any()) }
    }
}

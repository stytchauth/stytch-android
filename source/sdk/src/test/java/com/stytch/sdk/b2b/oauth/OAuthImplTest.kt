package com.stytch.sdk.b2b.oauth

import com.stytch.sdk.b2b.OAuthAuthenticateResponse
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.sessions.SessionAutoUpdater
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
import java.security.KeyStore
import java.util.Date

internal class OAuthImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.OAuth

    @MockK
    private lateinit var mockSessionStorage: B2BSessionStorage

    @MockK
    private lateinit var mockPKCEPairManager: PKCEPairManager

    private lateinit var impl: OAuthImpl
    private val dispatcher = Dispatchers.Unconfined

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkStatic("com.stytch.sdk.common.extensions.StringExtKt", "com.stytch.sdk.common.extensions.ByteArrayExtKt")
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        every { mockPKCEPairManager.clearPKCECodePair() } just runs
        mockkObject(StytchB2BApi)
        every { StytchB2BApi.isInitialized } returns true
        every { mockSessionStorage.intermediateSessionToken } returns null
        every { mockSessionStorage.lastValidatedAt } returns Date(0)
        StytchB2BClient.deviceInfo = mockk(relaxed = true)
        StytchB2BClient.appSessionId = "app-session-id"
        impl =
            OAuthImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockSessionStorage,
                pkcePairManager = mockPKCEPairManager,
                api = mockApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `authenticate returns correct error if PKCE is missing`() =
        runBlocking {
            every { mockPKCEPairManager.getPKCECodePair() } returns null
            val result = impl.authenticate(mockk(relaxed = true))
            require(result is StytchResult.Error)
            assert(result.exception is StytchMissingPKCEError)
        }

    @Test
    fun `authenticate returns correct error if api call fails`() =
        runBlocking {
            every { mockPKCEPairManager.getPKCECodePair() } returns PKCECodePair("code-challenge", "code-verifier")
            coEvery { mockApi.authenticate(any(), any(), any(), any(), any()) } returns
                StytchResult.Error(
                    StytchAPIError(errorType = "something_went_wrong", message = "testing", statusCode = 400),
                )
            val result = impl.authenticate(mockk(relaxed = true))
            require(result is StytchResult.Error)
            assert(result.exception is StytchAPIError)
            coVerify { mockApi.authenticate(any(), any(), any(), "code-verifier", any()) }
            verify(exactly = 1) { mockPKCEPairManager.clearPKCECodePair() }
        }

    @Test
    fun `authenticate returns success if api call succeeds`() =
        runBlocking {
            every { mockPKCEPairManager.getPKCECodePair() } returns PKCECodePair("code-challenge", "code-verifier")
            coEvery { mockApi.authenticate(any(), any(), any(), any(), any()) } returns
                StytchResult.Success(
                    mockk(relaxed = true),
                )
            val result = impl.authenticate(mockk(relaxed = true))
            require(result is StytchResult.Success)
            coVerify { mockApi.authenticate(any(), any(), any(), "code-verifier", any()) }
            verify(exactly = 1) { mockPKCEPairManager.clearPKCECodePair() }
        }

    @Test
    fun `authenticate with callback calls callback method`() {
        every { mockPKCEPairManager.getPKCECodePair() } returns null
        val spy = spyk<(OAuthAuthenticateResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), spy)
        verify { spy.invoke(any()) }
    }
}

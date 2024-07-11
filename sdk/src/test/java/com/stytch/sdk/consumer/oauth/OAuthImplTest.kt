package com.stytch.sdk.consumer.oauth

import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.consumer.OAuthAuthenticatedResponse
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.StytchApi
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
import java.security.KeyStore

internal class OAuthImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.OAuth

    @MockK
    private lateinit var mockSessionStorage: ConsumerSessionStorage

    @MockK
    private lateinit var mockStorageHelper: StorageHelper

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
        every { mockStorageHelper.loadValue(any()) } returns ""
        every { mockStorageHelper.saveValue(any(), any()) } just runs
        every { mockStorageHelper.clearPKCE() } just runs
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns true
        StytchClient.deviceInfo = mockk(relaxed = true)
        StytchClient.appSessionId = "app-session-id"
        impl =
            OAuthImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockSessionStorage,
                storageHelper = mockStorageHelper,
                api = mockApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `Every ThirdParty provider class has the correct provider name`() {
        listOf(
            Pair(impl.apple, "apple"),
            Pair(impl.amazon, "amazon"),
            Pair(impl.bitbucket, "bitbucket"),
            Pair(impl.coinbase, "coinbase"),
            Pair(impl.discord, "discord"),
            Pair(impl.facebook, "facebook"),
            Pair(impl.figma, "figma"),
            Pair(impl.github, "github"),
            Pair(impl.gitlab, "gitlab"),
            Pair(impl.google, "google"),
            Pair(impl.linkedin, "linkedin"),
            Pair(impl.microsoft, "microsoft"),
            Pair(impl.salesforce, "salesforce"),
            Pair(impl.slack, "slack"),
            Pair(impl.snapchat, "snapchat"),
            Pair(impl.tiktok, "tiktok"),
            Pair(impl.twitch, "twitch"),
            Pair(impl.twitter, "twitter"),
            Pair(impl.yahoo, "yahoo"),
        ).forEach {
            assert(it.first.providerName == it.second)
        }
    }

    @Test
    fun `authenticate returns correct error if PKCE is missing`() =
        runTest {
            every { mockStorageHelper.retrieveCodeVerifier() } returns null
            val result = impl.authenticate(mockk(relaxed = true))
            require(result is StytchResult.Error)
            assert(result.exception is StytchMissingPKCEError)
        }

    @Test
    fun `authenticate returns correct error if api call fails`() =
        runTest {
            every { mockStorageHelper.retrieveCodeVerifier() } returns "code-challenge"
            coEvery { mockApi.authenticateWithThirdPartyToken(any(), any(), any()) } returns
                StytchResult.Error(
                    StytchAPIError(errorType = "something_went_wrong", message = "testing"),
                )
            val result = impl.authenticate(mockk(relaxed = true))
            require(result is StytchResult.Error)
            assert(result.exception is StytchAPIError)
            coVerify { mockApi.authenticateWithThirdPartyToken(any(), any(), "code-challenge") }
            verify(exactly = 1) { mockStorageHelper.clearPKCE() }
        }

    @Test
    fun `authenticate returns success if api call succeeds`() =
        runTest {
            every { mockStorageHelper.retrieveCodeVerifier() } returns "code-challenge"
            coEvery { mockApi.authenticateWithThirdPartyToken(any(), any(), any()) } returns
                StytchResult.Success(
                    mockk(relaxed = true),
                )
            val result = impl.authenticate(mockk(relaxed = true))
            require(result is StytchResult.Success)
            coVerify { mockApi.authenticateWithThirdPartyToken(any(), any(), "code-challenge") }
            verify(exactly = 1) { mockStorageHelper.clearPKCE() }
        }

    @Test
    fun `authenticate with callback calls callback method`() {
        every { mockStorageHelper.retrieveCodeVerifier() } returns null
        val spy = spyk<(OAuthAuthenticatedResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), spy)
        verify { spy.invoke(any()) }
    }
}

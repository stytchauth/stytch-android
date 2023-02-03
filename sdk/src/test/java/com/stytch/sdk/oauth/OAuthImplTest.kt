package com.stytch.sdk.oauth

import com.stytch.sdk.OAuthAuthenticatedResponse
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sdk.sessions.SessionAutoUpdater
import com.stytch.sdk.sessions.SessionStorage
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
import java.security.KeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class OAuthImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.OAuth

    @MockK
    private lateinit var mockSessionStorage: SessionStorage

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
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any()) } just runs
        every { mockStorageHelper.loadValue(any()) } returns ""
        every { mockStorageHelper.saveValue(any(), any()) } just runs
        impl = OAuthImpl(
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
            Pair(impl.amazon, "amazon"),
            Pair(impl.bitbucket, "bitbucket"),
            Pair(impl.coinbase, "coinbase"),
            Pair(impl.discord, "discord"),
            Pair(impl.facebook, "facebook"),
            Pair(impl.github, "github"),
            Pair(impl.gitlab, "gitlab"),
            Pair(impl.google, "google"),
            Pair(impl.linkedin, "linkedin"),
            Pair(impl.microsoft, "microsoft"),
            Pair(impl.slack, "slack"),
            Pair(impl.twitch, "twitch"),
        ).forEach {
            assert(it.first.providerName == it.second)
        }
    }

    @Test
    fun `authenticate returns correct error if PKCE is missing`() = runTest {
        every { mockStorageHelper.retrieveHashedCodeChallenge() } returns null
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == StytchErrorType.OAUTH_MISSING_PKCE.message)
    }

    @Test
    fun `authenticate returns correct error if api call fails`() = runTest {
        every { mockStorageHelper.retrieveHashedCodeChallenge() } returns "code-challenge"
        coEvery { mockApi.authenticateWithThirdPartyToken(any(), any(), any()) } returns StytchResult.Error(
            StytchExceptions.Response(mockk(relaxed = true))
        )
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchExceptions.Response)
        coVerify { mockApi.authenticateWithThirdPartyToken(any(), any(), "code-challenge") }
    }

    @Test
    fun `authenticate returns success if api call succeeds`() = runTest {
        every { mockStorageHelper.retrieveHashedCodeChallenge() } returns "code-challenge"
        coEvery { mockApi.authenticateWithThirdPartyToken(any(), any(), any()) } returns StytchResult.Success(
            mockk(relaxed = true)
        )
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Success)
        coVerify { mockApi.authenticateWithThirdPartyToken(any(), any(), "code-challenge") }
    }

    @Test
    fun `authenticate with callback calls callback method`() {
        every { mockStorageHelper.retrieveHashedCodeChallenge() } returns null
        val spy = spyk<(OAuthAuthenticatedResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), spy)
        verify { spy.invoke(any()) }
    }
}

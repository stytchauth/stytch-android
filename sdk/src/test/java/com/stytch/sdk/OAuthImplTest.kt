package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sessions.SessionAutoUpdater
import com.stytch.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import java.security.KeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
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
        mockkStatic("com.stytch.sdk.extensions.StringExtKt", "com.stytch.sdk.extensions.ByteArrayExtKt")
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
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

    // TODO: oauthimpl.authenticate()
    // TODO: oauthimpl.authenticate(callback)
}

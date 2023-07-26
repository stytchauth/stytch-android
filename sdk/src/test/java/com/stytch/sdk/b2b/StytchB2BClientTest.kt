package com.stytch.sdk.b2b

import android.content.Context
import android.net.Uri
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinks
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeeplinkResponse
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.network.StytchErrorType
import com.stytch.sdk.common.stytchError
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class StytchB2BClientTest {
    private var mContextMock = mockk<Context>(relaxed = true)
    private val dispatcher = Dispatchers.Unconfined

    @MockK
    private lateinit var mockMagicLinks: B2BMagicLinks

    @OptIn(DelicateCoroutinesApi::class)
    val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun before() {
        Dispatchers.setMain(mainThreadSurrogate)
        mockkStatic(KeyStore::class)
        mockkStatic("com.stytch.sdk.common.extensions.ContextExtKt")
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        mContextMock = mockk(relaxed = true)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        mockkObject(StytchB2BApi)
        every { StorageHelper.initialize(any()) } just runs
        every { StorageHelper.loadValue(any()) } returns ""
        every { StorageHelper.generateHashedCodeChallenge() } returns Pair("", "")
        MockKAnnotations.init(this, true, true)
        StytchB2BClient.magicLinks = mockMagicLinks
        StytchB2BClient.externalScope = TestScope()
        StytchB2BClient.dispatchers = StytchDispatchers(dispatcher, dispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun after() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        unmockkAll()
        clearAllMocks()
    }

    @Test(expected = IllegalStateException::class)
    fun `assertInitialized throws IllegalStateException when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.assertInitialized()
    }

    @Test
    fun `assertInitialized does not throw IllegalStateException when properly configured`() {
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "")
        stytchClientObject.assertInitialized()
    }

    @Test
    fun `should trigger StytchB2BApi configure when calling StytchB2BClient configure`() {
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "")
        verify { StytchB2BApi.configure("", deviceInfo) }
    }

    @Test
    fun `should trigger StorageHelper initialize when calling StytchB2BClient configure`() {
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "")
        verify { StorageHelper.initialize(mContextMock) }
    }

    @Test
    fun `should fetch bootstrap data when calling StytchB2BClient configure`() {
        runBlocking {
            StytchB2BClient.configure(mContextMock, "")
            coVerify { StytchB2BApi.getBootstrapData() }
        }
    }

    @Test(expected = StytchExceptions.Critical::class)
    fun `an exception in StytchB2BClient configure throws a Critical exception`() {
        every { StorageHelper.initialize(any()) } throws RuntimeException("Test")
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "")
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchB2BClient magicLinks throws IllegalStateException when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.magicLinks
    }

    @Test
    fun `accessing StytchB2BClient magicLinks returns instance of MagicLinks when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.magicLinks
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchB2BClient sessions throws IllegalStateException when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.sessions
    }

    @Test
    fun `accessing StytchB2BClient sessions returns instance of Session when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.sessions
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchB2BClient organization throws IllegalStateException when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.organization
    }

    @Test
    fun `accessing StytchB2BClient organization returns instance of Session when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.organization
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchB2BClient member throws IllegalStateException when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.member
    }

    @Test
    fun `accessing StytchB2BClient member returns instance of member when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.member
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchB2BClient passwords throws IllegalStateException when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.passwords
    }

    @Test
    fun `accessing StytchB2BClient passwords returns instance of Session when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.passwords
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchB2BClient discovery throws IllegalStateException when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.discovery
    }

    @Test
    fun `accessing StytchB2BClient discovery returns instance of Discovery when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.discovery
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchB2BClient sso throws IllegalStateException when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.sso
    }

    @Test
    fun `accessing StytchB2BClient sso returns instance of SSO when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.sso
    }

    @Test(expected = IllegalStateException::class)
    fun `handle with coroutines throws IllegalStateException when not configured`() {
        runBlocking {
            every { StytchB2BApi.isInitialized } returns false
            StytchB2BClient.handle(mockk(), 30U)
        }
    }

    @Test
    fun `handle with coroutines returns NotHandled with correct error when token is missing`() {
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns null
            }
            val response = StytchB2BClient.handle(mockUri, 30U)
            require(response is DeeplinkHandledStatus.NotHandled)
            assert(response.reason == StytchErrorType.DEEPLINK_MISSING_TOKEN.message)
        }
    }

    @Test
    fun `handle with coroutines returns NotHandled with correct error when token is unknown`() {
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns "something unexpected"
            }
            val response = StytchB2BClient.handle(mockUri, 30U)
            require(response is DeeplinkHandledStatus.NotHandled)
            assert(response.reason == StytchErrorType.DEEPLINK_UNKNOWN_TOKEN.message)
        }
    }

    @Test
    fun `handle with coroutines delegates to magiclinks when token is DISCOVERY`() {
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns "DISCOVERY"
            }
            val mockAuthResponse = mockk<DiscoveryEMLAuthResponse>()
            coEvery { mockMagicLinks.discoveryAuthenticate(any()) } returns mockAuthResponse
            val response = StytchB2BClient.handle(mockUri, 30U)
            coVerify { mockMagicLinks.discoveryAuthenticate(any()) }
            assert(response == DeeplinkHandledStatus.Handled(DeeplinkResponse.Discovery(mockAuthResponse)))
        }
    }

    @Test
    fun `handle with coroutines delegates to magiclinks when token is MAGIC_LINKS`() {
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns "MULTI_TENANT_MAGIC_LINKS"
            }
            val mockAuthResponse = mockk<AuthResponse>()
            coEvery { mockMagicLinks.authenticate(any()) } returns mockAuthResponse
            val response = StytchB2BClient.handle(mockUri, 30U)
            coVerify { mockMagicLinks.authenticate(any()) }
            assert(response == DeeplinkHandledStatus.Handled(DeeplinkResponse.Auth(mockAuthResponse)))
        }
    }

    @Test
    fun `handle with coroutines returns NotHandled with correct error  when token is OAUTH`() {
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns "OAUTH"
            }
            val response = StytchB2BClient.handle(mockUri, 30U)
            require(response is DeeplinkHandledStatus.NotHandled)
            assert(response.reason == StytchErrorType.DEEPLINK_UNKNOWN_TOKEN.message)
        }
    }

    @Test
    fun `handle with callback returns value in callback method when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        val mockUri = mockk<Uri> {
            every { getQueryParameter(any()) } returns null
        }
        val mockCallback = spyk<(DeeplinkHandledStatus) -> Unit>()
        StytchB2BClient.handle(mockUri, 30u, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test(expected = IllegalStateException::class)
    fun `stytchError throws IllegalStateException`() {
        stytchError("Test")
    }

    @Test
    fun `canHandle only returns true for supported token types`() {
        val uri = mockk<Uri>()
        every { uri.getQueryParameter(any()) } returns "MULTI_TENANT_MAGIC_LINKS"
        assert(StytchB2BClient.canHandle(uri))
        every { uri.getQueryParameter(any()) } returns "MAGIC_LINKS"
        assert(!StytchB2BClient.canHandle(uri))
        every { uri.getQueryParameter(any()) } returns "something random"
        assert(!StytchB2BClient.canHandle(uri))
    }
}

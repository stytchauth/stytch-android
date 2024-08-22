package com.stytch.sdk.b2b

import android.app.Application
import android.content.Context
import android.net.Uri
import com.google.android.recaptcha.Recaptcha
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinks
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.SessionsAuthenticateResponseData
import com.stytch.sdk.b2b.oauth.OAuth
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeeplinkResponse
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchClientOptions
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchDeeplinkMissingTokenError
import com.stytch.sdk.common.errors.StytchDeeplinkUnkownTokenTypeError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class StytchB2BClientTest {
    private var mContextMock = mockk<Context>(relaxed = true)
    private val dispatcher = Dispatchers.Unconfined

    @MockK
    private lateinit var mockMagicLinks: B2BMagicLinks

    @MockK
    private lateinit var mockOAuth: OAuth

    @MockK
    private lateinit var mockPKCEPairManager: PKCEPairManager

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun before() {
        Dispatchers.setMain(mainThreadSurrogate)
        mockkStatic(KeyStore::class)
        mockkStatic(
            "com.stytch.sdk.common.extensions.ContextExtKt",
            "com.stytch.sdk.b2b.extensions.StytchResultExtKt",
        )
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        val mockApplication: Application =
            mockk {
                every { registerActivityLifecycleCallbacks(any()) } just runs
                every { packageName } returns "Stytch"
            }
        mContextMock =
            mockk(relaxed = true) {
                every { applicationContext } returns mockApplication
            }
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        mockkObject(StytchB2BApi)
        mockkObject(StytchB2BApi.Sessions)
        mockkObject(Recaptcha)
        MockKAnnotations.init(this, true, true)
        coEvery { Recaptcha.getClient(any(), any()) } returns Result.success(mockk(relaxed = true))
        every { StorageHelper.initialize(any()) } just runs
        every { StorageHelper.loadValue(any()) } returns ""
        every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
        every { mockPKCEPairManager.getPKCECodePair() } returns mockk()
        coEvery { StytchB2BApi.getBootstrapData() } returns StytchResult.Error(mockk())
        StytchB2BClient.magicLinks = mockMagicLinks
        StytchB2BClient.oauth = mockOAuth
        StytchB2BClient.externalScope = TestScope()
        StytchB2BClient.dispatchers = StytchDispatchers(dispatcher, dispatcher)
        StytchB2BClient.dfpProvider = mockk()
        StytchB2BClient.pkcePairManager = mockPKCEPairManager
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun after() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        unmockkAll()
        clearAllMocks()
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `assertInitialized throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.assertInitialized()
    }

    @Test
    fun `assertInitialized does not throw StytchSDKNotConfiguredError when properly configured`() {
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

    @Test
    fun `configures DFP when calling StytchB2BClient configure`() {
        runBlocking {
            StytchB2BClient.configure(mContextMock, "")
            verify(exactly = 1) { StytchB2BApi.configureDFP(any(), any(), any(), any()) }
        }
    }

    @Test
    fun `should validate persisted sessions if applicable when calling StytchB2BClient configure`() {
        runBlocking {
            val mockResponse: StytchResult<SessionsAuthenticateResponseData> =
                mockk {
                    every { launchSessionUpdater(any(), any()) } just runs
                }
            coEvery { StytchB2BApi.Sessions.authenticate(any()) } returns mockResponse
            // no session data == no authentication/updater
            every { StorageHelper.loadValue(any()) } returns null
            StytchB2BClient.configure(mContextMock, "")
            coVerify(exactly = 0) { StytchB2BApi.Sessions.authenticate(any()) }
            verify(exactly = 0) { mockResponse.launchSessionUpdater(any(), any()) }
            // yes session data == yes authentication/updater
            every { StorageHelper.loadValue(any()) } returns "some-session-data"
            StytchB2BClient.configure(mContextMock, "")
            coVerify(exactly = 1) { StytchB2BApi.Sessions.authenticate() }
            verify(exactly = 1) { mockResponse.launchSessionUpdater(any(), any()) }
        }
    }

    @Test
    fun `should report the initialization state after configuration and initialization is complete`() {
        runTest {
            val mockResponse: StytchResult<SessionsAuthenticateResponseData> =
                mockk {
                    every { launchSessionUpdater(any(), any()) } just runs
                }
            coEvery { StytchB2BApi.Sessions.authenticate(any()) } returns mockResponse
            val callback = spyk<(Boolean) -> Unit>()
            StytchB2BClient.configure(mContextMock, "", StytchClientOptions(), callback)
            // callback is called with expected value
            verify(exactly = 1) { callback(true) }
            // isInitialized has fired
            assert(StytchB2BClient.isInitialized.value)
        }
    }

    @Test(expected = StytchInternalError::class)
    fun `an exception in StytchB2BClient configure throws a StytchInternalError exception`() {
        every { StorageHelper.initialize(any()) } throws RuntimeException("Test")
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "")
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchB2BClient magicLinks throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.magicLinks
    }

    @Test
    fun `accessing StytchB2BClient magicLinks returns instance of MagicLinks when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.magicLinks
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchB2BClient sessions throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.sessions
    }

    @Test
    fun `accessing StytchB2BClient sessions returns instance of Session when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.sessions
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchB2BClient organization throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.organization
    }

    @Test
    fun `accessing StytchB2BClient organization returns instance of Session when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.organization
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchB2BClient member throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.member
    }

    @Test
    fun `accessing StytchB2BClient member returns instance of member when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.member
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchB2BClient passwords throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.passwords
    }

    @Test
    fun `accessing StytchB2BClient passwords returns instance of Session when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.passwords
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchB2BClient discovery throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.discovery
    }

    @Test
    fun `accessing StytchB2BClient discovery returns instance of Discovery when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.discovery
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchB2BClient sso throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.sso
    }

    @Test
    fun `accessing StytchB2BClient sso returns instance of SSO when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.sso
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchB2BClient dfp throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BClient.dfp
    }

    @Test
    fun `accessing StytchB2BClient dfp returns instance of DFP when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        StytchB2BClient.dfp
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `handle with coroutines throws StytchSDKNotConfiguredError when not configured`() {
        runBlocking {
            every { StytchB2BApi.isInitialized } returns false
            StytchB2BClient.handle(mockk(), 30U)
        }
    }

    @Test
    fun `handle with coroutines returns NotHandled with correct error when token is missing`() {
        runBlocking {
            val deviceInfo = DeviceInfo()
            every { mContextMock.getDeviceInfo() } returns deviceInfo
            StytchB2BClient.configure(mContextMock, "")
            val mockUri =
                mockk<Uri> {
                    every { getQueryParameter(any()) } returns null
                }
            val response = StytchB2BClient.handle(mockUri, 30U)
            require(response is DeeplinkHandledStatus.NotHandled)
            assert(response.reason is StytchDeeplinkMissingTokenError)
        }
    }

    @Test
    fun `handle with coroutines returns NotHandled with correct error when token is unknown`() {
        runBlocking {
            val deviceInfo = DeviceInfo()
            every { mContextMock.getDeviceInfo() } returns deviceInfo
            StytchB2BClient.configure(mContextMock, "")
            val mockUri =
                mockk<Uri> {
                    every { getQueryParameter(any()) } returns "something unexpected"
                }
            val response = StytchB2BClient.handle(mockUri, 30U)
            require(response is DeeplinkHandledStatus.NotHandled)
            assert(response.reason is StytchDeeplinkUnkownTokenTypeError)
        }
    }

    @Test
    fun `handle with coroutines delegates to magiclinks when token is DISCOVERY`() {
        runBlocking {
            val deviceInfo = DeviceInfo()
            every { mContextMock.getDeviceInfo() } returns deviceInfo
            StytchB2BClient.configure(mContextMock, "")
            val mockUri =
                mockk<Uri> {
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
            val deviceInfo = DeviceInfo()
            every { mContextMock.getDeviceInfo() } returns deviceInfo
            StytchB2BClient.configure(mContextMock, "")
            val mockUri =
                mockk<Uri> {
                    every { getQueryParameter(any()) } returns "MULTI_TENANT_MAGIC_LINKS"
                }
            val mockAuthResponse = mockk<EMLAuthenticateResponse>()
            coEvery { mockMagicLinks.authenticate(any()) } returns mockAuthResponse
            val response = StytchB2BClient.handle(mockUri, 30U)
            coVerify { mockMagicLinks.authenticate(any()) }
            assert(response == DeeplinkHandledStatus.Handled(DeeplinkResponse.Auth(mockAuthResponse)))
        }
    }

    @Test
    fun `handle with coroutines delegates to oauth when token is OAUTH`() {
        runBlocking {
            val deviceInfo = DeviceInfo()
            every { mContextMock.getDeviceInfo() } returns deviceInfo
            StytchB2BClient.configure(mContextMock, "")
            val mockUri =
                mockk<Uri> {
                    every { getQueryParameter(any()) } returns "OAUTH"
                }
            val mockAuthResponse = mockk<OAuthAuthenticateResponse>()
            coEvery { mockOAuth.authenticate(any()) } returns mockAuthResponse
            val response = StytchB2BClient.handle(mockUri, 30U)
            assert(response == DeeplinkHandledStatus.Handled(DeeplinkResponse.Auth(mockAuthResponse)))
        }
    }

    @Test
    fun `handle with callback returns value in callback method when configured`() {
        every { StytchB2BApi.isInitialized } returns true
        val mockUri =
            mockk<Uri> {
                every { getQueryParameter(any()) } returns null
            }
        val mockCallback = spyk<(DeeplinkHandledStatus) -> Unit>()
        StytchB2BClient.handle(mockUri, 30u, mockCallback)
        verify { mockCallback.invoke(any()) }
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

    @Test
    fun `getPKCECodePair delegates to the PKCEPairManager`() {
        StytchB2BClient.getPKCECodePair()
        verify(exactly = 1) { mockPKCEPairManager.getPKCECodePair() }
    }
}

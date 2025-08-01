package com.stytch.sdk.b2b

import android.app.Application
import android.content.Context
import android.net.Uri
import com.google.android.recaptcha.Recaptcha
import com.squareup.moshi.Moshi
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.B2BSessionData
import com.stytch.sdk.b2b.network.models.SessionsAuthenticateResponseData
import com.stytch.sdk.common.AppLifecycleListener
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.EndpointOptions
import com.stytch.sdk.common.NetworkChangeListener
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StorageHelper.loadValue
import com.stytch.sdk.common.StytchClientOptions
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchDeeplinkMissingTokenError
import com.stytch.sdk.common.errors.StytchDeeplinkUnkownTokenTypeError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.utils.SHORT_FORM_DATE_FORMATTER
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
import java.util.Date
import java.util.UUID

internal class StytchB2BClientTest {
    private var mContextMock = mockk<Context>(relaxed = true)
    private val dispatcher = Dispatchers.Unconfined

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
        every { EncryptionManager.createNewKeys(any()) } returns Unit
        mockkObject(NetworkChangeListener)
        every { NetworkChangeListener.configure(any(), any()) } just runs
        every { NetworkChangeListener.networkIsAvailable } returns true
        mockkObject(AppLifecycleListener)
        every { AppLifecycleListener.configure(any()) } just runs
        val mockApplication: Application =
            mockk {
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
        mockkObject(StytchB2BApi.Events)
        mockkObject(Recaptcha)
        MockKAnnotations.init(this, true, true)
        coEvery { Recaptcha.fetchClient(any(), any()) } returns mockk(relaxed = true)
        every { StorageHelper.initialize(any()) } returns
            mockk {
                coEvery { join() } just runs
            }
        every { StorageHelper.loadValue(any()) } returns "{}"
        every { StorageHelper.saveValue(any(), any()) } just runs
        every { StorageHelper.saveLong(any(), any()) } just runs
        every { StorageHelper.getLong(any()) } returns 0
        every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
        every { mockPKCEPairManager.getPKCECodePair() } returns mockk()
        coEvery {
            StytchB2BApi.Events.logEvent(any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns mockk()
        coEvery { StytchB2BApi.getBootstrapData() } returns StytchResult.Error(mockk())
        StytchB2BClient.configurationManager.externalScope = TestScope()
        StytchB2BClient.configurationManager.dispatchers = StytchDispatchers(dispatcher, dispatcher)
        StytchB2BClient.configurationManager.dfpProvider = mockk()
        StytchB2BClient.configurationManager.pkcePairManager = mockPKCEPairManager
        StytchB2BClient.sessionStorage = mockk(relaxed = true, relaxUnitFun = true)
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
        StytchB2BApi.assertInitialized()
    }

    @Test
    fun `assertInitialized does not throw StytchSDKNotConfiguredError when properly configured`() {
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "")
        StytchB2BApi.assertInitialized()
    }

    @Test
    fun `should trigger StytchB2BApi configure when calling StytchB2BClient configure`() {
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        val publicToken = UUID.randomUUID().toString()
        stytchClientObject.configure(mContextMock, publicToken)
        verify { StytchB2BApi.configure(publicToken, deviceInfo, any(), any(), any()) }
    }

    @Test
    fun `should trigger StorageHelper initialize when calling StytchB2BClient configure`() {
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, UUID.randomUUID().toString())
        verify { StorageHelper.initialize(mContextMock) }
    }

    @Test
    fun `should fetch bootstrap data when calling StytchB2BClient configure`() {
        runBlocking {
            StytchB2BClient.configure(mContextMock, UUID.randomUUID().toString())
            coVerify { StytchB2BApi.getBootstrapData() }
        }
    }

    @Test
    fun `configures DFP when calling StytchB2BClient configure`() {
        runBlocking {
            StytchB2BClient.configure(mContextMock, UUID.randomUUID().toString())
            verify(exactly = 1) { StytchB2BApi.configureDFP(any()) }
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
            every { StytchB2BClient.sessionStorage.memberSession } returns null
            StytchB2BClient.configure(mContextMock, UUID.randomUUID().toString())
            coVerify(exactly = 0) { StytchB2BApi.Sessions.authenticate(any()) }
            verify(exactly = 0) { mockResponse.launchSessionUpdater(any(), any()) }
            // yes session data, but expired, no authentication/updater
            val mockExpiredSession =
                mockk<B2BSessionData>(relaxed = true) {
                    every { expiresAt } returns SHORT_FORM_DATE_FORMATTER.format(Date(0L))
                }
            val mockExpiredSessionJSON =
                Moshi
                    .Builder()
                    .build()
                    .adapter(B2BSessionData::class.java)
                    .lenient()
                    .toJson(mockExpiredSession)
            every { StorageHelper.loadValue(any()) } returns mockExpiredSessionJSON
            StytchB2BClient.configure(mContextMock, UUID.randomUUID().toString())
            coVerify(exactly = 0) { StytchB2BApi.Sessions.authenticate() }
            verify(exactly = 0) { mockResponse.launchSessionUpdater(any(), any()) }
            // yes session data, and valid, yes authentication/updater
            val mockValidSession =
                mockk<B2BSessionData>(relaxed = true) {
                    every { expiresAt } returns SHORT_FORM_DATE_FORMATTER.format(Date(Date().time + 1000))
                }
            val mockValidSessionJSON =
                Moshi
                    .Builder()
                    .build()
                    .adapter(B2BSessionData::class.java)
                    .lenient()
                    .toJson(mockValidSession)
            every { StorageHelper.loadValue(any()) } returns mockValidSessionJSON
            StytchB2BClient.configure(mContextMock, UUID.randomUUID().toString())
            coVerify(exactly = 1) { StytchB2BApi.Sessions.authenticate() }
            verify(exactly = 1) { mockResponse.launchSessionUpdater(any(), any()) }
        }
    }

    @Test
    fun `should report the initialization state after configuration and initialization is complete`() {
        runBlocking {
            val mockResponse: StytchResult<SessionsAuthenticateResponseData> =
                mockk {
                    every { launchSessionUpdater(any(), any()) } just runs
                }
            coEvery { StytchB2BApi.Sessions.authenticate(any()) } returns mockResponse
            val callback = spyk<(Boolean) -> Unit>()
            StytchB2BClient.configure(mContextMock, UUID.randomUUID().toString(), StytchClientOptions(), callback)
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

    @Test
    fun `calling StytchB2BClient configure with the same public token and no options short circuits`() {
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "publicToken")
        stytchClientObject.configure(mContextMock, "publicToken")
        stytchClientObject.configure(mContextMock, "publicToken")
        verify(exactly = 1) { mContextMock.getDeviceInfo() }
    }

    @Test
    fun `calling StytchB2BClient configure with different public tokens and no options doesn't short circuit`() {
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "publicToken1")
        stytchClientObject.configure(mContextMock, "publicToken2")
        stytchClientObject.configure(mContextMock, "publicToken3")
        verify(exactly = 3) { mContextMock.getDeviceInfo() }
    }

    @Test
    fun `calling StytchB2BClient configure with the same public token and the same options short circuits`() {
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(
            mContextMock,
            "publicToken",
            StytchClientOptions(EndpointOptions("dfppa-domain.com")),
        )
        stytchClientObject.configure(
            mContextMock,
            "publicToken",
            StytchClientOptions(EndpointOptions("dfppa-domain.com")),
        )
        stytchClientObject.configure(
            mContextMock,
            "publicToken",
            StytchClientOptions(EndpointOptions("dfppa-domain.com")),
        )
        verify(exactly = 1) { mContextMock.getDeviceInfo() }
    }

    @Test
    fun `calling StytchB2BClient configure with different public tokens and the same options doesn't short circuit`() {
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(
            mContextMock,
            "publicToken1",
            StytchClientOptions(EndpointOptions("dfppa-domain.com")),
        )
        stytchClientObject.configure(
            mContextMock,
            "publicToken2",
            StytchClientOptions(EndpointOptions("dfppa-domain.com")),
        )
        stytchClientObject.configure(
            mContextMock,
            "publicToken3",
            StytchClientOptions(EndpointOptions("dfppa-domain.com")),
        )
        verify(exactly = 3) { mContextMock.getDeviceInfo() }
    }

    @Test
    fun `calling StytchB2BClient configure with the same public tokens and different options doesn't short circuit`() {
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchB2BClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(
            mContextMock,
            "publicToken",
            StytchClientOptions(EndpointOptions("dfppa-domain1.com")),
        )
        stytchClientObject.configure(
            mContextMock,
            "publicToken",
            StytchClientOptions(EndpointOptions("dfppa-domain2.com")),
        )
        stytchClientObject.configure(
            mContextMock,
            "publicToken",
            StytchClientOptions(EndpointOptions("dfppa-domain3.com")),
        )
        verify(exactly = 3) { mContextMock.getDeviceInfo() }
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
            StytchB2BClient.handle(mockk(), 30)
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
            val response = StytchB2BClient.handle(mockUri, 30)
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
            val response = StytchB2BClient.handle(mockUri, 30)
            require(response is DeeplinkHandledStatus.NotHandled)
            assert(response.reason is StytchDeeplinkUnkownTokenTypeError)
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
        StytchB2BClient.handle(mockUri, 30, mockCallback)
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

    @Test
    fun `verify bootstrap data is not overwritten by a failed bootstrap response`() =
        runTest {
            val nonDefaultBootstrapData = BootstrapData(cnameDomain = "android.stytch.com")
            assert(nonDefaultBootstrapData != BootstrapData())
            StytchB2BClient.configurationManager.bootstrapData = nonDefaultBootstrapData
            every { NetworkChangeListener.networkIsAvailable } returns true
            coEvery { StytchB2BApi.getBootstrapData() } returns
                StytchResult.Error(StytchInternalError(RuntimeException("something went wrong")))
            StytchB2BClient.configurationManager.refreshBootstrapData()
            assert(StytchB2BClient.configurationManager.bootstrapData == nonDefaultBootstrapData)
        }

    @Test
    fun `verify bootstrap data is overwritten by a successful bootstrap response`() =
        runTest {
            val nonDefaultBootstrapData = BootstrapData(cnameDomain = "android.stytch.com")
            StytchB2BClient.configurationManager.bootstrapData = BootstrapData()
            assert(StytchB2BClient.configurationManager.bootstrapData != nonDefaultBootstrapData)
            every { NetworkChangeListener.networkIsAvailable } returns true
            coEvery { StytchB2BApi.getBootstrapData() } returns StytchResult.Success(nonDefaultBootstrapData)
            StytchB2BClient.configurationManager.refreshBootstrapData()
            assert(StytchB2BClient.configurationManager.bootstrapData == nonDefaultBootstrapData)
        }
}

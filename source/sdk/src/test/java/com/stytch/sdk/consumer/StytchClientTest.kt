package com.stytch.sdk.consumer

import android.app.Application
import android.content.Context
import android.net.Uri
import com.google.android.recaptcha.Recaptcha
import com.squareup.moshi.Moshi
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.EndpointOptions
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
import com.stytch.sdk.common.utils.SHORT_FORM_DATE_FORMATTER
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.models.AuthData
import com.stytch.sdk.consumer.network.models.SessionData
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
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore
import java.util.Date
import java.util.UUID

internal class StytchClientTest {
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
            "com.stytch.sdk.consumer.extensions.StytchResultExtKt",
        )
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
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
        mockkObject(StytchApi)
        mockkObject(StytchApi.Sessions)
        mockkObject(Recaptcha)
        MockKAnnotations.init(this, true, true)
        coEvery { Recaptcha.fetchClient(any(), any()) } returns mockk(relaxed = true)
        every { StorageHelper.initialize(any()) } just runs
        every { StorageHelper.loadValue(any()) } returns "{}"
        every { StorageHelper.saveValue(any(), any()) } just runs
        every { StorageHelper.saveLong(any(), any()) } just runs
        every { StorageHelper.getLong(any()) } returns 0
        every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns mockk()
        every { mockPKCEPairManager.getPKCECodePair() } returns mockk()
        coEvery { StytchApi.getBootstrapData() } returns StytchResult.Error(mockk())
        StytchClient.externalScope = TestScope()
        StytchClient.dispatchers = StytchDispatchers(dispatcher, dispatcher)
        StytchClient.dfpProvider = mockk()
        StytchClient.pkcePairManager = mockPKCEPairManager
        StytchClient.sessionStorage = mockk(relaxed = true, relaxUnitFun = true)
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
        every { StytchApi.isInitialized } returns false
        StytchClient.assertInitialized()
    }

    @Test
    fun `assertInitialized does not throw StytchSDKNotConfiguredError when properly configured`() {
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "")
        stytchClientObject.assertInitialized()
    }

    @Test
    fun `should trigger StytchApi configure when calling StytchClient configure`() {
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        val publicToken = UUID.randomUUID().toString()
        stytchClientObject.configure(mContextMock, publicToken)
        verify { StytchApi.configure(publicToken, deviceInfo) }
    }

    @Test
    fun `should trigger StorageHelper initialize when calling StytchClient configure`() {
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, UUID.randomUUID().toString())
        verify { StorageHelper.initialize(mContextMock) }
    }

    @Test
    fun `should fetch bootstrap data when calling StytchClient configure`() {
        runBlocking {
            StytchClient.configure(mContextMock, UUID.randomUUID().toString())
            coVerify { StytchApi.getBootstrapData() }
        }
    }

    @Test
    fun `configures DFP when calling StytchClient configure`() {
        runBlocking {
            StytchClient.configure(mContextMock, UUID.randomUUID().toString())
            verify(exactly = 1) { StytchApi.configureDFP(any(), any(), any(), any()) }
        }
    }

    @Test
    fun `should validate persisted sessions if applicable when calling StytchClient configure`() {
        runBlocking {
            val mockResponse: StytchResult<AuthData> =
                mockk {
                    every { launchSessionUpdater(any(), any()) } just runs
                }
            coEvery { StytchApi.Sessions.authenticate(any()) } returns mockResponse
            // no session data == no authentication/updater
            every { StorageHelper.loadValue(any()) } returns null
            StytchClient.configure(mContextMock, UUID.randomUUID().toString())
            coVerify(exactly = 0) { StytchApi.Sessions.authenticate() }
            verify(exactly = 0) { mockResponse.launchSessionUpdater(any(), any()) }
            // yes session data, but expired, no authentication/updater
            val mockExpiredSession =
                mockk<SessionData>(relaxed = true) {
                    every { expiresAt } returns SHORT_FORM_DATE_FORMATTER.format(Date(0L))
                }
            val mockExpiredSessionJSON =
                Moshi
                    .Builder()
                    .build()
                    .adapter(SessionData::class.java)
                    .lenient()
                    .toJson(mockExpiredSession)
            every { StorageHelper.loadValue(any()) } returns mockExpiredSessionJSON
            StytchClient.configure(mContextMock, UUID.randomUUID().toString())
            coVerify(exactly = 0) { StytchApi.Sessions.authenticate() }
            verify(exactly = 0) { mockResponse.launchSessionUpdater(any(), any()) }
            // yes session data, and valid, yes authentication/updater
            val mockValidSession =
                mockk<SessionData>(relaxed = true) {
                    every { expiresAt } returns SHORT_FORM_DATE_FORMATTER.format(Date(Date().time + 1000))
                }
            val mockValidSessionJSON =
                Moshi
                    .Builder()
                    .build()
                    .adapter(SessionData::class.java)
                    .lenient()
                    .toJson(mockValidSession)
            every { StorageHelper.loadValue(any()) } returns mockValidSessionJSON
        }
    }

    @Test
    fun `should report the initialization state after configuration and initialization is complete`() {
        runBlocking {
            val mockResponse: StytchResult<AuthData> =
                mockk {
                    every { launchSessionUpdater(any(), any()) } just runs
                }
            coEvery { StytchApi.Sessions.authenticate(any()) } returns mockResponse
            val callback = spyk<(Boolean) -> Unit>()
            StytchClient.configure(mContextMock, UUID.randomUUID().toString(), StytchClientOptions(), callback)
            // callback is called with expected value
            verify(exactly = 1) { callback(true) }
            // isInitialized has fired
            assert(StytchClient.isInitialized.value)
        }
    }

    @Test(expected = StytchInternalError::class)
    fun `an exception in StytchClient configure throws a StytchInternalError exception`() {
        every { StorageHelper.initialize(any()) } throws RuntimeException("Test")
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, UUID.randomUUID().toString())
    }

    @Test
    fun `calling StytchClient configure with the same public token and no options short circuits`() {
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "publicToken")
        stytchClientObject.configure(mContextMock, "publicToken")
        stytchClientObject.configure(mContextMock, "publicToken")
        verify(exactly = 1) { mContextMock.getDeviceInfo() }
    }

    @Test
    fun `calling StytchClient configure with different public tokens and no options doesn't short circuit`() {
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "publicToken1")
        stytchClientObject.configure(mContextMock, "publicToken2")
        stytchClientObject.configure(mContextMock, "publicToken3")
        verify(exactly = 3) { mContextMock.getDeviceInfo() }
    }

    @Test
    fun `calling StytchClient configure with the same public token and the same options short circuits`() {
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
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
    fun `calling StytchClient configure with different public tokens and the same options doesn't short circuit`() {
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
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
    fun `calling StytchClient configure with the same public tokens and different options doesn't short circuit`() {
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
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
    fun `accessing StytchClient magicLinks throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.magicLinks
    }

    @Test
    fun `accessing StytchClient magicLinks returns instance of MagicLinks when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.magicLinks
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchClient otps throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.otps
    }

    @Test
    fun `accessing StytchClient otps returns instance of OTP when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.otps
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchClient passwords throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.passwords
    }

    @Test
    fun `accessing StytchClient passwords returns instance of Password when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.passwords
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchClient sessions throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.sessions
    }

    @Test
    fun `accessing StytchClient sessions returns instance of Session when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.sessions
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchClient biometrics throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.biometrics
    }

    @Test
    fun `accessing StytchClient biometrics returns instance of Biometrics when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.biometrics
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchClient user throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.user
    }

    @Test
    fun `accessing StytchClient user returns instance of UserManagement when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.user
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchClient oauth throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.oauth
    }

    @Test
    fun `accessing StytchClient oauth returns instance of OAuth when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.oauth
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchClient dfp throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.dfp
    }

    @Test
    fun `accessing StytchClient dfp returns instance of DFP when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.dfp
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `accessing StytchClient passkeys throws StytchSDKNotConfiguredError when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.passkeys
    }

    @Test
    fun `accessing StytchClient passkeys returns instance of Passkeys when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.passkeys
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `handle with coroutines throws StytchSDKNotConfiguredError when not configured`() {
        runBlocking {
            every { StytchApi.isInitialized } returns false
            StytchClient.handle(mockk(), 30)
        }
    }

    @Test
    fun `handle with coroutines returns NotHandled with correct error when token is missing`() {
        runBlocking {
            val deviceInfo = DeviceInfo()
            every { mContextMock.getDeviceInfo() } returns deviceInfo
            StytchClient.configure(mContextMock, "")
            val mockUri =
                mockk<Uri> {
                    every { getQueryParameter(any()) } returns null
                }
            val response = StytchClient.handle(mockUri, 30)
            require(response is DeeplinkHandledStatus.NotHandled)
            assert(response.reason is StytchDeeplinkMissingTokenError)
        }
    }

    @Test
    fun `handle with coroutines returns NotHandled with correct error when token is unknown`() {
        runBlocking {
            val deviceInfo = DeviceInfo()
            every { mContextMock.getDeviceInfo() } returns deviceInfo
            StytchClient.configure(mContextMock, "")
            val mockUri =
                mockk<Uri> {
                    every { getQueryParameter(any()) } returns "something unexpected"
                }
            val response = StytchClient.handle(mockUri, 30)
            require(response is DeeplinkHandledStatus.NotHandled)
            assert(response.reason is StytchDeeplinkUnkownTokenTypeError)
        }
    }

    @Test
    fun `handle with callback returns value in callback method when configured`() {
        every { StytchApi.isInitialized } returns true
        val mockUri =
            mockk<Uri> {
                every { getQueryParameter(any()) } returns null
            }
        val mockCallback = spyk<(DeeplinkHandledStatus) -> Unit>()
        StytchClient.handle(mockUri, 30, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `canHandle only returns true for supported token types`() {
        val uri = mockk<Uri>()
        every { uri.getQueryParameter(any()) } returns "MAGIC_LINKS"
        assert(StytchClient.canHandle(uri))
        every { uri.getQueryParameter(any()) } returns "OAUTH"
        assert(StytchClient.canHandle(uri))
        every { uri.getQueryParameter(any()) } returns "RESET_PASSWORD"
        assert(StytchClient.canHandle(uri))
        every { uri.getQueryParameter(any()) } returns "MULTI_TENANT_MAGIC_LINKS"
        assert(!StytchClient.canHandle(uri))
        every { uri.getQueryParameter(any()) } returns "something random"
        assert(!StytchClient.canHandle(uri))
    }

    @Test
    fun `getPKCECodePair delegates to the PKCEPairManager`() {
        StytchClient.getPKCECodePair()
        verify(exactly = 1) { mockPKCEPairManager.getPKCECodePair() }
    }
}

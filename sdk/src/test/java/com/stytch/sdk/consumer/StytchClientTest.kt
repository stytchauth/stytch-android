package com.stytch.sdk.consumer

import android.content.Context
import android.net.Uri
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.network.StytchErrorType
import com.stytch.sdk.common.stytchError
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.oauth.OAuth
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

internal class StytchClientTest {
    private var mContextMock = mockk<Context>(relaxed = true)
    private val dispatcher = Dispatchers.Unconfined

    @MockK
    private lateinit var mockMagicLinks: MagicLinks

    @MockK
    private lateinit var mockOAuth: OAuth

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
        mockkObject(StytchApi)
        every { StorageHelper.initialize(any()) } just runs
        every { StorageHelper.loadValue(any()) } returns ""
        every { StorageHelper.generateHashedCodeChallenge() } returns Pair("", "")
        MockKAnnotations.init(this, true, true)
        StytchClient.oauth = mockOAuth
        StytchClient.magicLinks = mockMagicLinks
        StytchClient.externalScope = TestScope()
        StytchClient.dispatchers = StytchDispatchers(dispatcher, dispatcher)
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
        every { StytchApi.isInitialized } returns false
        StytchClient.assertInitialized()
    }

    @Test
    fun `assertInitialized does not throw IllegalStateException when properly configured`() {
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
        stytchClientObject.configure(mContextMock, "")
        verify { StytchApi.configure("", deviceInfo) }
    }

    @Test
    fun `should trigger StorageHelper initialize when calling StytchClient configure`() {
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "")
        verify { StorageHelper.initialize(mContextMock) }
    }

    @Test(expected = StytchExceptions.Critical::class)
    fun `an exception in StytchClient configure throws a Critical exception`() {
        every { StorageHelper.initialize(any()) } throws RuntimeException("Test")
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        every { mContextMock.getDeviceInfo() } returns deviceInfo
        stytchClientObject.configure(mContextMock, "")
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient magicLinks throws IllegalStateException when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.magicLinks
    }

    @Test
    fun `accessing StytchClient magicLinks returns instance of MagicLinks when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.magicLinks
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient otps throws IllegalStateException when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.otps
    }

    @Test
    fun `accessing StytchClient otps returns instance of OTP when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.otps
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient passwords throws IllegalStateException when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.passwords
    }

    @Test
    fun `accessing StytchClient passwords returns instance of Password when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.passwords
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient sessions throws IllegalStateException when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.sessions
    }

    @Test
    fun `accessing StytchClient sessions returns instance of Session when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.sessions
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient biometrics throws IllegalStateException when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.biometrics
    }

    @Test
    fun `accessing StytchClient biometrics returns instance of Biometrics when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.biometrics
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient user throws IllegalStateException when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.user
    }

    @Test
    fun `accessing StytchClient user returns instance of UserManagement when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.user
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient oauth throws IllegalStateException when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchClient.oauth
    }

    @Test
    fun `accessing StytchClient oauth returns instance of OAuth when configured`() {
        every { StytchApi.isInitialized } returns true
        StytchClient.oauth
    }

    @Test(expected = IllegalStateException::class)
    fun `handle with coroutines throws IllegalStateException when not configured`() {
        runBlocking {
            every { StytchApi.isInitialized } returns false
            StytchClient.handle(mockk(), 30U)
        }
    }

    @Test
    fun `handle with coroutines returns NotHandled with correct error when token is missing`() {
        runBlocking {
            every { StytchApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns null
            }
            val response = StytchClient.handle(mockUri, 30U)
            require(response is DeeplinkHandledStatus.NotHandled)
            assert(response.reason == StytchErrorType.DEEPLINK_MISSING_TOKEN.message)
        }
    }

    @Test
    fun `handle with coroutines returns NotHandled with correct error when token is unknown`() {
        runBlocking {
            every { StytchApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns "something unexpected"
            }
            val response = StytchClient.handle(mockUri, 30U)
            require(response is DeeplinkHandledStatus.NotHandled)
            assert(response.reason == StytchErrorType.DEEPLINK_UNKNOWN_TOKEN.message)
        }
    }

    @Test
    fun `handle with coroutines delegates to magiclinks when token is MAGIC_LINKS`() {
        runBlocking {
            every { StytchApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns "MAGIC_LINKS"
            }
            val mockAuthResponse = mockk<AuthResponse>()
            coEvery { mockMagicLinks.authenticate(any()) } returns mockAuthResponse
            val response = StytchClient.handle(mockUri, 30U)
            coVerify { mockMagicLinks.authenticate(any()) }
            assert(response == DeeplinkHandledStatus.Handled(mockAuthResponse))
        }
    }

    @Test
    fun `handle with coroutines delegates to oauth when token is OAUTH`() {
        runBlocking {
            every { StytchApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns "OAUTH"
            }
            val mockAuthResponse = mockk<OAuthAuthenticatedResponse>()
            coEvery { mockOAuth.authenticate(any()) } returns mockAuthResponse
            val response = StytchClient.handle(mockUri, 30U)
            coVerify { mockOAuth.authenticate(any()) }
            assert(response == DeeplinkHandledStatus.Handled(mockAuthResponse))
        }
    }

    @Test
    fun `handle with callback returns value in callback method when configured`() {
        every { StytchApi.isInitialized } returns true
        val mockUri = mockk<Uri> {
            every { getQueryParameter(any()) } returns null
        }
        val mockCallback = spyk<(DeeplinkHandledStatus) -> Unit>()
        StytchClient.handle(mockUri, 30u, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test(expected = IllegalStateException::class)
    fun `stytchError throws IllegalStateException`() {
        stytchError("Test")
    }

    @Test
    fun `canHandle only returns true for supported token types`() {
        val uri = mockk<Uri>()
        every { uri.getQueryParameter(any()) } returns "MAGIC_LINKS"
        assert(StytchClient.canHandle(uri))
        every { uri.getQueryParameter(any()) } returns "OAUTH"
        assert(StytchClient.canHandle(uri))
        every { uri.getQueryParameter(any()) } returns "PASSWORD_RESET"
        assert(StytchClient.canHandle(uri))
        every { uri.getQueryParameter(any()) } returns "MULTI_TENANT_MAGIC_LINKS"
        assert(!StytchClient.canHandle(uri))
        every { uri.getQueryParameter(any()) } returns "something random"
        assert(!StytchClient.canHandle(uri))
    }
}

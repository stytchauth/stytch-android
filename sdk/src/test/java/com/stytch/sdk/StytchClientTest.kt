package com.stytch.sdk

import android.content.Context
import android.net.Uri
import com.stytch.sdk.network.StytchApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@Suppress("SwallowedException", "MaxLineLength")
internal class StytchClientTest {
    var mContextMock = mockk<Context>(relaxed = true)
    val dispatcher = Dispatchers.Unconfined

    @OptIn(DelicateCoroutinesApi::class)
    val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun before() {
        Dispatchers.setMain(mainThreadSurrogate)
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        mContextMock = mockk<Context>(relaxed = true)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        every { StorageHelper.initialize(any()) } just runs
        every { StorageHelper.loadValue(any()) } returns ""
        every { StorageHelper.generateHashedCodeChallenge() } returns Pair("", "")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun after() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        unmockkAll()
    }

    @Test(expected = IllegalStateException::class)
    fun `assertInitialized throws IllegalStateException when not configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns false
        StytchClient.assertInitialized()
    }

    @Test
    fun `assertInitialized does not throw IllegalStateException when properly configured`() {
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { stytchClientObject["getDeviceInfo"].invoke(mContextMock) }.returns(deviceInfo)
        stytchClientObject.configure(mContextMock, "")
        stytchClientObject.assertInitialized()
    }

    @Test
    fun `should trigger StytchApi configure when calling StytchClient configure`() {
        mockkObject(StytchApi)
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { stytchClientObject["getDeviceInfo"].invoke(mContextMock) }.returns(deviceInfo)
        stytchClientObject.configure(mContextMock, "")
        verify { StytchApi.configure("", deviceInfo) }
    }

    @Test
    fun `should trigger StorageHelper initialize when calling StytchClient configure`() {
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { stytchClientObject["getDeviceInfo"].invoke(mContextMock) }.returns(deviceInfo)
        stytchClientObject.configure(mContextMock, "")
        verify { StorageHelper.initialize(mContextMock) }
    }

    @Test(expected = StytchExceptions.Critical::class)
    fun `an exception in StytchClient configure throws a Critical exception`() {
        every { StorageHelper.initialize(any()) } throws RuntimeException("Test")
        val deviceInfo = DeviceInfo()
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        every { stytchClientObject["getDeviceInfo"].invoke(mContextMock) }.returns(deviceInfo)
        stytchClientObject.configure(mContextMock, "")
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient magicLinks throws IllegalStateException when not configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns false
        StytchClient.magicLinks
    }

    @Test
    fun `accessing StytchClient magicLinks returns instance of MagicLinks when configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns true
        StytchClient.magicLinks
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient otps throws IllegalStateException when not configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns false
        StytchClient.otps
    }

    @Test
    fun `accessing StytchClient otps returns instance of OTP when configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns true
        StytchClient.otps
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient passwords throws IllegalStateException when not configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns false
        StytchClient.passwords
    }

    @Test
    fun `accessing StytchClient passwords returns instance of Password when configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns true
        StytchClient.passwords
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing StytchClient sessions throws IllegalStateException when not configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns false
        StytchClient.sessions
    }

    @Test
    fun `accessing StytchClient sessions returns instance of Session when configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns true
        StytchClient.sessions
    }

    @Test(expected = IllegalStateException::class)
    fun `handle with coroutines throws IllegalStateException when not configured`() {
        runBlocking {
            mockkObject(StytchApi)
            every { StytchApi.isInitialized } returns false
            StytchClient.handle(mockk(), 30U)
        }
    }

    @Test
    fun `handle with coroutines returns AuthResponse with correct error when token is missing`() {
        runBlocking {
            mockkObject(StytchApi)
            every { StytchApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns null
            }
            val response = StytchClient.handle(mockUri, 30U)
            require(response is StytchResult.Error)
            require(response.exception is StytchExceptions.Input)
            assert(response.exception.reason == "Magic link missing token")
        }
    }

    @Test
    fun `handle with coroutines returns AuthResponse with correct error when token is unknown`() {
        runBlocking {
            mockkObject(StytchApi)
            every { StytchApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns "something unexpected"
            }
            val response = StytchClient.handle(mockUri, 30U)
            require(response is StytchResult.Error)
            require(response.exception is StytchExceptions.Input)
            assert(response.exception.reason == "Unknown magic link type")
        }
    }

    @Test
    fun `handle with coroutines delegates to magiclinks when token is MAGIC_LINKS`() {
        runBlocking {
            mockkObject(StytchApi)
            every { StytchApi.isInitialized } returns true
            val mockUri = mockk<Uri> {
                every { getQueryParameter(any()) } returns "MAGIC_LINKS"
            }
            val mockAuthResponse = mockk<AuthResponse>()
            val mockMagicLinks = mockk<MagicLinks> {
                coEvery { authenticate(any()) } returns mockAuthResponse
            }
            StytchClient.magicLinks = mockMagicLinks
            val response = StytchClient.handle(mockUri, 30U)
            coVerify { mockMagicLinks.authenticate(any()) }
            assert(response == mockAuthResponse)
        }
    }

    @Test
    fun `handle with callback returns value in callback method when configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns true
        val mockUri = mockk<Uri> {
            every { getQueryParameter(any()) } returns null
        }
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        runBlocking {
            StytchClient.externalScope = this
            StytchClient.dispatchers = StytchDispatchers(dispatcher, dispatcher)
            StytchClient.handle(mockUri, 30u, mockCallback)
        }
        verify { mockCallback.invoke(any()) }
    }

    @Test(expected = IllegalStateException::class)
    fun `stytchError throws IllegalStateException`() {
        stytchError("Test")
    }
}

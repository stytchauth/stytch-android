package com.stytch.sdk

import android.content.Context
import com.stytch.sdk.network.StytchApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import java.security.KeyStore

internal class StytchClientTest {

    var mContextMock = mockk<Context>(relaxed = true)
    val dispatcher = Dispatchers.Unconfined

    val params = MagicLinks.EmailMagicLinks.Parameters(
        email = "email@email.com"
    )

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling loginOrCreate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.magicLinks.email.loginOrCreate(params)
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling authenticate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token"))
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `should trigger StytchApi configure when calling StytchClient configure`() {
        mockkObject(StytchApi)
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        val deviceInfo = DeviceInfo()
        every { stytchClientObject["getDeviceInfo"].invoke(mContextMock) }.returns(deviceInfo)
        stytchClientObject.configure(mContextMock, "", "")
        verify { StytchApi.configure("", "", deviceInfo) }
    }

    @Before
    fun before(){
        mockkConstructor(StorageHelper::class)
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any(), any()) } returns Unit
        mContextMock = mockk<Context>(relaxed = true)
        every { KeyStore.getInstance(any())} returns mockk(relaxed = true)
        every { anyConstructed<StorageHelper>().loadValue(any()) } returns ""
        every { anyConstructed<StorageHelper>().getHashedCodeChallenge(any()) } returns Pair("","")
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun `should return result success loginOrCreate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreate(
                email = params.email,
                codeChallenge = "",
                codeChallengeMethod = "",
                loginMagicLinkUrl = null
            )
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.magicLinks.email.loginOrCreate(params)
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success loginOrCreate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreate(
                email = params.email,
                loginMagicLinkUrl = null,
            codeChallenge = any(),
            codeChallengeMethod = any())
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.magicLinks.email.loginOrCreate(params) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success authenticate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate(any(), codeVerifier = any(),  sessionDurationMinutes = any())
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token"))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success authenticate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate("token", sessionDurationMinutes = 60u, codeVerifier = "", )
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token")) {
            assert(it is StytchResult.Success)
        }
    }
}

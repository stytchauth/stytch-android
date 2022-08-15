package com.stytch.sdk

import android.content.Context
import com.stytch.sdk.network.StytchApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any

internal class StytchClientTest {

    val mContextMock = mockk<Context>(relaxed = true)
    val mainThreadSurrogate = newSingleThreadContext("UI thread")

    val params = MagicLinks.EmailMagicLinks.Parameters(
        email = "email@email.com",
        codeChallenge = "codeChallenge",
        codeChallengeMethod = "codeChallengeMethod"
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
                StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token", "codeVerifier"))
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
        val deviceInfo = DeviceInfo()
        every { stytchClientObject["getDeviceInfo"].invoke(mContextMock) }.returns(deviceInfo)
        stytchClientObject.configure(mContextMock, "", "")
        verify { StytchApi.configure("", "", deviceInfo) }
    }

    @Before
    fun before(){
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun after() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        unmockkAll()
    }

    @Test
    fun `should return result success loginOrCreate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreateEmail(
                email = params.email,
                codeChallenge = params.codeChallenge,
                codeChallengeMethod = params.codeChallengeMethod,
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
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreateEmail(
                email = params.email,
                codeChallenge = params.codeChallenge,
                codeChallengeMethod = params.codeChallengeMethod,
                loginMagicLinkUrl = null)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.magicLinks.email.loginOrCreate(params) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success authenticate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate("token", codeVerifier = "codeVerifier")
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token", "codeVerifier"))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success authenticate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate("token", codeVerifier = "codeVerifier")
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token", "codeVerifier")) {
            assert(it is StytchResult.Success)
        }
    }

}

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
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.mockito.ArgumentMatchers.any

internal class StytchClientTest {

    val mContextMock = mockk<Context>(relaxed = true)

    val params = StytchClient.MagicLinks.Parameters(
        email = "email@email.com"
    )

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling loginOrCreate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.MagicLinks.loginOrCreate(params)
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
                StytchClient.MagicLinks.authenticate("token")
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

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun `should return result success loginOrCreate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreateEmail(params.email, null, null)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.MagicLinks.loginOrCreate(params)
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success loginOrCreate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreateEmail(params.email, null, null)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.MagicLinks.loginOrCreate(params) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success authenticate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate("token")
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.MagicLinks.authenticate("token")
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success authenticate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate("token")
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.MagicLinks.authenticate("token") {
            assert(it is StytchResult.Success)
        }
    }

}

package com.stytch.sdk

import android.content.Context
import com.stytch.sdk.network.StytchApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.mockito.ArgumentMatchers.any

internal class StytchClientTest {

    val mContextMock = mockk<Context>(relaxed = true)
    val stytchApiMagicLinksEmailMock = mockk<StytchApi.MagicLinks.Email>()

    val params = StytchClient.MagicLinks.Parameters(
        email = "email@email.com"
    )

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured`() {
        runBlocking {
            try {
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
    fun `should trigger StytchApi configure when calling StytchClient configure`() {
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        val stytchApiMock = spyk<StytchApi>(recordPrivateCalls = true)
        val deviceInfo = DeviceInfo()
        every { stytchClientObject["getDeviceInfo"].invoke(mContextMock) } returns deviceInfo
        stytchClientObject.configure(mContextMock, "", "")
        verify { stytchApiMock.configure("", "", deviceInfo) }
    }

    @After
    fun after() {
        unmockkAll()
        coEvery {
            stytchApiMagicLinksEmailMock.loginOrCreateEmail(
                email = params.email,
                loginMagicLinkUrl = params.loginMagicLinkUrl,
                loginExpirationMinutes = params.loginExpirationInMinutes,
                signupMagicLinkUrl = params.signupMagicLinkUrl,
                signupExpirationMinutes = params.signupExpirationInMinutes)
        } returns StytchResult.Success(any())
    }

    @Test
    fun `should return result success loginOrCreate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.configure(mContextMock, "", "")
        val result = runBlocking {
            StytchClient.MagicLinks.loginOrCreate(params)
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success loginOrCreate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.MagicLinks.loginOrCreate(params) {
            assert(it is StytchResult.Success)
        }
    }

}

package com.stytch.sdk

import android.util.Log
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

internal class StytchClientTest {

    val stytchObject = spyk<Stytch>(recordPrivateCalls = true)
    val stytchApiMagicLinksSpy = spyk<StytchApi.MagicLinks.Email>()

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

//    @Test
//    fun `IllegalStateException exception not thrown when if Sdk configured`() {
//        val params = StytchClient.MagicLinks.Parameters(
//            email = "email@email.com"
//        )
//        Stytch.configure("", StytchEnvironment.TEST)
//        runBlocking {
//            try {
////              Call method with configuration
//                StytchClient.MagicLinks.loginOrCreate(params)
//            } catch (exception: IllegalStateException) {
////              if exception was thrown test failed
//                return@runBlocking
//            }
////            test failed if no exception was thrown
//            assert(true)
//        }
//    }

    @Before
    fun before() {
        coEvery { stytchApiMagicLinksSpy.loginOrCreateNew(params.email, null, null, 60, 60) }.returns(StytchResult.NetworkError)
        every { stytchObject getProperty "isInitialized" }.returns(true)
    }

    @Test
    fun `should return Stytch SDK as initialized`() {
        assert(stytchObject.isInitialized)
    }

    @Test
    fun `should return network error result when loginOrCreate called with no urls`() {
        runBlocking {
            val res = StytchClient.MagicLinks.loginOrCreate(params)
            assert(res is StytchResult.NetworkError)
        }
    }

}

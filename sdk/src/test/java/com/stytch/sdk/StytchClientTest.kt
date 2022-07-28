package com.stytch.sdk

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

internal class StytchClientTest {

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured`() {
        val params = StytchClient.MagicLinks.Parameters(
            email =  "email@email.com"
        )

        runBlocking {
            try {
//                Call method without configuration
                StytchClient.MagicLinks.loginOrCreate(params)
            } catch ( exception: IllegalStateException){
//                if exception was thrown test passed
                return@runBlocking
            }
//            test failed if no exception was thrown
            assert(false)
        }
    }

}
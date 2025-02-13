package com.stytch.sdk.consumer.oauth

import android.app.Activity
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.UnexpectedCredentialType
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.consumer.NativeOAuthResponse
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class GoogleOneTapImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.OAuth

    @MockK
    private lateinit var mockSessionStorage: ConsumerSessionStorage

    @MockK
    private lateinit var mockGoogleCredentialManagerProvider: GoogleCredentialManagerProvider

    @MockK
    private lateinit var mockPKCEPairManager: PKCEPairManager

    private lateinit var impl: GoogleOneTapImpl
    private val dispatcher = Dispatchers.Unconfined
    private val mockActivity: Activity = mockk()
    private val startParameters =
        OAuth.GoogleOneTap.StartParameters(
            context = mockActivity,
            clientId = "clientId",
        )

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        mockkStatic("com.stytch.sdk.consumer.extensions.StytchResultExtKt")
        every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns mockk(relaxed = true)
        every { mockSessionStorage.lastAuthMethodUsed = any() } just runs
        impl =
            GoogleOneTapImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockSessionStorage,
                api = mockApi,
                credentialManagerProvider = mockGoogleCredentialManagerProvider,
                pkcePairManager = mockPKCEPairManager,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `GoogleOneTapImpl start returns error if nonce fails to generate`() =
        runBlocking {
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } throws RuntimeException("Testing")
            val result = impl.start(startParameters)
            assert(result is StytchResult.Error)
        }

    @Test
    fun `GoogleOneTapImpl start with callback calls callback method`() {
        // short circuit
        every { mockPKCEPairManager.generateAndReturnPKCECodePair() } throws RuntimeException("Testing")
        val spy = spyk<(NativeOAuthResponse) -> Unit>()
        impl.start(startParameters, spy)
        verify { spy.invoke(any()) }
    }

    @Test
    fun `GoogleOneTapImpl start returns error if returned an unexpected credential type`() =
        runBlocking {
            coEvery {
                mockGoogleCredentialManagerProvider.getSignInWithGoogleCredential(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
                mockk {
                    every { credential } returns
                        mockk {
                            every { type } returns "Something Weird"
                        }
                }
            val result = impl.start(startParameters)
            require(result is StytchResult.Error)
            assert(result.exception is UnexpectedCredentialType)
        }

    @Test
    fun `GoogleOneTapImpl start returns error if create credential fails`() =
        runBlocking {
            coEvery {
                mockGoogleCredentialManagerProvider.getSignInWithGoogleCredential(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
                mockk {
                    every { credential } returns
                        mockk {
                            every { type } returns GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                        }
                }
            every {
                mockGoogleCredentialManagerProvider.createTokenCredential(
                    any(),
                )
            } throws RuntimeException("Testing")
            val result = impl.start(startParameters)
            require(result is StytchResult.Error)
        }

    @Test
    fun `GoogleOneTapImpl delegates to api if everything is successful`() =
        runBlocking {
            coEvery {
                mockGoogleCredentialManagerProvider.getSignInWithGoogleCredential(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
                mockk {
                    every { credential } returns
                        mockk {
                            every { type } returns GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                            every { data } returns mockk(relaxed = true)
                        }
                }
            every {
                mockGoogleCredentialManagerProvider.createTokenCredential(
                    any(),
                )
            } returns
                mockk(relaxed = true) {
                    every { idToken } returns "my-id-token"
                }
            coEvery {
                mockApi.authenticateWithGoogleIdToken(any(), any(), any())
            } returns StytchResult.Success(mockk(relaxed = true))
            val result = impl.start(startParameters)
            assert(result is StytchResult.Success)
            coVerify { mockApi.authenticateWithGoogleIdToken("my-id-token", any(), any()) }
        }
}

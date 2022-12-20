package com.stytch.sdk

import android.app.Activity
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.stytch.sdk.network.StytchApi
import com.stytch.sessions.SessionAutoUpdater
import com.stytch.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class OAuthImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.OAuth

    @MockK
    private lateinit var mockSessionStorage: SessionStorage

    @MockK
    private lateinit var mockGoogleOAuthProvider: GoogleOAuthProvider
    private lateinit var impl: OAuthImpl
    private val dispatcher = Dispatchers.Unconfined
    private val mockActivity: Activity = mockk()
    private val startParameters = OAuth.Google.StartParameters(
        context = mockActivity,
        clientId = "clientId",
        oAuthRequestIdentifier = 1
    )

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any()) } just runs
        mockkObject(EncryptionManager)
        every { EncryptionManager.generateCodeChallenge() } returns "code-challenge"
        every { EncryptionManager.encryptCodeChallenge(any()) } returns "encrypted-code-challenge"
        impl = OAuthImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            sessionStorage = mockSessionStorage,
            api = mockApi,
            googleOAuthProvider = mockGoogleOAuthProvider,
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `OAuth Google start returns false if oneTapClient fails to initialize`() = runTest {
        every { mockGoogleOAuthProvider.getSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } throws RuntimeException("Testing")
        }
        every { mockGoogleOAuthProvider.getSignInRequest(any(), any(), any()) } returns mockk(relaxed = true)
        val result = impl.google.start(startParameters)
        assert(!result)
    }

    @Test
    fun `OAuth Google start returns false if oneTapClient calls FailureListener`() = runTest {
        val fail = slot<OnFailureListener>()
        val mockedTask: Task<BeginSignInResult> = mockk(relaxed = true) {
            every { addOnSuccessListener(mockActivity, any()) } returns this@mockk
            every { addOnFailureListener(mockActivity, capture(fail)) } answers {
                fail.captured.onFailure(RuntimeException("Testing"))
                this@mockk
            }
        }
        every { mockGoogleOAuthProvider.getSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } returns mockedTask
        }
        every { mockGoogleOAuthProvider.getSignInRequest(any(), any(), any()) } returns mockk(relaxed = true)
        val result = impl.google.start(startParameters)
        assert(!result)
    }

    @Test
    fun `OAuth Google start returns false if startIntentSenderForResult throws`() = runTest {
        val success = slot<OnSuccessListener<BeginSignInResult>>()
        val mockedTask: Task<BeginSignInResult> = mockk(relaxed = true) {
            every { addOnSuccessListener(mockActivity, capture(success)) } answers {
                success.captured.onSuccess(mockk(relaxed = true))
                this@mockk
            }
            every { addOnFailureListener(mockActivity, any()) } returns this@mockk
        }
        every { mockGoogleOAuthProvider.getSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } returns mockedTask
        }
        every { mockGoogleOAuthProvider.getSignInRequest(any(), any(), any()) } returns mockk(relaxed = true)
        every {
            mockActivity.startIntentSenderForResult(any(), any(), any(), any(), any(), any(), any())
        } throws(IntentSender.SendIntentException())
        val result = impl.google.start(startParameters)
        assert(!result)
    }

    @Test
    fun `OAuth Google start returns true if startIntentSenderForResult succeeds`() = runTest {
        val success = slot<OnSuccessListener<BeginSignInResult>>()
        val mockedTask: Task<BeginSignInResult> = mockk(relaxed = true) {
            every { addOnSuccessListener(mockActivity, capture(success)) } answers {
                success.captured.onSuccess(mockk(relaxed = true))
                this@mockk
            }
            every { addOnFailureListener(mockActivity, any()) } returns this@mockk
        }
        every { mockGoogleOAuthProvider.getSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } returns mockedTask
        }
        every { mockGoogleOAuthProvider.getSignInRequest(any(), any(), any()) } returns mockk(relaxed = true)
        every { mockActivity.startIntentSenderForResult(any(), any(), any(), any(), any(), any(), any()) } just runs
        val result = impl.google.start(startParameters)
        assert(result)
    }

    @Test
    fun `OAuth Google start with callback calls callback method`() {
        // short circuit
        every { mockGoogleOAuthProvider.getSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } throws RuntimeException("Testing")
        }
        every { mockGoogleOAuthProvider.getSignInRequest(any(), any(), any()) } returns mockk(relaxed = true)
        val spy = spyk<(Boolean) -> Unit>()
        impl.google.start(startParameters, spy)
        verify { spy.invoke(false) }
    }
}

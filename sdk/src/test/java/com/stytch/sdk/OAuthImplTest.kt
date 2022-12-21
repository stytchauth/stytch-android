package com.stytch.sdk

import android.app.Activity
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sessions.SessionAutoUpdater
import com.stytch.sessions.SessionStorage
import com.stytch.sessions.launchSessionUpdater
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
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
    fun `OAuth Google start returns false if nonce fails to generate`() = runTest {
        every { mockGoogleOAuthProvider.createNonce() } throws RuntimeException("Testing")
        every { mockGoogleOAuthProvider.createSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } throws RuntimeException("Testing")
        }
        every { mockGoogleOAuthProvider.getSignInRequest(any(), any(), any()) } returns mockk(relaxed = true)
        val result = impl.google.start(startParameters)
        assert(!result)
    }

    @Test
    fun `OAuth Google start returns false if oneTapClient fails to initialize`() = runTest {
        every { mockGoogleOAuthProvider.createNonce() } returns "nonce"
        every { mockGoogleOAuthProvider.createSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } throws RuntimeException("Testing")
        }
        val result = impl.google.start(startParameters)
        assert(!result)
    }

    @Test
    fun `OAuth Google start returns false if getSignInRequest fails`() = runTest {
        val fail = slot<OnFailureListener>()
        every { mockGoogleOAuthProvider.createNonce() } returns "nonce"
        every { mockGoogleOAuthProvider.createSignInClient(any()) } returns mockk(relaxed = true)
        every { mockGoogleOAuthProvider.getSignInRequest(any(), any(), any()) } throws RuntimeException("test")
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
        every { mockGoogleOAuthProvider.createNonce() } returns "nonce"
        every { mockGoogleOAuthProvider.createSignInClient(any()) } returns mockk {
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
        every { mockGoogleOAuthProvider.createNonce() } returns "nonce"
        every { mockGoogleOAuthProvider.createSignInClient(any()) } returns mockk {
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
        every { mockGoogleOAuthProvider.createNonce() } returns "nonce"
        every { mockGoogleOAuthProvider.createSignInClient(any()) } returns mockk {
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
        every { mockGoogleOAuthProvider.createNonce() } returns "nonce"
        every { mockGoogleOAuthProvider.createSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } throws RuntimeException("Testing")
        }
        every { mockGoogleOAuthProvider.getSignInRequest(any(), any(), any()) } returns mockk(relaxed = true)
        val spy = spyk<(Boolean) -> Unit>()
        impl.google.start(startParameters, spy)
        verify { spy.invoke(false) }
    }

    @Test
    fun `OAuth Google authenticate returns error if nonce is missing`() = runTest {
        every { mockGoogleOAuthProvider.nonce } returns null
        val result = impl.google.authenticate(mockk())
        require(result is StytchResult.Error)
        require(result.exception is StytchExceptions.Critical)
        assert(result.exception.reason is NullPointerException)
    }

    @Test
    fun `OAuth Google authenticate returns error if onetap client is not initialized`() = runTest {
        every { mockGoogleOAuthProvider.nonce } returns "nonce"
        every { mockGoogleOAuthProvider.oneTapClient } returns null
        val result = impl.google.authenticate(mockk())
        require(result is StytchResult.Error)
        require(result.exception is StytchExceptions.Critical)
        assert(result.exception.reason is NullPointerException)
    }

    @Test
    fun `OAuth Google authenticate returns error if ApiException is thrown`() = runTest {
        every { mockGoogleOAuthProvider.nonce } returns "nonce"
        every { mockGoogleOAuthProvider.oneTapClient } returns mockk {
            every { getSignInCredentialFromIntent(any()) } throws ApiException(Status.RESULT_INTERNAL_ERROR)
        }
        val result = impl.google.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        require(result.exception is StytchExceptions.Critical)
        assert(result.exception.reason is ApiException)
    }

    @Test
    fun `OAuth Google authenticate returns correct error if idToken is missing`() = runTest {
        every { mockGoogleOAuthProvider.nonce } returns "nonce"
        every { mockGoogleOAuthProvider.oneTapClient } returns mockk {
            every { getSignInCredentialFromIntent(any()) } returns mockk {
                every { googleIdToken } returns null
            }
        }
        val result = impl.google.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        require(result.exception is StytchExceptions.Input)
        assert(result.exception.reason == StytchErrorType.GOOGLE_ONETAP_MISSING_ID_TOKEN.message)
    }

    @Test
    fun `OAuth Google authenticate returns error if authenticateWithGoogleIdToken fails`() = runTest {
        every { mockGoogleOAuthProvider.nonce } returns "nonce"
        every { mockGoogleOAuthProvider.oneTapClient } returns mockk {
            every { getSignInCredentialFromIntent(any()) } returns mockk {
                every { googleIdToken } returns ""
            }
        }
        coEvery { mockApi.authenticateWithGoogleIdToken(any(), any(), any()) } returns StytchResult.Error(
            StytchExceptions.Response(mockk(relaxed = true))
        )
        val result = impl.google.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        require(result.exception is StytchExceptions.Response)
    }

    @Test
    fun `OAuth Google authenticate returns success if authenticateWithGoogleIdToken succeeds`() = runTest {
        every { mockGoogleOAuthProvider.nonce } returns "nonce"
        every { mockGoogleOAuthProvider.oneTapClient } returns mockk {
            every { getSignInCredentialFromIntent(any()) } returns mockk {
                every { googleIdToken } returns ""
            }
        }
        val mockResponse: StytchResult.Success<AuthData> = mockk {
            every { launchSessionUpdater(any(), any()) } just runs
        }
        coEvery { mockApi.authenticateWithGoogleIdToken(any(), any(), any()) } returns mockResponse
        val result = impl.google.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Success)
    }

    @Test
    fun `OAuth Google authenticate with callback calls callback method`() {
        // shortcircuit
        every { mockGoogleOAuthProvider.nonce } returns null
        val spy = spyk<(AuthResponse) -> Unit>()
        impl.google.authenticate(mockk(), spy)
        verify { spy.invoke(any()) }
    }

    @Test
    fun `OAuth Google signout delegates to onetap client`() {
        val mockSignInClient: SignInClient = mockk {
            every { signOut() } returns mockk()
        }
        every { mockGoogleOAuthProvider.oneTapClient } returns mockSignInClient
        impl.google.signOut()
        verify { mockSignInClient.signOut() }
    }
}

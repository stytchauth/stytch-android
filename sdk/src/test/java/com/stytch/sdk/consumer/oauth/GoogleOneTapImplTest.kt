package com.stytch.sdk.consumer.oauth

import android.app.Activity
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.errors.StytchInvalidAuthorizationCredentialError
import com.stytch.sdk.common.errors.StytchMissingAuthorizationCredentialIdTokenError
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.common.sso.GoogleOneTapProvider
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.models.NativeOAuthData
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
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
internal class GoogleOneTapImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.OAuth

    @MockK
    private lateinit var mockSessionStorage: ConsumerSessionStorage

    @MockK
    private lateinit var mockGoogleOneTapProvider: GoogleOneTapProvider
    private lateinit var impl: GoogleOneTapImpl
    private val dispatcher = Dispatchers.Unconfined
    private val mockActivity: Activity = mockk()
    private val startParameters = OAuth.GoogleOneTap.StartParameters(
        context = mockActivity,
        clientId = "clientId",
        oAuthRequestIdentifier = 1
    )

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        mockkStatic("com.stytch.sdk.consumer.extensions.StytchResultExtKt")
        mockkObject(EncryptionManager)
        every { EncryptionManager.generateCodeChallenge() } returns "code-challenge"
        every { EncryptionManager.encryptCodeChallenge(any()) } returns "encrypted-code-challenge"
        impl = GoogleOneTapImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            sessionStorage = mockSessionStorage,
            api = mockApi,
            googleOneTapProvider = mockGoogleOneTapProvider,
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `GoogleOneTapImpl start returns false if nonce fails to generate`() = runTest {
        every { EncryptionManager.generateCodeChallenge() } throws RuntimeException("Testing")
        val result = impl.start(startParameters)
        assert(!result)
    }

    @Test
    fun `GoogleOneTapImpl start returns false if oneTapClient fails to initialize`() = runTest {
        every { mockGoogleOneTapProvider.getSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } throws RuntimeException("Testing")
        }
        val result = impl.start(startParameters)
        assert(!result)
    }

    @Test
    fun `GoogleOneTapImpl start returns false if getSignInRequest fails`() = runTest {
        every { mockGoogleOneTapProvider.getSignInClient(any()) } returns mockk(relaxed = true)
        every { mockGoogleOneTapProvider.getSignInRequest(any(), any(), any()) } throws RuntimeException("test")
        val result = impl.start(startParameters)
        assert(!result)
    }

    @Test
    fun `GoogleOneTapImpl start returns false if oneTapClient calls FailureListener`() = runTest {
        val fail = slot<OnFailureListener>()
        val mockedTask: Task<BeginSignInResult> = mockk(relaxed = true) {
            every { addOnSuccessListener(mockActivity, any()) } returns this@mockk
            every { addOnFailureListener(mockActivity, capture(fail)) } answers {
                fail.captured.onFailure(RuntimeException("Testing"))
                this@mockk
            }
        }
        every { mockGoogleOneTapProvider.getSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } returns mockedTask
        }
        every { mockGoogleOneTapProvider.getSignInRequest(any(), any(), any()) } returns mockk(relaxed = true)
        val result = impl.start(startParameters)
        assert(!result)
    }

    @Test
    fun `GoogleOneTapImpl start returns false if startIntentSenderForResult throws`() = runTest {
        val success = slot<OnSuccessListener<BeginSignInResult>>()
        val mockedTask: Task<BeginSignInResult> = mockk(relaxed = true) {
            every { addOnSuccessListener(mockActivity, capture(success)) } answers {
                success.captured.onSuccess(mockk(relaxed = true))
                this@mockk
            }
            every { addOnFailureListener(mockActivity, any()) } returns this@mockk
        }
        every { mockGoogleOneTapProvider.getSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } returns mockedTask
        }
        every { mockGoogleOneTapProvider.getSignInRequest(any(), any(), any()) } returns mockk(relaxed = true)
        every {
            mockActivity.startIntentSenderForResult(any(), any(), any(), any(), any(), any(), any())
        } throws(IntentSender.SendIntentException())
        val result = impl.start(startParameters)
        assert(!result)
    }

    @Test
    fun `GoogleOneTapImpl start returns true if startIntentSenderForResult succeeds`() = runTest {
        val success = slot<OnSuccessListener<BeginSignInResult>>()
        val mockedTask: Task<BeginSignInResult> = mockk(relaxed = true) {
            every { addOnSuccessListener(mockActivity, capture(success)) } answers {
                success.captured.onSuccess(mockk(relaxed = true))
                this@mockk
            }
            every { addOnFailureListener(mockActivity, any()) } returns this@mockk
        }
        every { mockGoogleOneTapProvider.getSignInClient(any()) } returns mockk {
            every { beginSignIn(any()) } returns mockedTask
        }
        every { mockGoogleOneTapProvider.getSignInRequest(any(), any(), any()) } returns mockk(relaxed = true)
        every { mockActivity.startIntentSenderForResult(any(), any(), any(), any(), any(), any(), any()) } just runs
        val result = impl.start(startParameters)
        assert(result)
    }

    @Test
    fun `GoogleOneTapImpl start with callback calls callback method`() {
        // short circuit
        every { EncryptionManager.generateCodeChallenge() } throws RuntimeException("Testing")
        val spy = spyk<(Boolean) -> Unit>()
        impl.start(startParameters, spy)
        verify { spy.invoke(false) }
    }

    @Test
    fun `GoogleOneTapImpl authenticate returns error if nonce is missing`() = runTest {
        val result = impl.authenticate(mockk())
        require(result is StytchResult.Error)
        require(result.exception is StytchInvalidAuthorizationCredentialError)
    }

    @Test
    fun `GoogleOneTapImpl authenticate returns error if onetap client is not initialized`() = runTest {
        impl.nonce = "nonce"
        val result = impl.authenticate(mockk())
        require(result is StytchResult.Error)
        assert(result.exception is StytchInvalidAuthorizationCredentialError)
    }

    @Test
    fun `GoogleOneTapImpl authenticate returns error if ApiException is thrown`() = runTest {
        impl.nonce = "nonce"
        impl.oneTapClient = mockk {
            every { getSignInCredentialFromIntent(any()) } throws ApiException(Status.RESULT_INTERNAL_ERROR)
        }
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        require(result.exception is StytchInternalError)
        assert((result.exception as StytchInternalError).exception is ApiException)
    }

    @Test
    fun `GoogleOneTapImpl authenticate returns correct error if idToken is missing`() = runTest {
        impl.nonce = "nonce"
        impl.oneTapClient = mockk {
            every { getSignInCredentialFromIntent(any()) } returns mockk {
                every { googleIdToken } returns null
            }
        }
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        require(result.exception is StytchMissingAuthorizationCredentialIdTokenError)
    }

    @Test
    fun `GoogleOneTapImpl authenticate returns error if authenticateWithGoogleIdToken fails`() = runTest {
        impl.nonce = "nonce"
        impl.oneTapClient = mockk {
            every { getSignInCredentialFromIntent(any()) } returns mockk {
                every { googleIdToken } returns ""
            }
        }
        coEvery { mockApi.authenticateWithGoogleIdToken(any(), any(), any()) } returns StytchResult.Error(
            StytchAPIError(name = "something_went_wrong", description = "testing")
        )
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        require(result.exception is StytchAPIError)
    }

    @Test
    fun `GoogleOneTapImpl authenticate returns success if authenticateWithGoogleIdToken succeeds`() = runTest {
        impl.nonce = "nonce"
        impl.oneTapClient = mockk {
            every { getSignInCredentialFromIntent(any()) } returns mockk {
                every { googleIdToken } returns ""
            }
        }
        val mockResponse: StytchResult.Success<NativeOAuthData> = mockk {
            every { launchSessionUpdater(any(), any()) } just runs
        }
        coEvery { mockApi.authenticateWithGoogleIdToken(any(), any(), any()) } returns mockResponse
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Success)
    }

    @Test
    fun `GoogleOneTapImpl authenticate with callback calls callback method`() {
        val spy = spyk<(AuthResponse) -> Unit>()
        impl.authenticate(mockk(), spy)
        verify { spy.invoke(any()) }
    }

    @Test
    fun `GoogleOneTapImpl signout delegates to onetap client`() {
        val mockSignInClient: SignInClient = mockk {
            every { signOut() } returns mockk()
        }
        impl.oneTapClient = mockSignInClient
        impl.signOut()
        verify { mockSignInClient.signOut() }
    }
}

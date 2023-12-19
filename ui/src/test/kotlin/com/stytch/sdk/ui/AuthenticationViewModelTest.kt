package com.stytch.sdk.ui

import android.app.Activity
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchSSOError
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import com.stytch.sdk.common.sso.SSOError
import com.stytch.sdk.common.sso.SSOError.Companion.SSO_EXCEPTION
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.INativeOAuthData
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

@OptIn(ExperimentalCoroutinesApi::class)
internal class AuthenticationViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var mockStytchClient: StytchClient

    private lateinit var viewModel: AuthenticationViewModel

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true, true)
        viewModel = AuthenticationViewModel(mockStytchClient)
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun `authenticateGoogleOneTapLogin delegates to stytch client and emits the result`() = runTest(dispatcher) {
        val result: StytchResult<INativeOAuthData> = mockk(relaxed = true)
        coEvery { mockStytchClient.oauth.googleOneTap.authenticate(any()) } returns result
        val expectedEvent = EventState.Authenticated(result)
        val eventFlow = async {
            viewModel.eventFlow.first()
        }
        viewModel.authenticateGoogleOneTapLogin(mockk(relaxed = true), mockk(relaxed = true), this)
        coVerify(exactly = 1) { mockStytchClient.oauth.googleOneTap.authenticate(any()) }
        assert(eventFlow.await() == expectedEvent)
    }

    @Test
    fun `authenticateThirdPartyOAuth emits the expected event based on intent when success`() = runTest(dispatcher) {
        val resultData = StytchResult.Success<CommonAuthenticationData>(mockk())
        coEvery { mockStytchClient.handle(any(), any()) } returns mockk<DeeplinkHandledStatus.Handled>(relaxed = true) {
            every { response } returns mockk(relaxed = true) {
                every { result } returns resultData
            }
        }
        val expectedEvent = EventState.Authenticated(resultData)
        val eventFlow = async {
            viewModel.eventFlow.first()
        }
        viewModel.authenticateThirdPartyOAuth(Activity.RESULT_OK, mockk(relaxed = true), mockk(relaxed = true), this)
        coVerify(exactly = 1) { mockStytchClient.handle(any(), any()) }
        assert(eventFlow.await() == expectedEvent)
    }

    @Test
    fun `authenticateThirdPartyOAuth emits the expected event based on exception when failed`() = runTest(dispatcher) {
        val eventFlow = async {
            viewModel.eventFlow.first()
        }
        viewModel.authenticateThirdPartyOAuth(
            resultCode = Activity.RESULT_CANCELED,
            intent = mockk(relaxed = true) {
                every { extras } returns mockk {
                    every { getSerializable(SSO_EXCEPTION) } returns SSOError.UserCanceled
                }
            },
            sessionOptions = mockk(relaxed = true),
            scope = this
        )
        val expectedEvent = EventState.Authenticated(StytchResult.Error(StytchSSOError(SSOError.UserCanceled)))
        assert(eventFlow.await() == expectedEvent)
    }

    @Test
    fun `handleDeepLink emits the expected events`() = runTest(dispatcher) {
        // Handled
        var eventFlow = async {
            viewModel.eventFlow.first()
        }
        val resultData = StytchResult.Success<CommonAuthenticationData>(mockk())
        coEvery { mockStytchClient.handle(any(), any()) } returns mockk<DeeplinkHandledStatus.Handled>(relaxed = true) {
            every { response } returns mockk(relaxed = true) {
                every { result } returns resultData
            }
        }
        var expectedEvent: EventState = EventState.Authenticated(resultData)
        viewModel.handleDeepLink(mockk(), mockk(relaxed = true), this)
        coVerify(exactly = 1) { mockStytchClient.handle(any(), any()) }
        assert(eventFlow.await() == expectedEvent)

        // NotHandled
        eventFlow = async {
            viewModel.eventFlow.first()
        }
        coEvery {
            mockStytchClient.handle(any(), any())
        } returns mockk<DeeplinkHandledStatus.NotHandled>(relaxed = true)
        expectedEvent = EventState.Exit
        viewModel.handleDeepLink(mockk(), mockk(relaxed = true), this)
        coVerify(exactly = 2) { mockStytchClient.handle(any(), any()) }
        assert(eventFlow.await() == expectedEvent)

        // ManualHandlingRequired
        eventFlow = async {
            viewModel.eventFlow.first()
        }
        coEvery {
            mockStytchClient.handle(any(), any())
        } returns mockk<DeeplinkHandledStatus.ManualHandlingRequired>(relaxed = true) {
            every { token } returns "test-token"
        }
        expectedEvent = EventState.NavigationRequested(
            NavigationRoute.SetNewPassword(emailAddress = "", token = "test-token")
        )
        viewModel.handleDeepLink(mockk(), mockk(relaxed = true), this)
        coVerify(exactly = 3) { mockStytchClient.handle(any(), any()) }
        assert(eventFlow.await() == expectedEvent)
    }
}

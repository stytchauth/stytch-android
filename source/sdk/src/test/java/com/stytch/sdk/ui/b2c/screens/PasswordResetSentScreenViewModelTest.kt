package com.stytch.sdk.ui.b2c.screens

import androidx.lifecycle.SavedStateHandle
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.NavigationRoute
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
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
internal class PasswordResetSentScreenViewModelTest {
    private val savedStateHandle = SavedStateHandle()
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: PasswordResetSentScreenViewModel
    private lateinit var mockStytchClient: StytchClient

    @Before
    fun before() {
        mockkStatic(KeyStore::class, StytchClient::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true, true)
        mockStytchClient =
            mockk {
                every { events } returns
                    mockk(relaxed = true) {
                        every { logEvent(any(), any()) } just runs
                    }
            }
        viewModel = PasswordResetSentScreenViewModel(savedStateHandle, mockStytchClient)
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun `onDialogDismiss sets showResendDialog to false`() {
        viewModel.onDialogDismiss()
        assert(!viewModel.uiState.value.showResendDialog)
    }

    @Test
    fun `onShowResendDialog sets showResendDialog to true`() {
        viewModel.onShowResendDialog()
        assert(viewModel.uiState.value.showResendDialog)
    }

    @Test
    fun `onResendPasswordResetStart calls resetByEmailStart and does nothing on success`() =
        runTest(dispatcher) {
            val originalState = viewModel.uiState.value
            val result: StytchResult.Success<BasicData> = mockk(relaxed = true)
            coEvery { mockStytchClient.passwords.resetByEmailStart(any()) } returns result
            viewModel.onResendPasswordResetStart(mockk(relaxed = true), this)
            coVerify(exactly = 1) { mockStytchClient.passwords.resetByEmailStart(any()) }
            assert(viewModel.uiState.value == originalState)
        }

    @Test
    fun `onResendPasswordResetStart calls resetByEmailStart and updates state on error`() =
        runTest(dispatcher) {
            val result: StytchResult.Error =
                mockk(relaxed = true) {
                    every { exception } returns
                        mockk<StytchInternalError> {
                            every { message } returns "Something bad happened internally"
                        }
                }
            coEvery { mockStytchClient.passwords.resetByEmailStart(any()) } returns result
            viewModel.onResendPasswordResetStart(mockk(relaxed = true), this)
            coVerify(exactly = 1) { mockStytchClient.passwords.resetByEmailStart(any()) }
            assert(viewModel.uiState.value.genericErrorMessage == "Something bad happened internally")
        }

    @Test
    fun `sendEML updates state and emits event on success`() =
        runTest(dispatcher) {
            every { mockStytchClient.configurationManager.publicToken } returns "publicToken"
            val result: StytchResult.Success<BasicData> = mockk(relaxed = true)
            val eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            coEvery { mockStytchClient.magicLinks.email.loginOrCreate(any()) } returns result
            viewModel.sendEML("", mockk(relaxed = true), Locale.EN, this)
            assert(!viewModel.uiState.value.showLoadingDialog)
            val event = eventFlow.await()
            require(event is EventState.NavigationRequested)
            require(event.navigationRoute is NavigationRoute.EMLConfirmation)
            require(event.navigationRoute.isReturningUser)
        }

    @Test
    fun `sendEML updates state on failure`() =
        runTest(dispatcher) {
            every { mockStytchClient.configurationManager.publicToken } returns "publicToken"
            val result: StytchResult.Error =
                mockk(relaxed = true) {
                    every { exception } returns
                        mockk<StytchInternalError> {
                            every { message } returns "Something bad happened internally"
                        }
                }
            coEvery { mockStytchClient.magicLinks.email.loginOrCreate(any()) } returns result
            viewModel.sendEML("", mockk(relaxed = true), Locale.EN, this)
            assert(!viewModel.uiState.value.showLoadingDialog)
            assert(viewModel.uiState.value.genericErrorMessage == "Something bad happened internally")
        }

    @Test
    fun `sendEmailOTP updates state and emits event on success`() =
        runTest(dispatcher) {
            val result: StytchResult.Success<LoginOrCreateOTPData> =
                mockk {
                    every { value } returns
                        mockk {
                            every { methodId } returns ""
                        }
                }
            val eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            coEvery { mockStytchClient.otps.email.loginOrCreate(any()) } returns result
            viewModel.sendEmailOTP("", mockk(relaxed = true), Locale.EN, this)
            assert(!viewModel.uiState.value.showLoadingDialog)
            val event = eventFlow.await()
            require(event is EventState.NavigationRequested)
            require(event.navigationRoute is NavigationRoute.OTPConfirmation)
            require(event.navigationRoute.isReturningUser)
        }

    @Test
    fun `sendEmailOTP updates state on failure`() =
        runTest(dispatcher) {
            val result: StytchResult.Error =
                mockk(relaxed = true) {
                    every { exception } returns
                        mockk<StytchInternalError> {
                            every { message } returns "Something bad happened internally"
                        }
                }
            coEvery { mockStytchClient.otps.email.loginOrCreate(any()) } returns result
            viewModel.sendEmailOTP("", mockk(relaxed = true), Locale.EN, this)
            assert(!viewModel.uiState.value.showLoadingDialog)
            assert(viewModel.uiState.value.genericErrorMessage == "Something bad happened internally")
        }
}

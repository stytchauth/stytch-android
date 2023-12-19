package com.stytch.sdk.ui.screens

import androidx.lifecycle.SavedStateHandle
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.IAuthData
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.PasswordOptions
import com.stytch.sdk.ui.data.PasswordResetType
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
internal class ReturningUserScreenViewModelTest {
    private val savedStateHandle = SavedStateHandle()
    private val dispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var mockStytchClient: StytchClient

    private lateinit var viewModel: ReturningUserScreenViewModel

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true, true)
        viewModel = ReturningUserScreenViewModel(savedStateHandle, mockStytchClient)
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun `initializeState correctly initializes the state`() = runTest(dispatcher) {
        viewModel.initializeState("my@email.address")
        assert(viewModel.uiState.value.emailState.emailAddress == "my@email.address")
        assert(viewModel.uiState.value.emailState.validEmail == true)
    }

    @Test
    fun `onEmailAddressChanged updates the state as expected`() = runTest(dispatcher) {
        viewModel.onEmailAddressChanged("my@email.com")
        assert(viewModel.uiState.value.emailState.emailAddress == "my@email.com")
        assert(viewModel.uiState.value.emailState.validEmail == true)
        viewModel.onEmailAddressChanged("bad email")
        assert(viewModel.uiState.value.emailState.emailAddress == "bad email")
        assert(viewModel.uiState.value.emailState.validEmail == false)
    }

    @Test
    fun `onPasswordChanged updates the state as expected`() = runTest(dispatcher) {
        viewModel.onPasswordChanged("my new password")
        assert(viewModel.uiState.value.passwordState.password == "my new password")
        assert(viewModel.uiState.value.passwordState.validPassword)
    }

    @Test
    fun `authenticate updates state and emits correct event on success`() = runTest(dispatcher) {
        val result: StytchResult.Success<IAuthData> = mockk(relaxed = true)
        coEvery { mockStytchClient.passwords.authenticate(any()) } returns result
        val eventFlow = async {
            viewModel.eventFlow.first()
        }
        val expectedEvent = EventState.Authenticated(result)
        viewModel.authenticate(mockk(relaxed = true), mockk(), this)
        coVerify(exactly = 1) { mockStytchClient.passwords.authenticate(any()) }
        assert(!viewModel.uiState.value.showLoadingDialog)
        assert(eventFlow.await() == expectedEvent)
    }

    @Test
    fun `authenticate emits correct event on reset_password success`() = runTest(dispatcher) {
        val result: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchAPIError> {
                every { errorType } returns "reset_password"
            }
        }
        coEvery { mockStytchClient.passwords.authenticate(any()) } returns result
        coEvery { mockStytchClient.passwords.resetByEmailStart(any()) } returns mockk<StytchResult.Success<BasicData>>()
        val eventFlow = async {
            viewModel.eventFlow.first()
        }
        viewModel.authenticate(mockk(relaxed = true), PasswordOptions(), this)
        coVerify { mockStytchClient.passwords.authenticate(any()) }
        coVerify { mockStytchClient.passwords.resetByEmailStart(any()) }
        val event = eventFlow.await()
        require(event is EventState.NavigationRequested)
        require(event.navigationRoute is NavigationRoute.PasswordResetSent)
        require(event.navigationRoute.details.resetType == PasswordResetType.DEDUPE)
    }

    @Test
    fun `authenticate updates state on reset_password failure`() = runTest(dispatcher) {
        val result: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchAPIError> {
                every { errorType } returns "reset_password"
            }
        }
        coEvery { mockStytchClient.passwords.authenticate(any()) } returns result
        coEvery { mockStytchClient.passwords.resetByEmailStart(any()) } returns StytchResult.Error(
            StytchInternalError(message = "Testing error state")
        )
        viewModel.authenticate(mockk(relaxed = true), PasswordOptions(), this)
        coVerify { mockStytchClient.passwords.authenticate(any()) }
        coVerify { mockStytchClient.passwords.resetByEmailStart(any()) }
        assert(!viewModel.uiState.value.showLoadingDialog)
        assert(viewModel.uiState.value.genericErrorMessage == "Testing error state")
    }

    @Test
    fun `authenticate updates state on non reset_password StytchAPIError`() = runTest(dispatcher) {
        val result: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchAPIError> {
                every { errorType } returns "something_else"
                every { message } returns "Something happened in the API"
            }
        }
        coEvery { mockStytchClient.passwords.authenticate(any()) } returns result
        viewModel.authenticate(mockk(relaxed = true), PasswordOptions(), this)
        coVerify { mockStytchClient.passwords.authenticate(any()) }
        assert(!viewModel.uiState.value.showLoadingDialog)
        assert(viewModel.uiState.value.genericErrorMessage == "Something happened in the API")
    }

    @Test
    fun `authenticate updates state on non StytchAPIError`() = runTest(dispatcher) {
        val result: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchInternalError> {
                every { message } returns "Something bad happened internally"
            }
        }
        coEvery { mockStytchClient.passwords.authenticate(any()) } returns result
        viewModel.authenticate(mockk(relaxed = true), PasswordOptions(), this)
        coVerify { mockStytchClient.passwords.authenticate(any()) }
        assert(!viewModel.uiState.value.showLoadingDialog)
        assert(viewModel.uiState.value.genericErrorMessage == "Something bad happened internally")
    }

    @Test
    fun `sendEML updates state and emits event on success`() = runTest(dispatcher) {
        val result: StytchResult.Success<BasicData> = mockk(relaxed = true)
        val eventFlow = async {
            viewModel.eventFlow.first()
        }
        coEvery { mockStytchClient.magicLinks.email.loginOrCreate(any()) } returns result
        viewModel.sendEML(mockk(relaxed = true), this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        val event = eventFlow.await()
        require(event is EventState.NavigationRequested)
        require(event.navigationRoute is NavigationRoute.EMLConfirmation)
        require(event.navigationRoute.isReturningUser)
    }

    @Test
    fun `sendEML updates state on failure`() = runTest(dispatcher) {
        val result: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchInternalError> {
                every { message } returns "Something bad happened internally"
            }
        }
        coEvery { mockStytchClient.magicLinks.email.loginOrCreate(any()) } returns result
        viewModel.sendEML(mockk(relaxed = true), this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        assert(viewModel.uiState.value.genericErrorMessage == "Something bad happened internally")
    }

    @Test
    fun `sendEmailOTP updates state and emits event on success`() = runTest(dispatcher) {
        val result: StytchResult.Success<LoginOrCreateOTPData> = mockk {
            every { value } returns mockk {
                every { methodId } returns ""
            }
        }
        val eventFlow = async {
            viewModel.eventFlow.first()
        }
        coEvery { mockStytchClient.otps.email.loginOrCreate(any()) } returns result
        viewModel.sendEmailOTP(mockk(relaxed = true), this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        val event = eventFlow.await()
        require(event is EventState.NavigationRequested)
        require(event.navigationRoute is NavigationRoute.OTPConfirmation)
        require(event.navigationRoute.isReturningUser)
    }

    @Test
    fun `sendEmailOTP updates state on failure`() = runTest(dispatcher) {
        val result: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchInternalError> {
                every { message } returns "Something bad happened internally"
            }
        }
        coEvery { mockStytchClient.otps.email.loginOrCreate(any()) } returns result
        viewModel.sendEmailOTP(mockk(relaxed = true), this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        assert(viewModel.uiState.value.genericErrorMessage == "Something bad happened internally")
    }

    @Test
    fun `onForgotPasswordClicked updates state and emits event on success`() = runTest(dispatcher) {
        val result: StytchResult.Success<BasicData> = mockk(relaxed = true)
        val eventFlow = async {
            viewModel.eventFlow.first()
        }
        coEvery { mockStytchClient.passwords.resetByEmailStart(any()) } returns result
        viewModel.onForgotPasswordClicked(mockk(relaxed = true), this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        val event = eventFlow.await()
        require(event is EventState.NavigationRequested)
        require(event.navigationRoute is NavigationRoute.PasswordResetSent)
        require(event.navigationRoute.details.resetType == PasswordResetType.FORGOT_PASSWORD)
    }

    @Test
    fun `onForgotPasswordClicked updates state on failure`() = runTest(dispatcher) {
        val result: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchInternalError> {
                every { message } returns "Something bad happened internally"
            }
        }
        coEvery { mockStytchClient.passwords.resetByEmailStart(any()) } returns result
        viewModel.onForgotPasswordClicked(mockk(relaxed = true), this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        assert(viewModel.uiState.value.genericErrorMessage == "Something bad happened internally")
    }
}
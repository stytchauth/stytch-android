package com.stytch.sdk.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.consumer.PasswordsStrengthCheckResponse
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.CreateResponse
import com.stytch.sdk.consumer.network.models.Feedback
import com.stytch.sdk.consumer.network.models.StrengthCheckResponse
import com.stytch.sdk.consumer.network.models.StrengthPolicy
import com.stytch.sdk.ui.data.EmailState
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OTPDetails
import dalvik.annotation.TestTarget
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
internal class NewUserScreenViewModelTest {
    private val savedStateHandle = SavedStateHandle()
    private val dispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var mockStytchClient: StytchClient

    private lateinit var viewModel: NewUserScreenViewModel

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true, true)
        viewModel = NewUserScreenViewModel(savedStateHandle, mockStytchClient)
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun `sendEmailMagicLink updates state and emits correct event on success`() = runTest(dispatcher) {
        every { mockStytchClient.publicToken } returns "publicToken"
        val result: StytchResult.Success<BasicData> = mockk(relaxed = true)
        val eventFlow = async {
            viewModel.eventFlow.first()
        }
        coEvery { mockStytchClient.magicLinks.email.loginOrCreate(any()) } returns result
        viewModel.sendEmailMagicLink(mockk(relaxed = true), this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        val event = eventFlow.await()
        require(event is EventState.NavigationRequested)
        require(event.navigationRoute is NavigationRoute.EMLConfirmation)
        require(!event.navigationRoute.isReturningUser)
    }

    @Test
    fun `sendEmailMagicLink updates state on error`() = runTest(dispatcher) {
        every { mockStytchClient.publicToken } returns "publicToken"
        val result: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchInternalError> {
                every { message } returns "Something bad happened internally"
            }
        }
        coEvery { mockStytchClient.magicLinks.email.loginOrCreate(any()) } returns result
        viewModel.sendEmailMagicLink(mockk(relaxed = true), this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        assert(viewModel.uiState.value.emailState.errorMessage == "Something bad happened internally")
    }

    @Test
    fun `sendEmailOTP updates state and emits correct event on success`() = runTest(dispatcher) {
        val result: StytchResult.Success<LoginOrCreateOTPData> = mockk {
            every { value } returns mockk {
                every { methodId } returns "my-method-id"
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
        require(!event.navigationRoute.isReturningUser)
        require(event.navigationRoute.details is OTPDetails.EmailOTP)
        require(event.navigationRoute.details.methodId == "my-method-id")
    }

    @Test
    fun `sendEmailOTP updates state on error`() = runTest(dispatcher) {
        val result: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchInternalError> {
                every { message } returns "Something bad happened internally"
            }
        }
        coEvery { mockStytchClient.otps.email.loginOrCreate(any()) } returns result
        viewModel.sendEmailOTP(mockk(relaxed = true), this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        assert(viewModel.uiState.value.emailState.errorMessage == "Something bad happened internally")
    }

    @Test
    fun `onEmailAddressChanged updates the state as expected`() {
        viewModel.onEmailAddressChanged("my@email.com")
        assert(viewModel.uiState.value.emailState.emailAddress == "my@email.com")
        assert(viewModel.uiState.value.emailState.validEmail == true)
        viewModel.onEmailAddressChanged("bad email address")
        assert(viewModel.uiState.value.emailState.emailAddress == "bad email address")
        assert(viewModel.uiState.value.emailState.validEmail == false)
    }

    @Test
    fun `onPasswordChanged calls strengthcheck and updates state accordingly`() = runTest(dispatcher) {
        val mockedFeedback: Feedback = mockk(relaxed = true)
        val validResponse = StytchResult.Success<StrengthCheckResponse>(
            mockk {
                every { breachedPassword } returns false
                every { feedback } returns mockedFeedback
                every { score } returns 5
                every { validPassword } returns true
                every { strengthPolicy } returns StrengthPolicy.ZXCVBN
            }
        )
        val invalidResponse: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchInternalError> {
                every { message } returns "Something bad happened internally"
            }
        }
        coEvery { mockStytchClient.passwords.strengthCheck(any()) } returns validResponse
        viewModel.onPasswordChanged("", this)
        coVerify(exactly = 1) { mockStytchClient.passwords.strengthCheck(any()) }
        assert(viewModel.uiState.value.passwordState.breachedPassword == validResponse.value.breachedPassword)
        assert(viewModel.uiState.value.passwordState.feedback == validResponse.value.feedback)
        assert(viewModel.uiState.value.passwordState.score == validResponse.value.score)
        assert(viewModel.uiState.value.passwordState.validPassword == validResponse.value.validPassword)

        coEvery { mockStytchClient.passwords.strengthCheck(any()) } returns invalidResponse
        viewModel.onPasswordChanged("", this)
        coVerify(exactly = 2) { mockStytchClient.passwords.strengthCheck(any()) }
        assert(viewModel.uiState.value.passwordState.errorMessage == "Something bad happened internally")
    }

    @Test
    fun `createAccountWithPassword calls password create and updates state and events`() = runTest(dispatcher) {
        val validCreateResponse: CreateResponse = mockk(relaxed = true)
        val eventFlow = async {
            viewModel.eventFlow.first()
        }
        coEvery { mockStytchClient.passwords.create(any()) } returns StytchResult.Success(validCreateResponse)
        viewModel.createAccountWithPassword(30U, this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        val event = eventFlow.await()
        require(event is EventState.Authenticated)
        require(event.result is StytchResult.Success)
        require(event.result.value == validCreateResponse)

        val invalidResponse: StytchResult.Error = mockk(relaxed = true) {
            every { exception } returns mockk<StytchInternalError> {
                every { message } returns "Something bad happened internally"
            }
        }
        coEvery { mockStytchClient.passwords.create(any()) } returns invalidResponse
        viewModel.createAccountWithPassword(30U, this)
        assert(!viewModel.uiState.value.showLoadingDialog)
        assert(viewModel.uiState.value.passwordState.errorMessage == "Something bad happened internally")
    }
}
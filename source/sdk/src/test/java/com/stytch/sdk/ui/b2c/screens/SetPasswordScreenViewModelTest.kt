package com.stytch.sdk.ui.b2c.screens

import androidx.lifecycle.SavedStateHandle
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.Feedback
import com.stytch.sdk.consumer.network.models.IAuthData
import com.stytch.sdk.consumer.network.models.StrengthCheckResponse
import com.stytch.sdk.consumer.network.models.StrengthPolicy
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.shared.data.EmailState
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
internal class SetPasswordScreenViewModelTest {
    private val savedStateHandle = SavedStateHandle()
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: SetPasswordScreenViewModel
    private lateinit var mockStytchClient: StytchClient

    @Before
    fun before() {
        mockkStatic(KeyStore::class, StytchClient::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true, true)
        mockStytchClient = mockk()
        viewModel = SetPasswordScreenViewModel(savedStateHandle, mockStytchClient)
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun `setEmailReadOnly sets the initial state correctly`() {
        assert(!viewModel.uiState.value.emailState.readOnly)
        viewModel.setEmailReadOnly()
        assert(viewModel.uiState.value.emailState.readOnly)
    }

    @Test
    fun `onEmailAddressChanged updates the state correctly`() {
        viewModel.onEmailAddressChanged("new@email.com")
        val expected =
            EmailState(
                emailAddress = "new@email.com",
                validEmail = true,
            )
        assert(viewModel.uiState.value.emailState == expected)
    }

    @Test
    fun `onPasswordChanged delegates to stytchclient, and updates the state as expected`() =
        runTest(dispatcher) {
            coEvery { mockStytchClient.passwords.strengthCheck(any()) } returns
                StytchResult.Error(
                    StytchInternalError(message = "Testing error state"),
                )
            viewModel.onPasswordChanged("fail strength check", this)
            assert(viewModel.uiState.value.passwordState.password == "fail strength check")
            assert(viewModel.uiState.value.passwordState.errorMessage == "Testing error state")

            val mockFeedback: Feedback = mockk(relaxed = true)
            val result =
                StrengthCheckResponse(
                    breachedPassword = false,
                    feedback = mockFeedback,
                    requestId = "",
                    score = 5,
                    statusCode = 200,
                    validPassword = true,
                    strengthPolicy = StrengthPolicy.ZXCVBN,
                )
            coEvery { mockStytchClient.passwords.strengthCheck(any()) } returns StytchResult.Success(result)
            viewModel.onPasswordChanged("pass strength check", this)
            assert(viewModel.uiState.value.passwordState.password == "pass strength check")
            assert(viewModel.uiState.value.passwordState.breachedPassword == result.breachedPassword)
            assert(viewModel.uiState.value.passwordState.feedback == result.feedback)
            assert(viewModel.uiState.value.passwordState.score == result.score)
            assert(viewModel.uiState.value.passwordState.validPassword == result.validPassword)
        }

    @Test
    fun `onSubmit delegates to stytchclient and navigates on success`() =
        runTest(dispatcher) {
            val result = StytchResult.Success(mockk<IAuthData>(relaxed = true))
            coEvery { mockStytchClient.passwords.resetByEmail(any()) } returns result
            val eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            val expectedEvent = EventState.Authenticated(result)
            viewModel.onSubmit("", mockk(relaxed = true), this)
            coVerify(exactly = 1) { mockStytchClient.passwords.resetByEmail(any()) }
            assert(eventFlow.await() == expectedEvent)
        }

    @Test
    fun `onSubmit delegates to stytchclient and updates state on error`() =
        runTest(dispatcher) {
            coEvery { mockStytchClient.passwords.resetByEmail(any()) } returns
                StytchResult.Error(
                    StytchInternalError(message = "Testing error state"),
                )
            viewModel.onSubmit("", mockk(relaxed = true), this)
            coVerify(exactly = 1) { mockStytchClient.passwords.resetByEmail(any()) }
            assert(viewModel.uiState.value.genericErrorMessage == "Testing error state")
        }
}

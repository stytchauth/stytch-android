package com.stytch.sdk.ui.b2c.screens

import androidx.lifecycle.SavedStateHandle
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.NavigationRoute
import com.stytch.sdk.ui.b2c.data.PasswordOptions
import com.stytch.sdk.ui.b2c.data.PasswordResetDetails
import com.stytch.sdk.ui.b2c.data.PasswordResetType
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
internal class EMLConfirmationScreenViewModelTest {
    private val savedStateHandle = SavedStateHandle()
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: EMLConfirmationScreenViewModel
    private lateinit var mockStytchClient: StytchClient

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkStatic(StytchClient::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true, true)
        mockStytchClient =
            mockk {
                every { events } returns
                    mockk(relaxed = true) {
                        every { logEvent(any(), any()) } just runs
                    }
            }
        viewModel = EMLConfirmationScreenViewModel(savedStateHandle, mockStytchClient)
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
    fun `sendResetPasswordEmail sets error message if no email address is passed`() =
        runTest(dispatcher) {
            viewModel.sendResetPasswordEmail(null, mockk(), Locale.EN, this)
            assert(viewModel.uiState.value.genericErrorMessage == "Can't reset password for unknown email address")
        }

    @Test
    fun `sendResetPasswordEmail delegates to correct method and emits event on success`() =
        runTest(dispatcher) {
            coEvery {
                mockStytchClient.passwords.resetByEmailStart(
                    any(),
                )
            } returns StytchResult.Success(BasicData(200, ""))
            every { mockStytchClient.configurationManager.publicToken } returns "publicToken"
            val eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            val mockOptions: PasswordOptions = mockk(relaxed = true)
            val expectedEvent =
                EventState.NavigationRequested(
                    NavigationRoute.PasswordResetSent(
                        PasswordResetDetails(
                            mockOptions.toResetByEmailStartParameters("my@email.com", "publicToken", Locale.EN),
                            PasswordResetType.NO_PASSWORD_SET,
                        ),
                    ),
                )
            viewModel.sendResetPasswordEmail("my@email.com", mockOptions, Locale.EN, this)
            coVerify(exactly = 1) { mockStytchClient.passwords.resetByEmailStart(any()) }
            assert(eventFlow.await() == expectedEvent)
        }

    @Test
    fun `sendResetPasswordEmail delegates to correct method and updates state on error`() =
        runTest(dispatcher) {
            every { mockStytchClient.configurationManager.publicToken } returns "publicToken"
            coEvery { mockStytchClient.passwords.resetByEmailStart(any()) } returns
                StytchResult.Error(
                    StytchInternalError(message = "Testing error state"),
                )
            val mockOptions: PasswordOptions = mockk(relaxed = true)
            viewModel.sendResetPasswordEmail("my@email.com", mockOptions, Locale.EN, this)
            coVerify(exactly = 1) { mockStytchClient.passwords.resetByEmailStart(any()) }
            assert(viewModel.uiState.value.genericErrorMessage == "Testing error state")
        }

    @Test
    fun `resendEML delegates to correct method and updates state on success`() =
        runTest(dispatcher) {
            coEvery {
                mockStytchClient.magicLinks.email.loginOrCreate(any())
            } returns StytchResult.Success(BasicData(200, ""))
            viewModel.resendEML(mockk(relaxed = true), this)
            coVerify(exactly = 1) { mockStytchClient.magicLinks.email.loginOrCreate(any()) }
            assert(!viewModel.uiState.value.showResendDialog)
            assert(viewModel.uiState.value.genericErrorMessage == null)
        }

    @Test
    fun `resendEML delegates to correct method and updates state on error`() =
        runTest(dispatcher) {
            coEvery {
                mockStytchClient.magicLinks.email.loginOrCreate(any())
            } returns
                StytchResult.Error(
                    StytchInternalError(message = "Testing error state"),
                )
            viewModel.resendEML(mockk(), this)
            coVerify(exactly = 1) { mockStytchClient.magicLinks.email.loginOrCreate(any()) }
            assert(!viewModel.uiState.value.showResendDialog)
            assert(viewModel.uiState.value.genericErrorMessage == "Testing error state")
        }
}

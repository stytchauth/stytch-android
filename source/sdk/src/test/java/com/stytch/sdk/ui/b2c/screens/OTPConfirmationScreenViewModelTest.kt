package com.stytch.sdk.ui.b2c.screens

import android.text.format.DateUtils
import androidx.lifecycle.SavedStateHandle
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.IAuthData
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.NavigationRoute
import com.stytch.sdk.ui.b2c.data.OTPDetails
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

@OptIn(ExperimentalCoroutinesApi::class)
internal class OTPConfirmationScreenViewModelTest {
    private val savedStateHandle = SavedStateHandle()
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: OTPConfirmationScreenViewModel
    private lateinit var mockStytchClient: StytchClient

    @Before
    fun before() {
        mockkStatic(KeyStore::class, DateUtils::class, StytchClient::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        every { DateUtils.formatElapsedTime(any()) } returns "Formatted DateTime String"
        MockKAnnotations.init(this, true, true, true)
        mockStytchClient =
            mockk {
                every { events } returns
                    mockk(relaxed = true) {
                        every { logEvent(any(), any()) } just runs
                    }
            }
        viewModel = OTPConfirmationScreenViewModel(savedStateHandle, mockStytchClient)
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun `setInitialState for Email sets the state and kicks off the countdown timer`() =
        runTest(dispatcher) {
            every { DateUtils.formatElapsedTime(any()) } returns "Formatted DateTime String"
            val emailDetails =
                OTPDetails.EmailOTP(
                    methodId = "email-method-id",
                    parameters = OTP.EmailOTP.Parameters("", expirationMinutes = 5),
                )
            val expectedCountdownSeconds = (emailDetails.parameters.expirationMinutes * 60).toLong()
            viewModel.setInitialState(emailDetails, this)
            assert(viewModel.methodId == emailDetails.methodId)
            assert(viewModel.resendCountdownSeconds == expectedCountdownSeconds)
            assert(viewModel.countdownSeconds == expectedCountdownSeconds)
            delay(1000L)
            assert(viewModel.countdownSeconds == expectedCountdownSeconds - 1)
        }

    @Test
    fun `setInitialState for SMS sets the state and kicks off the countdown timer`() =
        runTest(dispatcher) {
            every { DateUtils.formatElapsedTime(any()) } returns "Formatted DateTime String"
            val smsDetails =
                OTPDetails.SmsOTP(
                    methodId = "sms-method-id",
                    parameters = OTP.SmsOTP.Parameters("", expirationMinutes = 10),
                )
            val expectedCountdownSeconds = (smsDetails.parameters.expirationMinutes * 60).toLong()
            viewModel.setInitialState(smsDetails, this)
            assert(viewModel.methodId == smsDetails.methodId)
            assert(viewModel.resendCountdownSeconds == expectedCountdownSeconds)
            assert(viewModel.countdownSeconds == expectedCountdownSeconds)
            delay(1000L)
            assert(viewModel.countdownSeconds == expectedCountdownSeconds - 1)
        }

    @Test
    fun `setInitialState for WhatsApp sets the state and kicks off the countdown timer`() =
        runTest(dispatcher) {
            val whatsappDetails =
                OTPDetails.WhatsAppOTP(
                    methodId = "whatsapp-method-id",
                    parameters = OTP.WhatsAppOTP.Parameters("", expirationMinutes = 15),
                )
            val expectedCountdownSeconds = (whatsappDetails.parameters.expirationMinutes * 60).toLong()
            viewModel.setInitialState(whatsappDetails, this)
            assert(viewModel.methodId == whatsappDetails.methodId)
            assert(viewModel.resendCountdownSeconds == expectedCountdownSeconds)
            assert(viewModel.countdownSeconds == expectedCountdownSeconds)
            delay(1000L)
            assert(viewModel.countdownSeconds == expectedCountdownSeconds - 1)
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
    fun `authenticate updates state and emits event on success`() =
        runTest(dispatcher) {
            val result: StytchResult.Success<IAuthData> = mockk(relaxed = true)
            val eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            coEvery { mockStytchClient.otps.authenticate(any()) } returns result
            viewModel.authenticateOTP("", mockk(relaxed = true), this)
            coVerify(exactly = 1) { mockStytchClient.otps.authenticate(any()) }
            assert(!viewModel.uiState.value.showLoadingDialog)
            assert(viewModel.uiState.value.genericErrorMessage == null)
            assert(eventFlow.await() == EventState.Authenticated(result))
        }

    @Test
    fun `authenticate updates state on failure`() =
        runTest(dispatcher) {
            val result: StytchResult.Error =
                mockk(relaxed = true) {
                    every { exception } returns
                        mockk<StytchInternalError> {
                            every { message } returns "Something bad happened internally"
                        }
                }
            coEvery { mockStytchClient.otps.authenticate(any()) } returns result
            viewModel.authenticateOTP("", mockk(relaxed = true), this)
            coVerify(exactly = 1) { mockStytchClient.otps.authenticate(any()) }
            assert(!viewModel.uiState.value.showLoadingDialog)
            assert(viewModel.uiState.value.genericErrorMessage == "Something bad happened internally")
        }

    @Test
    fun `resendOTP with email delegates to the appropriate StytchClient method`() =
        runTest(dispatcher) {
            coEvery { mockStytchClient.otps.email.loginOrCreate(any()) } returns mockk(relaxed = true)
            viewModel.resendOTP(OTPDetails.EmailOTP(mockk(relaxed = true), ""), this)
            coVerify(exactly = 1) { mockStytchClient.otps.email.loginOrCreate(any()) }
        }

    @Test
    fun `resendOTP with sms delegates to the appropriate StytchClient method`() =
        runTest(dispatcher) {
            coEvery { mockStytchClient.otps.sms.loginOrCreate(any()) } returns mockk(relaxed = true)
            viewModel.resendOTP(OTPDetails.SmsOTP(mockk(relaxed = true), ""), this)
            coVerify(exactly = 1) { mockStytchClient.otps.sms.loginOrCreate(any()) }
        }

    @Test
    fun `resendOTP with whatsapp delegates to the appropriate StytchClient method`() =
        runTest(dispatcher) {
            coEvery { mockStytchClient.otps.whatsapp.loginOrCreate(any()) } returns mockk(relaxed = true)
            viewModel.resendOTP(OTPDetails.WhatsAppOTP(mockk(relaxed = true), ""), this)
            coVerify(exactly = 1) { mockStytchClient.otps.whatsapp.loginOrCreate(any()) }
        }

    @Test
    fun `resendOTP updates everything as expected on success`() =
        runTest(dispatcher) {
            val result: StytchResult.Success<LoginOrCreateOTPData> =
                mockk {
                    every { value } returns
                        mockk {
                            every { methodId } returns "new-method-id"
                        }
                }
            coEvery { mockStytchClient.otps.email.loginOrCreate(any()) } returns result
            viewModel.resendOTP(OTPDetails.EmailOTP(mockk(relaxed = true), ""), this)
            coVerify(exactly = 1) { mockStytchClient.otps.email.loginOrCreate(any()) }
            assert(viewModel.methodId == "new-method-id")
            assert(!viewModel.uiState.value.showLoadingDialog)
            assert(!viewModel.uiState.value.showResendDialog)
        }

    @Test
    fun `resendOTP updates everything as expected on failure`() =
        runTest(dispatcher) {
            val result: StytchResult.Error =
                mockk(relaxed = true) {
                    every { exception } returns
                        mockk<StytchInternalError> {
                            every { message } returns "Something bad happened internally"
                        }
                }
            coEvery { mockStytchClient.otps.email.loginOrCreate(any()) } returns result
            viewModel.resendOTP(OTPDetails.EmailOTP(mockk(relaxed = true), ""), this)
            coVerify(exactly = 1) { mockStytchClient.otps.email.loginOrCreate(any()) }
            assert(!viewModel.uiState.value.showLoadingDialog)
            assert(!viewModel.uiState.value.showResendDialog)
            assert(viewModel.uiState.value.genericErrorMessage == "Something bad happened internally")
        }

    @Test
    fun `sendResetPasswordEmail updates error if no email is passed`() =
        runTest(dispatcher) {
            viewModel.sendResetPasswordEmail(null, mockk(relaxed = true), this)
            assert(viewModel.uiState.value.genericErrorMessage == "Can't reset password for unknown email address")
        }

    @Test
    fun `sendResetPasswordEmail emits correct event on success`() =
        runTest(dispatcher) {
            every { mockStytchClient.publicToken } returns "publicToken"
            val result: StytchResult.Success<BasicData> = mockk(relaxed = true)
            val eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            coEvery { mockStytchClient.passwords.resetByEmailStart(any()) } returns result
            viewModel.sendResetPasswordEmail("my@email.com", mockk(relaxed = true), this)
            val event = eventFlow.await()
            require(event is EventState.NavigationRequested)
            require(event.navigationRoute is NavigationRoute.PasswordResetSent)
            require(event.navigationRoute.details.resetType == PasswordResetType.NO_PASSWORD_SET)
        }

    @Test
    fun `sendResetPasswordEmail updates state on failure`() =
        runTest(dispatcher) {
            every { mockStytchClient.publicToken } returns "publicToken"
            val result: StytchResult.Error =
                mockk(relaxed = true) {
                    every { exception } returns
                        mockk<StytchInternalError> {
                            every { message } returns "Something bad happened internally"
                        }
                }
            coEvery { mockStytchClient.passwords.resetByEmailStart(any()) } returns result
            viewModel.sendResetPasswordEmail("my@email.com", mockk(relaxed = true), this)
            assert(viewModel.uiState.value.genericErrorMessage == "Something bad happened internally")
        }
}

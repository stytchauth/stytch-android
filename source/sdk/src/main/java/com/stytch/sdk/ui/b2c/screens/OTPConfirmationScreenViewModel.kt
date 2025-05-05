package com.stytch.sdk.ui.b2c.screens

import android.text.format.DateUtils
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.util.CoilUtils.result
import com.stytch.sdk.R
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.GenericErrorDetails
import com.stytch.sdk.ui.b2c.data.NavigationRoute
import com.stytch.sdk.ui.b2c.data.OTPDetails
import com.stytch.sdk.ui.b2c.data.PasswordOptions
import com.stytch.sdk.ui.b2c.data.PasswordResetDetails
import com.stytch.sdk.ui.b2c.data.PasswordResetType
import com.stytch.sdk.ui.shared.data.EventTypes
import com.stytch.sdk.ui.shared.data.SessionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class OTPConfirmationScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    @VisibleForTesting
    internal var methodId: String = ""

    @VisibleForTesting
    internal var resendCountdownSeconds: Long = 0

    @VisibleForTesting
    internal var countdownSeconds: Long = 0
        set(value) {
            field = value
            savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                uiState.value.copy(
                    expirationTimeFormatted = DateUtils.formatElapsedTime(value),
                )
        }

    fun setInitialState(
        resendParameters: OTPDetails,
        scope: CoroutineScope = viewModelScope,
    ) {
        methodId =
            when (resendParameters) {
                is OTPDetails.EmailOTP -> resendParameters.methodId
                is OTPDetails.SmsOTP -> resendParameters.methodId
                is OTPDetails.WhatsAppOTP -> resendParameters.methodId
            }
        countdownSeconds =
            (
                when (resendParameters) {
                    is OTPDetails.EmailOTP -> resendParameters.parameters.expirationMinutes
                    is OTPDetails.SmsOTP -> resendParameters.parameters.expirationMinutes
                    is OTPDetails.WhatsAppOTP -> resendParameters.parameters.expirationMinutes
                } * 60
            ).toLong()
        resendCountdownSeconds = countdownSeconds
        scope.launch {
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds -= 1
            }
        }
    }

    fun onDialogDismiss() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showResendDialog = false)
    }

    fun onShowResendDialog() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showResendDialog = true)
    }

    fun authenticateOTP(
        token: String,
        sessionOptions: SessionOptions,
        scope: CoroutineScope = viewModelScope,
    ) {
        scope.launch {
            when (
                val result =
                    stytchClient.otps.authenticate(
                        OTP.AuthParameters(
                            token = token,
                            methodId = methodId,
                            sessionDurationMinutes = sessionOptions.sessionDurationMinutes,
                        ),
                    )
            ) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            genericErrorMessage = null,
                        )
                    _eventFlow.emit(EventState.Authenticated(result))
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            genericErrorMessage = GenericErrorDetails(errorText = result.exception.message),
                        )
                }
            }
        }
    }

    fun resendOTP(
        resend: OTPDetails,
        scope: CoroutineScope = viewModelScope,
    ) {
        scope.launch {
            val result =
                when (resend) {
                    is OTPDetails.EmailOTP -> stytchClient.otps.email.loginOrCreate(resend.parameters)
                    is OTPDetails.SmsOTP ->
                        stytchClient.otps.sms.loginOrCreate(resend.parameters)
                    is OTPDetails.WhatsAppOTP -> stytchClient.otps.whatsapp.loginOrCreate(resend.parameters)
                }
            when (result) {
                is StytchResult.Success -> {
                    if (resend is OTPDetails.EmailOTP) {
                        stytchClient.events.logEvent(
                            eventName = EventTypes.EMAIL_TRY_AGAIN_CLICKED,
                            details =
                                mapOf(
                                    "email" to resend.parameters.email,
                                    "type" to EventTypes.LOGIN_OR_CREATE_OTP,
                                ),
                        )
                    }
                    methodId = result.value.methodId
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            showResendDialog = false,
                        )
                    countdownSeconds = resendCountdownSeconds
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            showResendDialog = false,
                            genericErrorMessage = GenericErrorDetails(result.exception.message),
                        )
                }
            }
        }
    }

    fun sendResetPasswordEmail(
        emailAddress: String?,
        passwordOptions: PasswordOptions,
        locale: Locale,
        scope: CoroutineScope = viewModelScope,
    ) {
        scope.launch {
            emailAddress?.let {
                val parameters =
                    passwordOptions.toResetByEmailStartParameters(
                        emailAddress = emailAddress,
                        publicToken = stytchClient.configurationManager.publicToken,
                        locale = locale,
                    )
                when (val result = stytchClient.passwords.resetByEmailStart(parameters)) {
                    is StytchResult.Success -> {
                        stytchClient.events.logEvent(
                            eventName = EventTypes.EMAIL_SENT,
                            details =
                                mapOf(
                                    "email" to parameters.email,
                                    "type" to EventTypes.RESET_PASSWORD,
                                ),
                        )
                        _eventFlow.emit(
                            EventState.NavigationRequested(
                                NavigationRoute.PasswordResetSent(
                                    PasswordResetDetails(parameters, PasswordResetType.NO_PASSWORD_SET),
                                ),
                            ),
                        )
                    }
                    is StytchResult.Error ->
                        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                            uiState.value.copy(
                                genericErrorMessage = GenericErrorDetails(result.exception.message),
                            )
                }
            } ?: run {
                // this should never happen
                savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                    uiState.value.copy(
                        genericErrorMessage =
                            GenericErrorDetails(
                                errorMessageId = R.string.stytch_b2c_password_reset_unknown_email_address,
                            ),
                    )
            }
        }
    }

    companion object {
        fun factory(savedStateHandle: SavedStateHandle): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    OTPConfirmationScreenViewModel(
                        stytchClient = StytchClient,
                        savedStateHandle = savedStateHandle,
                    )
                }
            }
    }
}

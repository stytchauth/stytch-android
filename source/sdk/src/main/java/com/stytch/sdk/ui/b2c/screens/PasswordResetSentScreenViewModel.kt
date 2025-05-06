package com.stytch.sdk.ui.b2c.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EMLDetails
import com.stytch.sdk.ui.b2c.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.GenericErrorDetails
import com.stytch.sdk.ui.b2c.data.NavigationRoute
import com.stytch.sdk.ui.b2c.data.OTPDetails
import com.stytch.sdk.ui.b2c.data.OTPOptions
import com.stytch.sdk.ui.shared.data.EventTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class PasswordResetSentScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onDialogDismiss() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showResendDialog = false)
    }

    fun onShowResendDialog() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showResendDialog = true)
    }

    fun onResendPasswordResetStart(
        parameters: Passwords.ResetByEmailStartParameters,
        scope: CoroutineScope = viewModelScope,
    ) {
        onDialogDismiss()
        scope.launch {
            when (val result = stytchClient.passwords.resetByEmailStart(parameters = parameters)) {
                is StytchResult.Success -> {
                    stytchClient.events.logEvent(
                        eventName = EventTypes.EMAIL_SENT,
                        details =
                            mapOf(
                                "email" to parameters.email,
                                "type" to EventTypes.RESET_PASSWORD,
                            ),
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            genericErrorMessage = GenericErrorDetails(result.exception.message),
                        )
                }
            }
        }
    }

    fun sendEML(
        emailAddress: String,
        emailMagicLinksOptions: EmailMagicLinksOptions,
        locale: Locale,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(
                showLoadingDialog = true,
                genericErrorMessage = null,
            )
        scope.launch {
            val parameters =
                emailMagicLinksOptions.toParameters(
                    emailAddress = emailAddress,
                    publicToken = stytchClient.configurationManager.publicToken,
                    locale = locale,
                )
            when (val result = stytchClient.magicLinks.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    stytchClient.events.logEvent(
                        eventName = EventTypes.EMAIL_SENT,
                        details =
                            mapOf(
                                "email" to parameters.email,
                                "type" to EventTypes.LOGIN_OR_CREATE_EML,
                            ),
                    )
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.EMLConfirmation(
                                details = EMLDetails(parameters),
                                isReturningUser = true,
                            ),
                        ),
                    )
                }
                is StytchResult.Error ->
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            genericErrorMessage = GenericErrorDetails(result.exception.message),
                        )
            }
        }
    }

    fun sendEmailOTP(
        emailAddress: String,
        otpOptions: OTPOptions,
        locale: Locale,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(
                showLoadingDialog = true,
                genericErrorMessage = null,
            )
        scope.launch {
            val parameters = otpOptions.toEmailOtpParameters(emailAddress, locale)
            when (val result = stytchClient.otps.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    stytchClient.events.logEvent(
                        eventName = EventTypes.EMAIL_SENT,
                        details =
                            mapOf(
                                "email" to parameters.email,
                                "type" to EventTypes.LOGIN_OR_CREATE_OTP,
                            ),
                    )
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.OTPConfirmation(
                                details = OTPDetails.EmailOTP(parameters, methodId = result.value.methodId),
                                isReturningUser = true,
                            ),
                        ),
                    )
                }
                is StytchResult.Error ->
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            genericErrorMessage = GenericErrorDetails(result.exception.message),
                        )
            }
        }
    }

    companion object {
        fun factory(savedStateHandle: SavedStateHandle): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PasswordResetSentScreenViewModel(
                        stytchClient = StytchClient,
                        savedStateHandle = savedStateHandle,
                    )
                }
            }
    }
}

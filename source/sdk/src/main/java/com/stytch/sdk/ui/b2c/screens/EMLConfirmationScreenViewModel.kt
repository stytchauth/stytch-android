package com.stytch.sdk.ui.b2c.screens

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
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.GenericErrorDetails
import com.stytch.sdk.ui.b2c.data.NavigationRoute
import com.stytch.sdk.ui.b2c.data.PasswordOptions
import com.stytch.sdk.ui.b2c.data.PasswordResetDetails
import com.stytch.sdk.ui.b2c.data.PasswordResetType
import com.stytch.sdk.ui.shared.data.EventTypes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class EMLConfirmationScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun resendEML(
        parameters: MagicLinks.EmailMagicLinks.Parameters,
        scope: CoroutineScope = viewModelScope,
    ) {
        scope.launch {
            when (val result = stytchClient.magicLinks.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    stytchClient.events.logEvent(
                        eventName = EventTypes.EMAIL_TRY_AGAIN_CLICKED,
                        details =
                            mapOf(
                                "email" to parameters.email,
                                "type" to EventTypes.LOGIN_OR_CREATE_EML,
                            ),
                    )
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showResendDialog = false,
                            genericErrorMessage = null,
                        )
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showResendDialog = false,
                            genericErrorMessage = GenericErrorDetails(result.exception.message),
                        )
                }
            }
        }
    }

    fun onDialogDismiss() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showResendDialog = false)
    }

    fun onShowResendDialog() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showResendDialog = true)
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
                    EMLConfirmationScreenViewModel(
                        stytchClient = StytchClient,
                        savedStateHandle = savedStateHandle,
                    )
                }
            }
    }
}

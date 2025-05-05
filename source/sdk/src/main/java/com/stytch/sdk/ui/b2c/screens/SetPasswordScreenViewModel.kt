package com.stytch.sdk.ui.b2c.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.GenericErrorDetails
import com.stytch.sdk.ui.shared.data.SessionOptions
import com.stytch.sdk.ui.shared.utils.isValidEmailAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class SetPasswordScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun setEmailReadOnly() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(
                emailState =
                    uiState.value.emailState.copy(
                        readOnly = true,
                    ),
            )
    }

    fun onEmailAddressChanged(emailAddress: String) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(
                emailState =
                    uiState.value.emailState.copy(
                        emailAddress = emailAddress,
                        validEmail = emailAddress.isValidEmailAddress(),
                    ),
            )
    }

    fun onPasswordChanged(
        password: String,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(
                passwordState =
                    uiState.value.passwordState.copy(
                        password = password,
                    ),
            )
        scope.launch {
            when (
                val result =
                    stytchClient.passwords.strengthCheck(
                        Passwords.StrengthCheckParameters(
                            email = uiState.value.emailState.emailAddress,
                            password = password,
                        ),
                    )
            ) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            passwordState =
                                uiState.value.passwordState.copy(
                                    breachedPassword = result.value.breachedPassword,
                                    feedback = result.value.feedback,
                                    score = result.value.score,
                                    validPassword = result.value.validPassword,
                                ),
                        )
                }

                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            passwordState =
                                uiState.value.passwordState.copy(
                                    errorMessage = result.exception.message,
                                ),
                        )
                }
            }
        }
    }

    fun onSubmit(
        token: String,
        sessionOptions: SessionOptions,
        scope: CoroutineScope = viewModelScope,
    ) {
        val password = uiState.value.passwordState.password
        scope.launch {
            val parameters =
                Passwords.ResetByEmailParameters(
                    token = token,
                    password = password,
                    sessionDurationMinutes = sessionOptions.sessionDurationMinutes,
                )
            when (val result = stytchClient.passwords.resetByEmail(parameters)) {
                is StytchResult.Success ->
                    _eventFlow.emit(
                        EventState.Authenticated(result),
                    )
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            genericErrorMessage = GenericErrorDetails(result.exception.message),
                        )
                }
            }
        }
    }

    companion object {
        fun factory(savedStateHandle: SavedStateHandle): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SetPasswordScreenViewModel(
                        stytchClient = StytchClient,
                        savedStateHandle = savedStateHandle,
                    )
                }
            }
    }
}

package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.data.EmailState
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.PasswordState
import com.stytch.sdk.ui.data.SessionOptions
import com.stytch.sdk.ui.utils.isValidEmailAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SetPasswordScreenUiState(
    val emailState: EmailState = EmailState(),
    val passwordState: PasswordState = PasswordState(),
    val genericErrorMessage: String? = null,
    val showLoadingDialog: Boolean = false,
) : Parcelable

internal class SetPasswordScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient = StytchClient
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow("SetPasswordScreenUiState", SetPasswordScreenUiState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun setInitialState(emailAddress: String) {
        savedStateHandle["SetPasswordScreenUiState"] = uiState.value.copy(
            emailState = EmailState(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress(),
                readOnly = true,
            )
        )
    }

    fun onEmailAddressChanged(emailAddress: String) {
        savedStateHandle["SetPasswordScreenUiState"] = uiState.value.copy(
            emailState = uiState.value.emailState.copy(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress(),
            ),
        )
    }

    fun onPasswordChanged(password: String, scope: CoroutineScope = viewModelScope) {
        savedStateHandle["SetPasswordScreenUiState"] = uiState.value.copy(
            passwordState = uiState.value.passwordState.copy(
                password = password,
            ),
        )
        scope.launch {
            when (
                val result = stytchClient.passwords.strengthCheck(
                    Passwords.StrengthCheckParameters(
                        email = uiState.value.emailState.emailAddress,
                        password = password,
                    ),
                )
            ) {
                is StytchResult.Success -> {
                    savedStateHandle["SetPasswordScreenUiState"] = uiState.value.copy(
                        passwordState = uiState.value.passwordState.copy(
                            breachedPassword = result.value.breachedPassword,
                            feedback = result.value.feedback,
                            score = result.value.score,
                            validPassword = result.value.validPassword,
                        ),
                    )
                }

                is StytchResult.Error -> {
                    savedStateHandle["SetPasswordScreenUiState"] = uiState.value.copy(
                        passwordState = uiState.value.passwordState.copy(
                            errorMessage = result.exception.message, // TODO
                        ),
                    )
                }
            }
        }
    }

    fun onSubmit(token: String, sessionOptions: SessionOptions, scope: CoroutineScope = viewModelScope) {
        val password = uiState.value.passwordState.password
        scope.launch {
            val parameters = Passwords.ResetByEmailParameters(
                token = token,
                password = password,
                sessionDurationMinutes = sessionOptions.sessionDurationMinutes,
            )
            when (val result = stytchClient.passwords.resetByEmail(parameters)) {
                is StytchResult.Success -> _eventFlow.emit(
                    EventState.Authenticated(result)
                )
                is StytchResult.Error -> {
                    savedStateHandle["SetPasswordScreenUiState"] = uiState.value.copy(
                        genericErrorMessage = result.exception.message, // TODO
                    )
                }
            }
        }
    }
}

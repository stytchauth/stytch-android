package com.stytch.sdk.ui.screens

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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class SetPasswordScreenUiState(
    val emailState: EmailState = EmailState(),
    val passwordState: PasswordState = PasswordState(),
    val genericErrorMessage: String? = null,
    val showLoadingDialog: Boolean = false,
)

internal class SetPasswordScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SetPasswordScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun setInitialState(emailAddress: String) {
        _uiState.value = _uiState.value.copy(
            emailState = EmailState(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress(),
                readOnly = true,
            )
        )
    }

    fun onEmailAddressChanged(emailAddress: String) {
        _uiState.value = _uiState.value.copy(
            emailState = _uiState.value.emailState.copy(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress(),
            ),
        )
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(
            passwordState = _uiState.value.passwordState.copy(
                password = password,
            ),
        )
        viewModelScope.launch {
            when (
                val result = StytchClient.passwords.strengthCheck(
                    Passwords.StrengthCheckParameters(
                        email = _uiState.value.emailState.emailAddress,
                        password = password,
                    ),
                )
            ) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        passwordState = _uiState.value.passwordState.copy(
                            breachedPassword = result.value.breachedPassword,
                            feedback = result.value.feedback,
                            score = result.value.score,
                            validPassword = result.value.validPassword,
                        ),
                    )
                }

                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        passwordState = _uiState.value.passwordState.copy(
                            errorMessage = result.exception.reason.toString(), // TODO
                        ),
                    )
                }
            }
        }
    }

    fun onSubmit(token: String, sessionOptions: SessionOptions) {
        val password = _uiState.value.passwordState.password
        viewModelScope.launch {
            val parameters = Passwords.ResetByEmailParameters(
                token = token,
                password = password,
                sessionDurationMinutes = sessionOptions.sessionDurationMinutes,
            )
            when (val result = StytchClient.passwords.resetByEmail(parameters)) {
                is StytchResult.Success -> _eventFlow.emit(
                    EventState.Authenticated(result)
                )
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        genericErrorMessage = result.exception.reason.toString(), // TODO
                    )
                }
            }
        }
    }
}

package com.stytch.sdk.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.EmailState
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.data.PasswordState
import com.stytch.sdk.ui.utils.isValidEmailAddress
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class NewUserUiState(
    val emailState: EmailState = EmailState(),
    val passwordState: PasswordState = PasswordState(),
    val showLoadingDialog: Boolean = false,
)

internal class NewUserScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewUserUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun setInitialState(email: String) {
        _uiState.value = _uiState.value.copy(
            emailState = EmailState(emailAddress = email),
        )
    }

    fun sendEmailMagicLink(emailMagicLinksOptions: EmailMagicLinksOptions) {
        val emailState = _uiState.value.emailState
        _uiState.value = _uiState.value.copy(showLoadingDialog = true)
        val parameters = emailMagicLinksOptions.toParameters(emailState.emailAddress)
        viewModelScope.launch {
            when (val result = StytchClient.magicLinks.email.loginOrCreate(parameters = parameters)) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.EMLConfirmation(EMLDetails(parameters), isReturningUser = false),
                        ),
                    )
                }
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(showLoadingDialog = false)
                    _uiState.value = _uiState.value.copy(
                        emailState = emailState.copy(
                            errorMessage = result.exception.reason.toString(), // TODO
                        ),
                    )
                }
            }
        }
    }

    fun sendEmailOTP(otpOptions: OTPOptions) {
        val emailState = _uiState.value.emailState
        _uiState.value = _uiState.value.copy(showLoadingDialog = true)
        viewModelScope.launch {
            val parameters = otpOptions.toEmailOtpParameters(emailState.emailAddress)
            when (val result = StytchClient.otps.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.OTPConfirmation(
                                OTPDetails.EmailOTP(
                                    parameters = parameters,
                                    methodId = result.value.methodId,
                                ),
                                isReturningUser = false,
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        showLoadingDialog = false,
                        emailState = emailState.copy(
                            errorMessage = result.exception.reason.toString(), // TODO
                        ),
                    )
                }
            }
        }
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

    fun createAccountWithPassword(sessionDurationMinutes: UInt) {
        val emailState = _uiState.value.emailState
        val passwordState = _uiState.value.passwordState
        _uiState.value = _uiState.value.copy(showLoadingDialog = true)
        viewModelScope.launch {
            when (
                val result = StytchClient.passwords.create(
                    Passwords.CreateParameters(
                        email = emailState.emailAddress,
                        password = passwordState.password,
                        sessionDurationMinutes = sessionDurationMinutes,
                    ),
                )
            ) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(EventState.Authenticated(result))
                }
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        showLoadingDialog = false,
                        passwordState = _uiState.value.passwordState.copy(
                            errorMessage = result.exception.reason.toString(), // TODO
                        ),
                    )
                }
            }
        }
    }
}

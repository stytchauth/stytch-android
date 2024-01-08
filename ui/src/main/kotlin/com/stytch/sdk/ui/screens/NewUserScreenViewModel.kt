package com.stytch.sdk.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.data.ApplicationUIState
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.utils.isValidEmailAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class NewUserScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun sendEmailMagicLink(emailMagicLinksOptions: EmailMagicLinksOptions, scope: CoroutineScope = viewModelScope) {
        val emailState = uiState.value.emailState
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        val parameters = emailMagicLinksOptions.toParameters(
            emailAddress = emailState.emailAddress,
            publicToken = stytchClient.publicToken,
        )
        scope.launch {
            when (val result = stytchClient.magicLinks.email.loginOrCreate(parameters = parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.EMLConfirmation(EMLDetails(parameters), isReturningUser = false),
                        ),
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        showLoadingDialog = false,
                        emailState = emailState.copy(
                            errorMessage = result.exception.message, // TODO
                        ),
                    )
                }
            }
        }
    }

    fun sendEmailOTP(otpOptions: OTPOptions, scope: CoroutineScope = viewModelScope) {
        val emailState = uiState.value.emailState
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            val parameters = otpOptions.toEmailOtpParameters(emailState.emailAddress)
            when (val result = stytchClient.otps.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
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
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        showLoadingDialog = false,
                        emailState = emailState.copy(
                            errorMessage = result.exception.message, // TODO
                        ),
                    )
                }
            }
        }
    }

    fun onEmailAddressChanged(emailAddress: String) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
            emailState = uiState.value.emailState.copy(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress(),
            ),
        )
    }

    fun onPasswordChanged(password: String, scope: CoroutineScope = viewModelScope) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
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
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        passwordState = uiState.value.passwordState.copy(
                            breachedPassword = result.value.breachedPassword,
                            feedback = result.value.feedback,
                            score = result.value.score,
                            validPassword = result.value.validPassword,
                            strengthPolicy = result.value.strengthPolicy,
                        ),
                    )
                }

                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        passwordState = uiState.value.passwordState.copy(
                            errorMessage = result.exception.message, // TODO
                        ),
                    )
                }
            }
        }
    }

    fun createAccountWithPassword(sessionDurationMinutes: UInt, scope: CoroutineScope = viewModelScope) {
        val emailState = uiState.value.emailState
        val passwordState = uiState.value.passwordState
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            when (
                val result = stytchClient.passwords.create(
                    Passwords.CreateParameters(
                        email = emailState.emailAddress,
                        password = passwordState.password,
                        sessionDurationMinutes = sessionDurationMinutes,
                    ),
                )
            ) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(EventState.Authenticated(result))
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        showLoadingDialog = false,
                        passwordState = uiState.value.passwordState.copy(
                            errorMessage = result.exception.message, // TODO
                        ),
                    )
                }
            }
        }
    }

    companion object {
        fun factory(savedStateHandle: SavedStateHandle): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                NewUserScreenViewModel(
                    stytchClient = StytchClient,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}

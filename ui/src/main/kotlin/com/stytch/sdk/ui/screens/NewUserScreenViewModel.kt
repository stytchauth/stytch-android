package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class NewUserUiState(
    val emailState: EmailState = EmailState(),
    val passwordState: PasswordState = PasswordState(),
    val showLoadingDialog: Boolean = false,
) : Parcelable

internal class NewUserScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    val stytchClient: StytchClient = StytchClient
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow("NewUserUiState", NewUserUiState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun setInitialState(email: String) {
        savedStateHandle["NewUserUiState"] = uiState.value.copy(
            emailState = EmailState(emailAddress = email),
        )
    }

    fun sendEmailMagicLink(emailMagicLinksOptions: EmailMagicLinksOptions, scope: CoroutineScope = viewModelScope) {
        val emailState = uiState.value.emailState
        savedStateHandle["NewUserUiState"] = uiState.value.copy(showLoadingDialog = true)
        val parameters = emailMagicLinksOptions.toParameters(emailState.emailAddress)
        scope.launch {
            when (val result = stytchClient.magicLinks.email.loginOrCreate(parameters = parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["NewUserUiState"] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.EMLConfirmation(EMLDetails(parameters), isReturningUser = false),
                        ),
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle["NewUserUiState"] = uiState.value.copy(showLoadingDialog = false)
                    savedStateHandle["NewUserUiState"] = uiState.value.copy(
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
        savedStateHandle["NewUserUiState"] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            val parameters = otpOptions.toEmailOtpParameters(emailState.emailAddress)
            when (val result = stytchClient.otps.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["NewUserUiState"] = uiState.value.copy(showLoadingDialog = false)
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
                    savedStateHandle["NewUserUiState"] = uiState.value.copy(
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
        savedStateHandle["NewUserUiState"] = uiState.value.copy(
            emailState = uiState.value.emailState.copy(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress(),
            ),
        )
    }

    fun onPasswordChanged(password: String, scope: CoroutineScope = viewModelScope) {
        savedStateHandle["NewUserUiState"] = uiState.value.copy(
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
                    savedStateHandle["NewUserUiState"] = uiState.value.copy(
                        passwordState = uiState.value.passwordState.copy(
                            breachedPassword = result.value.breachedPassword,
                            feedback = result.value.feedback,
                            score = result.value.score,
                            validPassword = result.value.validPassword,
                        ),
                    )
                }

                is StytchResult.Error -> {
                    savedStateHandle["NewUserUiState"] = uiState.value.copy(
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
        savedStateHandle["NewUserUiState"] = uiState.value.copy(showLoadingDialog = true)
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
                    savedStateHandle["NewUserUiState"] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(EventState.Authenticated(result))
                }
                is StytchResult.Error -> {
                    savedStateHandle["NewUserUiState"] = uiState.value.copy(
                        showLoadingDialog = false,
                        passwordState = uiState.value.passwordState.copy(
                            errorMessage = result.exception.message, // TODO
                        ),
                    )
                }
            }
        }
    }
}

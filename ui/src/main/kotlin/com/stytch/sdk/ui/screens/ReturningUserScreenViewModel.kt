package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.EmailState
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.data.PasswordOptions
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.data.PasswordResetType
import com.stytch.sdk.ui.data.PasswordState
import com.stytch.sdk.ui.data.SessionOptions
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.utils.isValidEmailAddress
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ReturningUserUiState(
    val emailState: EmailState = EmailState(),
    val passwordState: PasswordState = PasswordState(),
    val showLoadingDialog: Boolean = false,
    val genericErrorMessage: String? = null,
) : Parcelable

internal class ReturningUserScreenViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow("ReturningUserUiState", ReturningUserUiState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun initializeState(emailAddress: String) {
        savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
            emailState = EmailState(emailAddress = emailAddress, validEmail = true),
        )
    }

    fun onEmailAddressChanged(emailAddress: String) {
        savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
            emailState = uiState.value.emailState.copy(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress(),
            ),
        )
    }

    fun onPasswordChanged(password: String) {
        savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
            passwordState = uiState.value.passwordState.copy(
                password = password,
                validPassword = true, // always valid for login
            ),
        )
    }

    fun authenticate(
        sessionOptions: SessionOptions,
        passwordOptions: PasswordOptions,
    ) {
        savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        viewModelScope.launch {
            val parameters = Passwords.AuthParameters(
                email = uiState.value.emailState.emailAddress,
                password = uiState.value.passwordState.password,
                sessionDurationMinutes = sessionOptions.sessionDurationMinutes,
            )
            when (val result = StytchClient.passwords.authenticate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["ReturningUserUiState"] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(EventState.Authenticated(result))
                }
                is StytchResult.Error -> {
                    when (val exception = result.exception) {
                        is StytchAPIError -> {
                            if (exception.errorType.contains("reset_password")) {
                               sendPasswordResetAndNavigateAppropriately(
                                   email = uiState.value.emailState.emailAddress,
                                   passwordOptions = passwordOptions,
                               )
                            } else {
                                savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
                                    showLoadingDialog = false,
                                    genericErrorMessage = result.exception.message, // TODO
                                )
                            }
                        }
                        else -> {
                            savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
                                showLoadingDialog = false,
                                genericErrorMessage = result.exception.message, // TODO
                            )
                        }
                    }
                }
            }
        }
    }

    private fun sendPasswordResetAndNavigateAppropriately(
        email: String,
        passwordOptions: PasswordOptions,
    ) {
        viewModelScope.launch {
            // send reset password request and nav appropriately
            val parameters = Passwords.ResetByEmailStartParameters(
                email = email,
                loginRedirectUrl = passwordOptions.loginRedirectURL,
                loginExpirationMinutes = passwordOptions.loginExpirationMinutes,
                resetPasswordRedirectUrl = passwordOptions.resetPasswordRedirectURL,
                resetPasswordExpirationMinutes = passwordOptions.resetPasswordExpirationMinutes,
                resetPasswordTemplateId = passwordOptions.resetPasswordTemplateId,
            )
            when (val result = StytchClient.passwords.resetByEmailStart(parameters)) {
                is StytchResult.Success -> {
                    _eventFlow.emit(
                        EventState.NavigationRequested(NavigationRoute.PasswordResetSent(
                            details = PasswordResetDetails(
                                parameters = parameters,
                                resetType = PasswordResetType.DEDUPE
                            )
                        ))
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
                        showLoadingDialog = false,
                        genericErrorMessage = result.exception.message, // TODO
                    )
                }
            }
        }
    }

    fun sendEML(emailMagicLinksOptions: EmailMagicLinksOptions) {
        savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        viewModelScope.launch {
            val parameters = emailMagicLinksOptions.toParameters(uiState.value.emailState.emailAddress)
            when (val result = StytchClient.magicLinks.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["ReturningUserUiState"] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.EMLConfirmation(
                                details = EMLDetails(parameters),
                                isReturningUser = true,
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
                    showLoadingDialog = false,
                    genericErrorMessage = result.exception.message, // TODO
                )
            }
        }
    }

    fun sendEmailOTP(otpOptions: OTPOptions) {
        savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        viewModelScope.launch {
            val parameters = otpOptions.toEmailOtpParameters(uiState.value.emailState.emailAddress)
            when (val result = StytchClient.otps.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["ReturningUserUiState"] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.OTPConfirmation(
                                details = OTPDetails.EmailOTP(parameters, methodId = result.value.methodId),
                                isReturningUser = true,
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
                    showLoadingDialog = false,
                    genericErrorMessage = result.exception.message, // TODO
                )
            }
        }
    }

    fun onForgotPasswordClicked(passwordOptions: PasswordOptions) {
        savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        viewModelScope.launch {
            val parameters = passwordOptions.toResetByEmailStartParameters(uiState.value.emailState.emailAddress)
            when (val result = StytchClient.passwords.resetByEmailStart(parameters = parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["ReturningUserUiState"] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.PasswordResetSent(
                                PasswordResetDetails(parameters, PasswordResetType.FORGOT_PASSWORD),
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle["ReturningUserUiState"] = uiState.value.copy(
                        showLoadingDialog = false,
                        genericErrorMessage = result.exception.message, // TODO
                    )
                }
            }
        }
    }
}

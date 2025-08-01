package com.stytch.sdk.ui.b2c.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchAPIErrorType
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
import com.stytch.sdk.ui.b2c.data.PasswordOptions
import com.stytch.sdk.ui.b2c.data.PasswordResetDetails
import com.stytch.sdk.ui.b2c.data.PasswordResetType
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.EventTypes
import com.stytch.sdk.ui.shared.data.SessionOptions
import com.stytch.sdk.ui.shared.utils.getUserFacingErrorMessageId
import com.stytch.sdk.ui.shared.utils.isValidEmailAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class ReturningUserScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun initializeState(emailAddress: String) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(
                emailState = EmailState(emailAddress = emailAddress, validEmail = true),
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

    fun onPasswordChanged(password: String) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(
                passwordState =
                    uiState.value.passwordState.copy(
                        password = password,
                        // always valid for login
                        validPassword = true,
                    ),
            )
    }

    fun authenticate(
        sessionOptions: SessionOptions,
        passwordOptions: PasswordOptions,
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
                Passwords.AuthParameters(
                    email = uiState.value.emailState.emailAddress,
                    password = uiState.value.passwordState.password,
                    sessionDurationMinutes = sessionOptions.sessionDurationMinutes,
                )
            when (val result = stytchClient.passwords.authenticate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(EventState.Authenticated(result))
                }
                is StytchResult.Error -> {
                    when (val exception = result.exception) {
                        is StytchAPIError -> {
                            if (exception.errorType == StytchAPIErrorType.RESET_PASSWORD) {
                                sendPasswordResetAndNavigateAppropriately(
                                    email = uiState.value.emailState.emailAddress,
                                    passwordOptions = passwordOptions,
                                    locale = locale,
                                )
                            } else {
                                savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                                    uiState.value.copy(
                                        showLoadingDialog = false,
                                        genericErrorMessage =
                                            GenericErrorDetails(
                                                errorText = result.exception.message,
                                                errorMessageId = result.exception.getUserFacingErrorMessageId(),
                                            ),
                                    )
                            }
                        }
                        else -> {
                            savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                                uiState.value.copy(
                                    showLoadingDialog = false,
                                    genericErrorMessage =
                                        GenericErrorDetails(
                                            errorText = result.exception.message,
                                            errorMessageId = result.exception.getUserFacingErrorMessageId(),
                                        ),
                                )
                        }
                    }
                }
            }
        }
    }

    private suspend fun sendPasswordResetAndNavigateAppropriately(
        email: String,
        passwordOptions: PasswordOptions,
        locale: Locale,
    ) {
        // send reset password request and nav appropriately
        val parameters =
            passwordOptions.toResetByEmailStartParameters(
                emailAddress = email,
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
                            details =
                                PasswordResetDetails(
                                    parameters = parameters,
                                    resetType = PasswordResetType.DEDUPE,
                                ),
                        ),
                    ),
                )
            }
            is StytchResult.Error -> {
                savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                    uiState.value.copy(
                        showLoadingDialog = false,
                        genericErrorMessage =
                            GenericErrorDetails(
                                errorText = result.exception.message,
                                errorMessageId = result.exception.getUserFacingErrorMessageId(),
                            ),
                    )
            }
        }
    }

    fun sendEML(
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
                    emailAddress = uiState.value.emailState.emailAddress,
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
                            genericErrorMessage =
                                GenericErrorDetails(
                                    errorText = result.exception.message,
                                    errorMessageId = result.exception.getUserFacingErrorMessageId(),
                                ),
                        )
            }
        }
    }

    fun sendEmailOTP(
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
            val parameters = otpOptions.toEmailOtpParameters(uiState.value.emailState.emailAddress, locale)
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
                            genericErrorMessage =
                                GenericErrorDetails(
                                    errorText = result.exception.message,
                                    errorMessageId = result.exception.getUserFacingErrorMessageId(),
                                ),
                        )
            }
        }
    }

    fun onForgotPasswordClicked(
        passwordOptions: PasswordOptions,
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
                passwordOptions.toResetByEmailStartParameters(
                    emailAddress = uiState.value.emailState.emailAddress,
                    publicToken = stytchClient.configurationManager.publicToken,
                    locale = locale,
                )
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
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.PasswordResetSent(
                                PasswordResetDetails(parameters, PasswordResetType.FORGOT_PASSWORD),
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            genericErrorMessage =
                                GenericErrorDetails(
                                    errorText = result.exception.message,
                                    errorMessageId = result.exception.getUserFacingErrorMessageId(),
                                ),
                        )
                }
            }
        }
    }

    companion object {
        fun factory(savedStateHandle: SavedStateHandle): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    ReturningUserScreenViewModel(
                        stytchClient = StytchClient,
                        savedStateHandle = savedStateHandle,
                    )
                }
            }
    }
}

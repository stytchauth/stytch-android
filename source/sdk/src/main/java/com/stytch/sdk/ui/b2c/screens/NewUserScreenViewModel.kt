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
import com.stytch.sdk.ui.b2c.data.NavigationRoute
import com.stytch.sdk.ui.b2c.data.OTPDetails
import com.stytch.sdk.ui.b2c.data.OTPOptions
import com.stytch.sdk.ui.shared.data.EventTypes
import com.stytch.sdk.ui.shared.utils.getUserFacingErrorMessageId
import com.stytch.sdk.ui.shared.utils.isValidEmailAddress
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

    fun sendEmailMagicLink(
        emailMagicLinksOptions: EmailMagicLinksOptions,
        locale: Locale,
        scope: CoroutineScope = viewModelScope,
    ) {
        val emailState = uiState.value.emailState
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        val parameters =
            emailMagicLinksOptions.toParameters(
                emailAddress = emailState.emailAddress,
                publicToken = stytchClient.configurationManager.publicToken,
                locale = locale,
            )
        scope.launch {
            when (val result = stytchClient.magicLinks.email.loginOrCreate(parameters = parameters)) {
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
                            NavigationRoute.EMLConfirmation(EMLDetails(parameters), isReturningUser = false),
                        ),
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            emailState =
                                emailState.copy(
                                    errorMessage = result.exception.message,
                                    errorMessageId = result.exception.getUserFacingErrorMessageId(),
                                ),
                        )
                }
            }
        }
    }

    fun sendEmailOTP(
        otpOptions: OTPOptions,
        locale: Locale,
        scope: CoroutineScope = viewModelScope,
    ) {
        val emailState = uiState.value.emailState
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            val parameters = otpOptions.toEmailOtpParameters(emailState.emailAddress, locale)
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
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            emailState =
                                emailState.copy(
                                    errorMessage = result.exception.message,
                                    errorMessageId = result.exception.getUserFacingErrorMessageId(),
                                ),
                        )
                }
            }
        }
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
                                    strengthPolicy = result.value.strengthPolicy,
                                ),
                        )
                }

                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            passwordState =
                                uiState.value.passwordState.copy(
                                    errorMessage = result.exception.message,
                                    errorMessageId = result.exception.getUserFacingErrorMessageId(),
                                ),
                        )
                }
            }
        }
    }

    fun createAccountWithPassword(
        sessionDurationMinutes: Int,
        scope: CoroutineScope = viewModelScope,
    ) {
        val emailState = uiState.value.emailState
        val passwordState = uiState.value.passwordState
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            when (
                val result =
                    stytchClient.passwords.create(
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
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            passwordState =
                                uiState.value.passwordState.copy(
                                    errorMessage = result.exception.message,
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
                    NewUserScreenViewModel(
                        stytchClient = StytchClient,
                        savedStateHandle = savedStateHandle,
                    )
                }
            }
    }
}

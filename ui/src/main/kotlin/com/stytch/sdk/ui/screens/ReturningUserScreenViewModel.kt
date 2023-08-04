package com.stytch.sdk.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.EmailState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.data.PasswordState
import com.stytch.sdk.ui.data.SessionOptions
import com.stytch.sdk.ui.utils.isValidEmailAddress
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class ReturningUserUiState(
    val emailState: EmailState = EmailState(),
    val passwordState: PasswordState = PasswordState(),
    val showLoadingDialog: Boolean = false,
    val genericErrorMessage: String? = null,
)

internal sealed class ReturningUserEventState {
    data class LoggedInResult(val result: StytchResult<Any>) : ReturningUserEventState()

    data class NavigationRequested(val navigationRoute: NavigationRoute) : ReturningUserEventState()
}

internal class ReturningUserScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReturningUserUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ReturningUserEventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var didInitialize = false

    fun initializeState(emailAddress: String) {
        if (didInitialize) return
        _uiState.value = _uiState.value.copy(
            emailState = EmailState(emailAddress = emailAddress, validEmail = true)
        )
        didInitialize = true
    }

    fun onEmailAddressChanged(emailAddress: String) {
        _uiState.value = _uiState.value.copy(
            emailState = _uiState.value.emailState.copy(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress()
            )
        )
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(
            passwordState = _uiState.value.passwordState.copy(
                password = password,
                validPassword = true, // always valid for login
            )
        )
    }

    fun authenticate(sessionOptions: SessionOptions) {
        _uiState.value = _uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        viewModelScope.launch {
            val parameters = Passwords.AuthParameters(
                email = _uiState.value.emailState.emailAddress,
                password = _uiState.value.passwordState.password,
                sessionDurationMinutes = sessionOptions.sessionDurationMinutes,
            )
            when (val result = StytchClient.passwords.authenticate(parameters)) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(ReturningUserEventState.LoggedInResult(result))
                }
                is StytchResult.Error -> _uiState.value = _uiState.value.copy(
                    showLoadingDialog = false,
                    genericErrorMessage = result.exception.reason.toString() // TODO
                )
            }
        }
    }

    fun sendEML(emailMagicLinksOptions: EmailMagicLinksOptions) {
        _uiState.value = _uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        viewModelScope.launch {
            val parameters = MagicLinks.EmailMagicLinks.Parameters(
                email = _uiState.value.emailState.emailAddress,
                loginMagicLinkUrl = emailMagicLinksOptions.loginRedirectURL,
                signupMagicLinkUrl = emailMagicLinksOptions.signupRedirectURL,
                loginExpirationMinutes = emailMagicLinksOptions.loginExpirationMinutes,
                signupExpirationMinutes = emailMagicLinksOptions.signupExpirationMinutes,
                loginTemplateId = emailMagicLinksOptions.loginTemplateId,
                signupTemplateId = emailMagicLinksOptions.signupTemplateId,
            )
            when (val result = StytchClient.magicLinks.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        ReturningUserEventState.NavigationRequested(
                            NavigationRoute.EMLConfirmation(
                                details = EMLDetails(parameters),
                                isReturningUser = true
                            )
                        )
                    )
                }
                is StytchResult.Error -> _uiState.value = _uiState.value.copy(
                    showLoadingDialog = false,
                    genericErrorMessage = result.exception.reason.toString() // TODO
                )
            }
        }
    }

    fun sendEmailOTP(otpOptions: OTPOptions) {
        _uiState.value = _uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        viewModelScope.launch {
            val parameters = OTP.EmailOTP.Parameters(
                email = _uiState.value.emailState.emailAddress,
                expirationMinutes = otpOptions.expirationMinutes,
                loginTemplateId = otpOptions.loginTemplateId,
                signupTemplateId = otpOptions.signupTemplateId,
            )
            when (val result = StytchClient.otps.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        ReturningUserEventState.NavigationRequested(
                            NavigationRoute.OTPConfirmation(
                                details = OTPDetails.EmailOTP(parameters, methodId = result.value.methodId),
                                isReturningUser = true
                            )
                        )
                    )
                }
                is StytchResult.Error -> _uiState.value = _uiState.value.copy(
                    showLoadingDialog = false,
                    genericErrorMessage = result.exception.reason.toString() // TODO
                )
            }
        }
    }
}

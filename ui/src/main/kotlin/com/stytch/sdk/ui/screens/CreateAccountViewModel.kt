package com.stytch.sdk.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.EmailState
import com.stytch.sdk.ui.data.NextPage
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.data.PasswordState
import com.stytch.sdk.ui.utils.isValidEmailAddress
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class CreateAccountViewModel : ViewModel() {
    private val _emailState = MutableStateFlow(EmailState())
    val emailState = _emailState.asStateFlow()

    private val _passwordState = MutableStateFlow(PasswordState())
    val passwordState = _passwordState.asStateFlow()

    private val _nextPage = MutableSharedFlow<NextPage>()
    val nextPage = _nextPage.asSharedFlow()

    private val _emailLinkSent = MutableSharedFlow<Boolean>()
    val emailLinkSent = _emailLinkSent.asSharedFlow()

    private val _passwordCreated = MutableSharedFlow<StytchResult<Any>>()
    val passwordCreated = _passwordCreated.asSharedFlow()

    fun setInitialEmailState(email: String) {
        _emailState.value = _emailState.value.copy(emailAddress = email)
    }

    fun sendEmailMagicLink(emailMagicLinksOptions: EmailMagicLinksOptions?) {
        val parameters = MagicLinks.EmailMagicLinks.Parameters(
            email = _emailState.value.emailAddress,
            loginMagicLinkUrl = emailMagicLinksOptions?.loginRedirectURL,
            signupMagicLinkUrl = emailMagicLinksOptions?.signupRedirectURL,
            loginExpirationMinutes = emailMagicLinksOptions?.loginExpirationMinutes,
            signupExpirationMinutes = emailMagicLinksOptions?.signupExpirationMinutes,
            loginTemplateId = emailMagicLinksOptions?.loginTemplateId,
            signupTemplateId = emailMagicLinksOptions?.signupTemplateId,
        )
        viewModelScope.launch {
            when (val result = StytchClient.magicLinks.email.loginOrCreate(parameters = parameters)) {
                is StytchResult.Success -> _nextPage.emit(
                    NextPage.EMLConfirmation(EMLDetails(parameters), isReturningUser = false)
                )
                is StytchResult.Error -> {
                    _emailState.value = _emailState.value.copy(
                        errorMessage = result.exception.reason.toString() // TODO
                    )
                }
            }
        }
    }

    fun sendEmailOTP(otpOptions: OTPOptions?) {
        viewModelScope.launch {
            val parameters = OTP.EmailOTP.Parameters(
                email = _emailState.value.emailAddress,
                expirationMinutes = otpOptions?.expirationMinutes ?: Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
                loginTemplateId = otpOptions?.loginTemplateId,
                signupTemplateId = otpOptions?.signupTemplateId,
            )
            when (val result = StytchClient.otps.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    _nextPage.emit(
                        NextPage.OTPConfirmation(
                            OTPDetails.EmailOTP(
                                parameters = parameters,
                                methodId = result.value.methodId
                            ),
                            isReturningUser = false,
                        )
                    )
                }
                is StytchResult.Error -> {
                    _emailState.value = _emailState.value.copy(
                        errorMessage = result.exception.reason.toString() // TODO
                    )
                }
            }
        }
    }

    fun onEmailAddressChanged(emailAddress: String) {
        _emailState.value = _emailState.value.copy(
            emailAddress = emailAddress,
            validEmail = emailAddress.isValidEmailAddress()
        )
    }

    fun onPasswordChanged(password: String) {
        _passwordState.value = _passwordState.value.copy(
            password = password,
        )
        viewModelScope.launch {
            when (
                val result = StytchClient.passwords.strengthCheck(
                    Passwords.StrengthCheckParameters(email = _emailState.value.emailAddress, password = password)
                )
            ) {
                is StytchResult.Success -> {
                    _passwordState.value = _passwordState.value.copy(
                        breachedPassword = result.value.breachedPassword,
                        feedback = result.value.feedback,
                        score = result.value.score,
                        validPassword = result.value.validPassword,
                    )
                }

                is StytchResult.Error -> {
                    _passwordState.value = _passwordState.value.copy(
                        errorMessage = result.exception.reason.toString() // TODO
                    )
                }
            }
        }
    }

    fun createPassword(sessionDurationMinutes: UInt) {
        viewModelScope.launch {
            when (
                val result = StytchClient.passwords.create(
                    Passwords.CreateParameters(
                        email = _emailState.value.emailAddress,
                        password = _passwordState.value.password,
                        sessionDurationMinutes = sessionDurationMinutes,
                    )
                )
            ) {
                is StytchResult.Success -> _passwordCreated.emit(result)
                is StytchResult.Error -> _passwordState.value = _passwordState.value.copy(
                    errorMessage = result.exception.reason.toString() // TODO
                )
            }
        }
    }
}

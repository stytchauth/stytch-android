package com.stytch.stytchexampleapp.ui.screens.login

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.stytchexampleapp.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.regex.Pattern
import javax.inject.Inject

private val PHONE_NUMBER_PATTERN = Pattern.compile("^[0-9]{10}$")
private val CONFIRMATION_CODE_PATTERN = Pattern.compile("^[0-9]{6}$")

@HiltViewModel
internal class LoginViewModel
    @Inject
    constructor() : ViewModel() {
        private val phoneNumberFlow = MutableStateFlow("")
        private val confirmationCodeFlow = MutableStateFlow("")
        private val screenStateFlow = MutableStateFlow<ScreenState>(ScreenState.Idle)
        private val methodIdFlow = MutableStateFlow("")
        private val _uiStateFlow =
            combine(
                phoneNumberFlow,
                confirmationCodeFlow,
                screenStateFlow,
                methodIdFlow,
            ) { phoneNumber, confirmationCode, screenState, methodId ->
                uiStateFlow.value.copy(
                    phoneNumber = phoneNumber,
                    phoneNumberIsValid = PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches(),
                    confirmationCode = confirmationCode,
                    confirmationCodeIsValid = CONFIRMATION_CODE_PATTERN.matcher(confirmationCode).matches(),
                    screenState = screenState,
                    methodIdExists = methodId.isNotBlank(),
                )
            }

        val uiStateFlow: StateFlow<LoginUiState> =
            _uiStateFlow.stateIn(viewModelScope, SharingStarted.Lazily, LoginUiState())

        fun setPhoneNumber(number: String) {
            phoneNumberFlow.value = number
        }

        fun setConfirmationCode(code: String) {
            confirmationCodeFlow.value = code
        }

        fun submitPhoneNumber() {
            viewModelScope.launch {
                screenStateFlow.value = ScreenState.Loading
                when (val result = StytchClient.otps.sms.send(OTP.SmsOTP.Parameters("+1${phoneNumberFlow.value}"))) {
                    is StytchResult.Success -> {
                        methodIdFlow.value = result.value.methodId
                        screenStateFlow.value = ScreenState.Idle
                    }
                    is StytchResult.Error -> {
                        methodIdFlow.value = ""
                        screenStateFlow.value = ScreenState.Error(error = result.exception)
                    }
                }
            }
        }

        fun confirmCode() {
            viewModelScope.launch {
                screenStateFlow.value = ScreenState.Loading
                screenStateFlow.value =
                    when (
                        val result =
                            StytchClient.otps.authenticate(
                                OTP.AuthParameters(
                                    token = confirmationCodeFlow.value,
                                    methodId = methodIdFlow.value,
                                ),
                            )
                    ) {
                        is StytchResult.Success -> ScreenState.Idle
                        is StytchResult.Error -> {
                            confirmationCodeFlow.value = ""
                            ScreenState.Error(error = result.exception)
                        }
                    }
            }
        }
    }

@Parcelize
internal data class LoginUiState(
    val phoneNumber: String = "",
    val phoneNumberIsValid: Boolean? = null,
    val confirmationCode: String = "",
    val confirmationCodeIsValid: Boolean? = null,
    val screenState: ScreenState = ScreenState.Idle,
    val methodIdExists: Boolean = false,
) : Parcelable

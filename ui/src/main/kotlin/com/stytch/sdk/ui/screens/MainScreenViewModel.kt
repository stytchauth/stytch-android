package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.UserType
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.consumer.userManagement.UserManagement
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_GOOGLE_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.EmailState
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OAuthOptions
import com.stytch.sdk.ui.data.OAuthProvider
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.data.PasswordOptions
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.data.PasswordResetType
import com.stytch.sdk.ui.data.PhoneNumberState
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.utils.isValidEmailAddress
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class MainScreenUiState(
    val emailState: EmailState = EmailState(),
    val phoneNumberState: PhoneNumberState = PhoneNumberState(),
    val genericErrorMessage: String? = null,
    val showLoadingDialog: Boolean = false,
) : Parcelable

internal class MainScreenViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow("MainScreenUiState", MainScreenUiState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onStartOAuthLogin(
        context: ComponentActivity,
        provider: OAuthProvider,
        productConfig: StytchProductConfig,
    ) {
        savedStateHandle["MainScreenUiState"] = uiState.value.copy(showLoadingDialog = true)
        if (provider == OAuthProvider.GOOGLE) {
            viewModelScope.launch {
                val didStartOneTap = productConfig.googleOauthOptions.clientId?.let { clientId ->
                    StytchClient.oauth.googleOneTap.start(
                        OAuth.GoogleOneTap.StartParameters(
                            context = context,
                            clientId = clientId,
                            oAuthRequestIdentifier = STYTCH_GOOGLE_OAUTH_REQUEST_ID,
                        ),
                    )
                } ?: false
                if (!didStartOneTap) {
                    // Google OneTap is unavailable, fallback to traditional OAuth
                    onStartThirdPartyOAuth(context, provider = provider, oAuthOptions = productConfig.oAuthOptions)
                }
            }
        } else {
            onStartThirdPartyOAuth(context, provider = provider, oAuthOptions = productConfig.oAuthOptions)
        }
    }

    private fun onStartThirdPartyOAuth(
        context: ComponentActivity,
        provider: OAuthProvider,
        oAuthOptions: OAuthOptions? = null,
    ) {
        val parameters = OAuth.ThirdParty.StartParameters(
            context = context,
            oAuthRequestIdentifier = STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID,
            loginRedirectUrl = oAuthOptions?.loginRedirectURL,
            signupRedirectUrl = oAuthOptions?.signupRedirectURL,
        )
        provider.handler.start(parameters)
    }

    fun onCountryCodeChanged(countryCode: String) {
        savedStateHandle["MainScreenUiState"] = uiState.value.copy(
            phoneNumberState = PhoneNumberState(
                countryCode = countryCode,
                error = null,
            ),
            genericErrorMessage = null,
        )
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        savedStateHandle["MainScreenUiState"] = uiState.value.copy(
            phoneNumberState = PhoneNumberState(
                phoneNumber = phoneNumber,
                error = null,
            ),
            genericErrorMessage = null,
        )
    }

    fun onEmailAddressChanged(emailAddress: String) {
        savedStateHandle["MainScreenUiState"] = uiState.value.copy(
            emailState = EmailState(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress(),
            ),
            genericErrorMessage = null,
        )
    }

    fun onEmailAddressSubmit(productConfig: StytchProductConfig) {
        savedStateHandle["MainScreenUiState"] = uiState.value.copy(showLoadingDialog = true)
        viewModelScope.launch {
            val emailAddress = uiState.value.emailState.emailAddress
            when (getUserType(emailAddress)) {
                UserType.NEW -> NavigationRoute.NewUser(emailAddress = emailAddress)
                UserType.PASSWORD -> NavigationRoute.ReturningUser(emailAddress = emailAddress)
                UserType.PASSWORDLESS -> {
                    if (
                        productConfig.products.contains(StytchProduct.OTP) &&
                        productConfig.otpOptions.methods.contains(OTPMethods.EMAIL)
                    ) {
                        // send Email OTP
                        sendEmailOTPForReturningUserAndGetNavigationRoute(
                            emailAddress = emailAddress,
                            otpOptions = productConfig.otpOptions,
                        )
                    } else if (productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS)) {
                        // send EML
                        sendEmailMagicLinkForReturningUserAndGetNavigationRoute(
                            emailAddress = emailAddress,
                            emailMagicLinksOptions = productConfig.emailMagicLinksOptions,
                        )
                    } else {
                        // no Email OTP or EML, so set password
                        sendResetPasswordForReturningUserAndGetNavigationRoute(
                            emailAddress = emailAddress,
                            passwordOptions = productConfig.passwordOptions,
                        )
                    }
                }
                else -> {
                    savedStateHandle["MainScreenUiState"] = uiState.value.copy(
                        showLoadingDialog = false,
                        genericErrorMessage = "Failed to get user type",
                    )
                    null
                }
            }?.let {
                _eventFlow.emit(EventState.NavigationRequested(it))
            }
            savedStateHandle["MainScreenUiState"] = uiState.value.copy(showLoadingDialog = false)
        }
    }

    suspend fun getUserType(emailAddress: String): UserType? {
        return when (val result = StytchClient.user.search(UserManagement.SearchParams(emailAddress))) {
            is StytchResult.Success -> result.value.userType
            is StytchResult.Error -> null
        }
    }

    suspend fun sendEmailMagicLinkForReturningUserAndGetNavigationRoute(
        emailAddress: String,
        emailMagicLinksOptions: EmailMagicLinksOptions,
    ): NavigationRoute? {
        val parameters = emailMagicLinksOptions.toParameters(emailAddress)
        return when (val result = StytchClient.magicLinks.email.loginOrCreate(parameters = parameters)) {
            is StytchResult.Success -> {
                NavigationRoute.EMLConfirmation(details = EMLDetails(parameters), isReturningUser = true)
            }
            is StytchResult.Error -> {
                savedStateHandle["MainScreenUiState"] = uiState.value.copy(
                    genericErrorMessage = result.exception.message
                ) // TODO
                null
            }
        }
    }

    suspend fun sendEmailOTPForReturningUserAndGetNavigationRoute(
        emailAddress: String,
        otpOptions: OTPOptions,
    ): NavigationRoute? {
        val parameters = otpOptions.toEmailOtpParameters(emailAddress)
        return when (val result = StytchClient.otps.email.loginOrCreate(parameters)) {
            is StytchResult.Success -> {
                NavigationRoute.OTPConfirmation(
                    OTPDetails.EmailOTP(
                        parameters = parameters,
                        methodId = result.value.methodId,
                    ),
                    isReturningUser = true,
                    emailAddress = emailAddress,
                )
            }
            is StytchResult.Error -> {
                savedStateHandle["MainScreenUiState"] = uiState.value.copy(
                    genericErrorMessage = result.exception.message
                ) // TODO
                null
            }
        }
    }

    suspend fun sendResetPasswordForReturningUserAndGetNavigationRoute(
        emailAddress: String,
        passwordOptions: PasswordOptions,
    ): NavigationRoute? {
        val parameters = passwordOptions.toResetByEmailStartParameters(emailAddress)
        return when (val result = StytchClient.passwords.resetByEmailStart(parameters = parameters)) {
            is StytchResult.Success -> {
                NavigationRoute.PasswordResetSent(PasswordResetDetails(parameters, PasswordResetType.NO_PASSWORD_SET))
            }
            is StytchResult.Error -> {
                savedStateHandle["MainScreenUiState"] = uiState.value.copy(
                    genericErrorMessage = result.exception.message
                ) // TODO
                null
            }
        }
    }

    fun sendSmsOTP(otpOptions: OTPOptions) {
        savedStateHandle["MainScreenUiState"] = uiState.value.copy(showLoadingDialog = true)
        viewModelScope.launch {
            val phoneNumberState = uiState.value.phoneNumberState
            val parameters = otpOptions.toSMSOtpParameters(phoneNumberState.toE164())
            when (val result = StytchClient.otps.sms.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["MainScreenUiState"] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.OTPConfirmation(
                                OTPDetails.SmsOTP(
                                    parameters = parameters,
                                    methodId = result.value.methodId,
                                ),
                                isReturningUser = false,
                            )
                        ),
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle["MainScreenUiState"] = uiState.value.copy(
                        phoneNumberState = phoneNumberState.copy(
                            error = result.exception.message, // TODO
                        ),
                        showLoadingDialog = false,
                    )
                }
            }
        }
    }

    fun sendWhatsAppOTP(otpOptions: OTPOptions) {
        savedStateHandle["MainScreenUiState"] = uiState.value.copy(showLoadingDialog = true)
        viewModelScope.launch {
            val phoneNumberState = uiState.value.phoneNumberState
            val parameters = otpOptions.toWhatsAppOtpParameters(phoneNumberState.toE164())
            when (val result = StytchClient.otps.whatsapp.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["MainScreenUiState"] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.OTPConfirmation(
                                OTPDetails.WhatsAppOTP(
                                    parameters = parameters,
                                    methodId = result.value.methodId,
                                ),
                                isReturningUser = false,
                            )
                        ),
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle["MainScreenUiState"] = uiState.value.copy(
                        phoneNumberState = phoneNumberState.copy(
                            error = result.exception.message, // TODO
                        ),
                        showLoadingDialog = false,
                    )
                }
            }
        }
    }
}

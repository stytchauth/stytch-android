package com.stytch.sdk.ui.screens

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.consumer.network.models.UserType
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.consumer.userManagement.UserManagement
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_GOOGLE_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.EmailState
import com.stytch.sdk.ui.data.NavigationState
import com.stytch.sdk.ui.data.OAuthOptions
import com.stytch.sdk.ui.data.OAuthProvider
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.data.PasswordOptions
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.data.PhoneNumberState
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.utils.isValidEmailAddress
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class UiState(
    val emailState: EmailState = EmailState(),
    val phoneNumberState: PhoneNumberState = PhoneNumberState(),
    val genericErrorMessage: String? = null,
    val showLoadingOverlay: Boolean = false,
)

internal class MainScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationFlow = MutableSharedFlow<NavigationState>()
    val navigationFlow = _navigationFlow.asSharedFlow()

    fun onStartOAuthLogin(
        context: ComponentActivity,
        provider: OAuthProvider,
        productConfig: StytchProductConfig
    ) {
        _uiState.value = _uiState.value.copy(showLoadingOverlay = true)
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
        oAuthOptions: OAuthOptions? = null
    ) {
        val parameters = OAuth.ThirdParty.StartParameters(
            context = context,
            oAuthRequestIdentifier = STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID,
            loginRedirectUrl = oAuthOptions?.loginRedirectURL,
            signupRedirectUrl = oAuthOptions?.signupRedirectURL,
        )
        when (provider) {
            OAuthProvider.APPLE -> StytchClient.oauth.apple.start(parameters)
            OAuthProvider.GOOGLE -> StytchClient.oauth.google.start(parameters)
        }
    }

    fun onCountryCodeChanged(countryCode: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumberState = PhoneNumberState(
                countryCode = countryCode,
                error = null
            ),
            genericErrorMessage = null
        )
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumberState = PhoneNumberState(
                phoneNumber = phoneNumber,
                error = null
            ),
            genericErrorMessage = null
        )
    }

    fun onEmailAddressChanged(emailAddress: String) {
        _uiState.value = _uiState.value.copy(
            emailState = EmailState(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress(),
            ),
            genericErrorMessage = null
        )
    }

    fun onEmailAddressSubmit(productConfig: StytchProductConfig) {
        _uiState.value = _uiState.value.copy(showLoadingOverlay = true)
        viewModelScope.launch {
            val emailAddress = _uiState.value.emailState.emailAddress
            when (getUserType(emailAddress)) {
                UserType.NEW -> if (
                    !productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS) && // no EML
                    !productConfig.otpOptions.methods.contains(OTPMethods.EMAIL) // no Email OTP
                ) {
                    NavigationState.NewUserPasswordOnly(emailAddress = emailAddress)
                } else {
                    NavigationState.NewUserWithEMLOrOTP(emailAddress = emailAddress)
                }
                UserType.PASSWORD -> {
                    NavigationState.ReturningUserWithPassword(emailAddress = emailAddress)
                }
                UserType.PASSWORDLESS -> {
                    if (
                        productConfig.products.contains(StytchProduct.OTP) &&
                        productConfig.otpOptions.methods.contains(OTPMethods.EMAIL)
                    ) {
                        // send Email OTP
                        sendEmailOTPForReturningUserAndGetNavigationArguments(
                            emailAddress = emailAddress,
                            otpOptions = productConfig.otpOptions,
                        )
                    } else if (productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS)) {
                        // send EML
                        sendEmailMagicLinkForReturningUserAndGetNavigationArguments(
                            emailAddress = emailAddress,
                            emailMagicLinksOptions = productConfig.emailMagicLinksOptions
                        )
                    } else {
                        // no Email OTP or EML, so set password
                        sendResetPasswordForReturningUserAndGetNavigationArguments(
                            emailAddress = emailAddress,
                            passwordOptions = productConfig.passwordOptions
                        )
                    }
                }
                else -> {
                    _uiState.value = uiState.value.copy(
                        showLoadingOverlay = false,
                        genericErrorMessage = "Failed to get user type"
                    )
                    null
                }
            }?.let {
                _navigationFlow.emit(it)
            }
            _uiState.value = _uiState.value.copy(showLoadingOverlay = false)
        }
    }

    suspend fun getUserType(emailAddress: String): UserType? {
        return when (val result = StytchClient.user.search(UserManagement.SearchParams(emailAddress))) {
            is StytchResult.Success -> result.value.userType
            is StytchResult.Error -> null
        }
    }

    suspend fun sendEmailMagicLinkForReturningUserAndGetNavigationArguments(
        emailAddress: String,
        emailMagicLinksOptions: EmailMagicLinksOptions
    ): NavigationState? {
        val parameters = MagicLinks.EmailMagicLinks.Parameters(
            email = emailAddress,
            loginMagicLinkUrl = emailMagicLinksOptions.loginRedirectURL,
            signupMagicLinkUrl = emailMagicLinksOptions.signupRedirectURL,
            loginExpirationMinutes = emailMagicLinksOptions.loginExpirationMinutes,
            signupExpirationMinutes = emailMagicLinksOptions.signupExpirationMinutes,
            loginTemplateId = emailMagicLinksOptions.loginTemplateId,
            signupTemplateId = emailMagicLinksOptions.signupTemplateId,
        )
        return when (val result = StytchClient.magicLinks.email.loginOrCreate(parameters = parameters)) {
            is StytchResult.Success -> {
                NavigationState.EMLConfirmation(details = EMLDetails(parameters), isReturningUser = true)
            }
            is StytchResult.Error -> {
                _uiState.value = _uiState.value.copy(genericErrorMessage = result.exception.reason.toString()) // TODO
                null
            }
        }
    }

    suspend fun sendEmailOTPForReturningUserAndGetNavigationArguments(
        emailAddress: String,
        otpOptions: OTPOptions
    ): NavigationState? {
        val parameters = OTP.EmailOTP.Parameters(
            email = emailAddress,
            expirationMinutes = otpOptions.expirationMinutes,
            loginTemplateId = otpOptions.loginTemplateId,
            signupTemplateId = otpOptions.signupTemplateId,
        )
        return when (val result = StytchClient.otps.email.loginOrCreate(parameters)) {
            is StytchResult.Success -> {
                NavigationState.OTPConfirmation(
                    OTPDetails.EmailOTP(
                        parameters = parameters,
                        methodId = result.value.methodId
                    ),
                    isReturningUser = true,
                )
            }
            is StytchResult.Error -> {
                _uiState.value = _uiState.value.copy(genericErrorMessage = result.exception.reason.toString()) // TODO
                null
            }
        }
    }

    suspend fun sendResetPasswordForReturningUserAndGetNavigationArguments(
        emailAddress: String,
        passwordOptions: PasswordOptions
    ): NavigationState? {
        val parameters = Passwords.ResetByEmailStartParameters(
            email = emailAddress,
            loginRedirectUrl = passwordOptions.loginRedirectURL,
            loginExpirationMinutes = passwordOptions.loginExpirationMinutes,
            resetPasswordRedirectUrl = passwordOptions.resetPasswordRedirectURL,
            resetPasswordExpirationMinutes = passwordOptions.resetPasswordExpirationMinutes,
            resetPasswordTemplateId = passwordOptions.resetPasswordTemplateId
        )
        return when (val result = StytchClient.passwords.resetByEmailStart(parameters = parameters)) {
            is StytchResult.Success -> NavigationState.PasswordResetSent(PasswordResetDetails(parameters))
            is StytchResult.Error -> {
                _uiState.value = _uiState.value.copy(genericErrorMessage = result.exception.reason.toString()) // TODO
                null
            }
        }
    }

    fun sendSmsOTP(otpOptions: OTPOptions) {
        _uiState.value = _uiState.value.copy(showLoadingOverlay = true)
        viewModelScope.launch {
            val phoneNumberState = _uiState.value.phoneNumberState
            val parameters = OTP.SmsOTP.Parameters(
                phoneNumber = phoneNumberState.toE164(),
                expirationMinutes = otpOptions.expirationMinutes,
            )
            when (val result = StytchClient.otps.sms.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(showLoadingOverlay = false)
                    _navigationFlow.emit(
                        NavigationState.OTPConfirmation(
                            OTPDetails.SmsOTP(
                                parameters = parameters,
                                methodId = result.value.methodId
                            ),
                            isReturningUser = false
                        )
                    )
                }
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        phoneNumberState = phoneNumberState.copy(
                            error = result.exception.reason.toString() // TODO
                        ),
                        showLoadingOverlay = false
                    )
                }
            }
        }
    }

    fun sendWhatsAppOTP(otpOptions: OTPOptions) {
        _uiState.value = _uiState.value.copy(showLoadingOverlay = true)
        viewModelScope.launch {
            val phoneNumberState = _uiState.value.phoneNumberState
            val parameters = OTP.WhatsAppOTP.Parameters(
                phoneNumber = phoneNumberState.toE164(),
                expirationMinutes = otpOptions.expirationMinutes,
            )
            when (val result = StytchClient.otps.whatsapp.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(showLoadingOverlay = false)
                    _navigationFlow.emit(
                        NavigationState.OTPConfirmation(
                            OTPDetails.WhatsAppOTP(
                                parameters = parameters,
                                methodId = result.value.methodId
                            ),
                            isReturningUser = false
                        )
                    )
                }
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        phoneNumberState = phoneNumberState.copy(
                            error = result.exception.reason.toString() // TODO
                        ),
                        showLoadingOverlay = false
                    )
                }
            }
        }
    }
}

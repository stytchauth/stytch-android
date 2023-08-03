package com.stytch.sdk.ui.screens

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES
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
import com.stytch.sdk.ui.data.NextPage
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

internal class MainScreenViewModel : ViewModel() {
    private val _phoneState = MutableStateFlow(PhoneNumberState())
    val phoneState = _phoneState.asStateFlow()

    private val _emailState = MutableStateFlow(EmailState())
    val emailState = _emailState.asStateFlow()

    private val _nextPage = MutableSharedFlow<NextPage>()
    val nextPage = _nextPage.asSharedFlow()

    fun onStartOAuthLogin(
        context: ComponentActivity,
        provider: OAuthProvider,
        productConfig: StytchProductConfig
    ) {
        if (provider == OAuthProvider.GOOGLE) {
            viewModelScope.launch {
                val didStartOneTap = productConfig.googleOauthOptions?.clientId?.let { clientId ->
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
        _phoneState.value = _phoneState.value.copy(
            countryCode = countryCode,
            error = null,
        )
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        _phoneState.value = _phoneState.value.copy(
            phoneNumber = phoneNumber,
            error = null,
        )
    }

    fun onEmailAddressChanged(emailAddress: String) {
        _emailState.value = _emailState.value.copy(
            emailAddress = emailAddress,
            validEmail = emailAddress.isValidEmailAddress(),
        )
    }

    fun determineNextPageFromEmailAddress(productConfig: StytchProductConfig) {
        val emailAddress = _emailState.value.emailAddress
        viewModelScope.launch {
            when (
                val result = StytchClient.user.search(
                    UserManagement.SearchParams(
                        email = emailAddress
                    )
                )
            ) {
                is StytchResult.Success -> {
                    when (result.value.userType) {
                        UserType.NEW -> if (
                            !productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS) && // no EML
                            !productConfig.otpOptions.methods.contains(OTPMethods.EMAIL) // no Email OTP
                        ) {
                            NextPage.NewUserPasswordOnly(emailAddress = emailAddress)
                        } else {
                            NextPage.NewUserWithEMLOrOTP(emailAddress = emailAddress)
                        }
                        UserType.PASSWORD -> {
                            NextPage.ReturningUserWithPassword(emailAddress = emailAddress)
                        }
                        UserType.PASSWORDLESS -> {
                            if (
                                productConfig.products.contains(StytchProduct.OTP) &&
                                productConfig.otpOptions.methods.contains(OTPMethods.EMAIL)
                            ) {
                                // send Email OTP
                                sendEmailOTPForReturningUserAndGetPage(productConfig.otpOptions)
                            } else if (productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS)) {
                                // send EML
                                sendEmailMagicLinkForReturningUserAndGetPage(productConfig.emailMagicLinksOptions)
                            } else {
                                // no Email OTP or EML, so set password
                                sendResetPasswordForReturningUserAndGetPage(productConfig.passwordOptions)
                            }
                        }
                    }?.let {
                        _nextPage.emit(it)
                    }
                }
                is StytchResult.Error -> error(result.exception) // TODO
            }
        }
    }

    suspend fun sendEmailMagicLinkForReturningUserAndGetPage(
        emailMagicLinksOptions: EmailMagicLinksOptions?
    ): NextPage? {
        val parameters = MagicLinks.EmailMagicLinks.Parameters(
            email = _emailState.value.emailAddress,
            loginMagicLinkUrl = emailMagicLinksOptions?.loginRedirectURL,
            signupMagicLinkUrl = emailMagicLinksOptions?.signupRedirectURL,
            loginExpirationMinutes = emailMagicLinksOptions?.loginExpirationMinutes,
            signupExpirationMinutes = emailMagicLinksOptions?.signupExpirationMinutes,
            loginTemplateId = emailMagicLinksOptions?.loginTemplateId,
            signupTemplateId = emailMagicLinksOptions?.signupTemplateId,
        )
        return when (StytchClient.magicLinks.email.loginOrCreate(parameters = parameters)) {
            is StytchResult.Success -> NextPage.EMLConfirmation(EMLDetails(parameters), isReturningUser = true)
            is StytchResult.Error -> null // TODO
        }
    }

    suspend fun sendEmailOTPForReturningUserAndGetPage(otpOptions: OTPOptions?): NextPage? {
        val parameters = OTP.EmailOTP.Parameters(
            email = _emailState.value.emailAddress,
            expirationMinutes = otpOptions?.expirationMinutes ?: Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
            loginTemplateId = otpOptions?.loginTemplateId,
            signupTemplateId = otpOptions?.signupTemplateId,
        )
        return when (val result = StytchClient.otps.email.loginOrCreate(parameters)) {
            is StytchResult.Success -> {
                NextPage.OTPConfirmation(
                    OTPDetails.EmailOTP(
                        parameters = parameters,
                        methodId = result.value.methodId
                    ),
                    isReturningUser = true,
                )
            }
            is StytchResult.Error -> null // TODO
        }
    }

    suspend fun sendResetPasswordForReturningUserAndGetPage(passwordOptions: PasswordOptions): NextPage? {
        val parameters = Passwords.ResetByEmailStartParameters(
            email = _emailState.value.emailAddress,
            loginRedirectUrl = passwordOptions.loginRedirectURL,
            loginExpirationMinutes = passwordOptions.loginExpirationMinutes,
            resetPasswordRedirectUrl = passwordOptions.resetPasswordRedirectURL,
            resetPasswordExpirationMinutes = passwordOptions.resetPasswordExpirationMinutes,
            resetPasswordTemplateId = passwordOptions.resetPasswordTemplateId
        )
        return when (StytchClient.passwords.resetByEmailStart(parameters = parameters)) {
            is StytchResult.Success -> NextPage.PasswordResetSent(
                PasswordResetDetails(parameters)
            )
            is StytchResult.Error -> null // TODO
        }
    }

    fun sendSmsOTP(otpOptions: OTPOptions?) {
        viewModelScope.launch {
            val parameters = OTP.SmsOTP.Parameters(
                phoneNumber = _phoneState.value.toE164(),
                expirationMinutes = otpOptions?.expirationMinutes ?: DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
            )
            when (val result = StytchClient.otps.sms.loginOrCreate(parameters)) {
                is StytchResult.Success -> _nextPage.emit(
                    NextPage.OTPConfirmation(
                        OTPDetails.SmsOTP(
                            parameters = parameters,
                            methodId = result.value.methodId
                        ),
                        isReturningUser = false
                    )
                )
                is StytchResult.Error -> {
                    _phoneState.value = _phoneState.value.copy(
                        error = result.exception.reason.toString() // TODO
                    )
                }
            }
        }
    }

    fun sendWhatsAppOTP(otpOptions: OTPOptions?) {
        viewModelScope.launch {
            val parameters = OTP.WhatsAppOTP.Parameters(
                phoneNumber = _phoneState.value.toE164(),
                expirationMinutes = otpOptions?.expirationMinutes ?: DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
            )
            when (val result = StytchClient.otps.whatsapp.loginOrCreate(parameters)) {
                is StytchResult.Success -> _nextPage.emit(
                    NextPage.OTPConfirmation(
                        OTPDetails.WhatsAppOTP(
                            parameters = parameters,
                            methodId = result.value.methodId
                        ),
                        isReturningUser = false
                    )
                )
                is StytchResult.Error -> {
                    _phoneState.value = _phoneState.value.copy(
                        error = result.exception.reason.toString() // TODO
                    )
                }
            }
        }
    }
}

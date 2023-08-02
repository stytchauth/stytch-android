package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.stytch.sdk.common.Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.consumer.network.models.UserType
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.consumer.userManagement.UserManagement
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_GOOGLE_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.OAuthOptions
import com.stytch.sdk.ui.data.OAuthProvider
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.data.StytchProductConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

internal data class PhoneNumberState(
    val countryCode: String = "1",
    val phoneNumber: String = "",
    val error: String? = null,
) {
    fun toE164(): String {
        val phone = Phonenumber.PhoneNumber().apply {
            countryCode = this@PhoneNumberState.countryCode.toInt()
            nationalNumber = (this@PhoneNumberState.phoneNumber).toLong()
        }
        return PhoneNumberUtil.getInstance().format(phone, PhoneNumberUtil.PhoneNumberFormat.E164)
    }
}

internal data class EmailState(
    val emailAddress: String = "",
    val error: String? = null,
)

@Parcelize
internal sealed class OTPDetails : Parcelable {
    data class EmailOTP(val parameters: OTP.EmailOTP.Parameters, val methodId: String) : OTPDetails()

    data class SmsOTP(val parameters: OTP.SmsOTP.Parameters, val methodId: String) : OTPDetails()

    data class WhatsAppOTP(val parameters: OTP.WhatsAppOTP.Parameters, val methodId: String) : OTPDetails()
}

internal sealed class NextPage {
    data class OTPConfirmation(val details: OTPDetails) : NextPage()

    data class NewUserChooser(val emailAddress: String) : NextPage()

    data class NewUserCreatePassword(val emailAddress: String) : NextPage()

    data class ReturningUserNoPassword(val emailAddress: String) : NextPage()

    data class ReturningUserWithPassword(val emailAddress: String) : NextPage()
}

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
            error = null,
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
                    val user = result.value
                    val nextPage = when (user.userType) {
                        UserType.NEW -> if (
                            !productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS) && // no EML
                            productConfig.otpOptions?.methods?.contains(OTPMethods.EMAIL) != true // no Email OTP
                        ) {
                            NextPage.NewUserCreatePassword(emailAddress = emailAddress)
                        } else {
                            NextPage.NewUserChooser(emailAddress = emailAddress)
                        }
                        UserType.PASSWORD ->
                            NextPage.ReturningUserWithPassword(emailAddress = emailAddress)
                        UserType.PASSWORDLESS ->
                            NextPage.ReturningUserNoPassword(emailAddress = emailAddress)
                    }
                    _nextPage.emit(nextPage)
                }
                is StytchResult.Error -> error(result.exception) // TODO
            }
        }
    }

    fun sendEmailMagicLink(emailMagicLinksOptions: EmailMagicLinksOptions?) {
        viewModelScope.launch {
            when (
                val result = StytchClient.magicLinks.email.loginOrCreate(
                    parameters = MagicLinks.EmailMagicLinks.Parameters(
                        email = _emailState.value.emailAddress,
                        loginMagicLinkUrl = emailMagicLinksOptions?.loginRedirectURL,
                        signupMagicLinkUrl = emailMagicLinksOptions?.signupRedirectURL,
                        loginExpirationMinutes = emailMagicLinksOptions?.loginExpirationMinutes,
                        signupExpirationMinutes = emailMagicLinksOptions?.signupExpirationMinutes,
                        loginTemplateId = emailMagicLinksOptions?.loginTemplateId,
                        signupTemplateId = emailMagicLinksOptions?.signupTemplateId,
                    )
                )
            ) {
                is StytchResult.Success -> {}
                is StytchResult.Error -> {
                    _emailState.value = _emailState.value.copy(
                        error = result.exception.reason.toString()
                    )
                }
            }
        }
    }

    fun sendEmailOTP(otpOptions: OTPOptions?) {
        viewModelScope.launch {
            val parameters = OTP.EmailOTP.Parameters(
                email = _emailState.value.emailAddress,
                expirationMinutes = otpOptions?.expirationMinutes ?: DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
                loginTemplateId = otpOptions?.loginTemplateId,
                signupTemplateId = otpOptions?.signupTemplateId,
            )
            when (val result = StytchClient.otps.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> _nextPage.emit(
                    NextPage.OTPConfirmation(
                        OTPDetails.EmailOTP(
                            parameters = parameters,
                            methodId = result.value.methodId
                        )
                    )
                )
                is StytchResult.Error -> {
                    _emailState.value = _emailState.value.copy(
                        error = result.exception.reason.toString()
                    )
                }
            }
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
                        )
                    )
                )
                is StytchResult.Error -> {
                    _phoneState.value = _phoneState.value.copy(
                        error = result.exception.reason.toString()
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
                        )
                    )
                )
                is StytchResult.Error -> {
                    _phoneState.value = _phoneState.value.copy(
                        error = result.exception.reason.toString()
                    )
                }
            }
        }
    }
}

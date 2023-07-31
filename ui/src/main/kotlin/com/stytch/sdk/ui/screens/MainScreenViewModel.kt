package com.stytch.sdk.ui.screens

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_GOOGLE_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.data.OAuthOptions
import com.stytch.sdk.ui.data.OAuthProvider
import com.stytch.sdk.ui.data.StytchProductConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class PhoneNumberState(
    val countryCode: String = "1",
    val phoneNumber: String = "",
    val error: String? = null
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
    val emailAddress: String? = null,
    val error: String? = null,
)

internal class MainScreenViewModel : ViewModel() {
    private val _phoneState = MutableStateFlow(PhoneNumberState())
    val phoneState = _phoneState.asStateFlow()

    private val _emailState = MutableStateFlow(EmailState())
    val emailState = _emailState.asStateFlow()

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
            error = null
        )
    }
    fun onPhoneNumberChanged(phoneNumber: String) {
        _phoneState.value = _phoneState.value.copy(
            phoneNumber = phoneNumber,
            error = null
        )
    }

    fun onEmailAddressChanged(emailAddress: String) {
        _emailState.value = _emailState.value.copy(
            emailAddress = emailAddress,
            error = null
        )
    }

    fun sendEmailMagicLink() {
        viewModelScope.launch {
            when (
                val result = StytchClient.magicLinks.email.loginOrCreate(
                    parameters = MagicLinks.EmailMagicLinks.Parameters(
                        email = _emailState.value.emailAddress ?: ""
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

    fun sendEmailOTP() {
        viewModelScope.launch {
            when (
                val result = StytchClient.otps.email.loginOrCreate(
                    parameters = OTP.EmailOTP.Parameters(
                        email = _emailState.value.emailAddress ?: ""
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

    fun sendSmsOTP() {
        viewModelScope.launch {
            when (
                val result = StytchClient.otps.sms.loginOrCreate(
                    parameters = OTP.SmsOTP.Parameters(
                        phoneNumber = _phoneState.value.toE164()
                    )
                )
            ) {
                is StytchResult.Success -> {}
                is StytchResult.Error -> {
                    _phoneState.value = _phoneState.value.copy(
                        error = result.exception.reason.toString()
                    )
                }
            }
        }
    }

    fun sendWhatsAppOTP() {
        viewModelScope.launch {
            when (
                val result = StytchClient.otps.whatsapp.loginOrCreate(
                    parameters = OTP.WhatsAppOTP.Parameters(
                        phoneNumber = _phoneState.value.toE164()
                    )
                )
            ) {
                is StytchResult.Success -> {}
                is StytchResult.Error -> {
                    _phoneState.value = _phoneState.value.copy(
                        error = result.exception.reason.toString()
                    )
                }
            }
        }
    }
}
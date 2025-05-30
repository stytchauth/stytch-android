package com.stytch.exampleapp.b2b.ui.headless.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.otp.OTP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OTPScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: OTPAction) =
        when (action) {
            is OTPAction.EmailAuthenticate -> emailAuthenticate(action.organizationId, action.emailAddress, action.code)
            is OTPAction.EmailDiscoveryAuthenticate -> emailDiscoveryAuthenticate(action.emailAddress, action.code)
            is OTPAction.EmailDiscoverySend -> emailDiscoverySend(action.emailAddress)
            is OTPAction.EmailLoginOrCreate -> emailLoginOrCreate(action.organizationId, action.emailAddress)
            is OTPAction.SMSAuthenticate -> smsAuthenticate(action.organizationId, action.memberId, action.code)
            is OTPAction.SMSSend -> smsSend(action.organizationId, action.memberId, action.phoneNumber)
        }

    private fun emailAuthenticate(
        organizationId: String,
        emailAddress: String,
        code: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.otp.email.authenticate(
                    OTP.Email.AuthenticateParameters(
                        organizationId = organizationId,
                        emailAddress = emailAddress,
                        code = code,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun emailDiscoveryAuthenticate(
        emailAddress: String,
        code: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.otp.email.discovery.authenticate(
                    OTP.Email.Discovery.AuthenticateParameters(
                        emailAddress = emailAddress,
                        code = code,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun emailDiscoverySend(emailAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.otp.email.discovery.send(
                    OTP.Email.Discovery.SendParameters(emailAddress = emailAddress),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun emailLoginOrCreate(
        organizationId: String,
        emailAddress: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.otp.email.loginOrSignup(
                    OTP.Email.LoginOrSignupParameters(
                        organizationId = organizationId,
                        emailAddress = emailAddress,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun smsAuthenticate(
        organizationId: String,
        memberId: String,
        code: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.otp.sms.authenticate(
                    OTP.SMS.AuthenticateParameters(
                        organizationId = organizationId,
                        memberId = memberId,
                        code = code,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun smsSend(
        organizationId: String,
        memberId: String,
        phoneNumber: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.otp.sms.send(
                    OTP.SMS.SendParameters(
                        organizationId = organizationId,
                        memberId = memberId,
                        mfaPhoneNumber = phoneNumber,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }
}

sealed interface OTPAction {
    data class SMSSend(
        val organizationId: String,
        val memberId: String,
        val phoneNumber: String,
    ) : OTPAction

    data class SMSAuthenticate(
        val organizationId: String,
        val memberId: String,
        val code: String,
    ) : OTPAction

    data class EmailLoginOrCreate(
        val organizationId: String,
        val emailAddress: String,
    ) : OTPAction

    data class EmailAuthenticate(
        val organizationId: String,
        val emailAddress: String,
        val code: String,
    ) : OTPAction

    data class EmailDiscoverySend(
        val emailAddress: String,
    ) : OTPAction

    data class EmailDiscoveryAuthenticate(
        val emailAddress: String,
        val code: String,
    ) : OTPAction
}

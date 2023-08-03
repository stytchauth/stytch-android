package com.stytch.sdk.ui.data

internal sealed class NextPage {
    data class OTPConfirmation(val details: OTPDetails, val isReturningUser: Boolean) : NextPage()

    data class EMLConfirmation(val details: EMLDetails, val isReturningUser: Boolean) : NextPage()

    data class NewUserWithEMLOrOTP(val emailAddress: String) : NextPage()

    data class NewUserPasswordOnly(val emailAddress: String) : NextPage()

    data class ReturningUserWithPassword(val emailAddress: String) : NextPage()
}

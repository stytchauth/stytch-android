package com.stytch.sdk.ui.data

internal sealed class NavigationState {
    data class OTPConfirmation(val details: OTPDetails, val isReturningUser: Boolean) : NavigationState()

    data class EMLConfirmation(val details: EMLDetails, val isReturningUser: Boolean) : NavigationState()

    data class NewUserWithEMLOrOTP(val emailAddress: String) : NavigationState()

    data class NewUserPasswordOnly(val emailAddress: String) : NavigationState()

    data class ReturningUserWithPassword(val emailAddress: String) : NavigationState()

    data class PasswordResetSent(val details: PasswordResetDetails) : NavigationState()
}

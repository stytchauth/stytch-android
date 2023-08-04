package com.stytch.sdk.ui.data

import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.ui.screens.EMLConfirmationScreen
import com.stytch.sdk.ui.screens.NewUserPasswordOnlyScreen
import com.stytch.sdk.ui.screens.NewUserWithEMLOrOTPScreen
import com.stytch.sdk.ui.screens.OTPConfirmationScreen
import com.stytch.sdk.ui.screens.PasswordResetSentScreen
import com.stytch.sdk.ui.screens.ReturningUserWithPasswordScreen

internal sealed class NavigationState {
    abstract fun getScreen(): AndroidScreen
    data class OTPConfirmation(
        val details: OTPDetails,
        val isReturningUser: Boolean,
        val emailAddress: String? = null,
    ) : NavigationState() {
        override fun getScreen() = OTPConfirmationScreen(
            resendParameters = details,
            isReturningUser = isReturningUser,
            emailAddress = emailAddress
        )
    }

    data class EMLConfirmation(val details: EMLDetails, val isReturningUser: Boolean) : NavigationState() {
        override fun getScreen() = EMLConfirmationScreen(
            details = details,
            isReturningUser = isReturningUser,
        )
    }

    data class NewUserWithEMLOrOTP(val emailAddress: String) : NavigationState() {
        override fun getScreen() = NewUserWithEMLOrOTPScreen(emailAddress = emailAddress)
    }

    data class NewUserPasswordOnly(val emailAddress: String) : NavigationState() {
        override fun getScreen() = NewUserPasswordOnlyScreen(emailAddress = emailAddress)
    }

    data class ReturningUserWithPassword(val emailAddress: String) : NavigationState() {
        override fun getScreen() = ReturningUserWithPasswordScreen(emailAddress = emailAddress)
    }

    data class PasswordResetSent(val details: PasswordResetDetails) : NavigationState() {
        override fun getScreen() = PasswordResetSentScreen(details = details)
    }
}

package com.stytch.sdk.ui.data

import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.ui.screens.EMLConfirmationScreen
import com.stytch.sdk.ui.screens.NewUserScreen
import com.stytch.sdk.ui.screens.OTPConfirmationScreen
import com.stytch.sdk.ui.screens.PasswordResetSentScreen
import com.stytch.sdk.ui.screens.ReturningUserScreen
import com.stytch.sdk.ui.screens.SetPasswordScreen

internal sealed class NavigationRoute {
    abstract val screen: AndroidScreen
    data class OTPConfirmation(
        val details: OTPDetails,
        val isReturningUser: Boolean,
        val emailAddress: String? = null,
    ) : NavigationRoute() {
        override val screen = OTPConfirmationScreen(
            resendParameters = details,
            isReturningUser = isReturningUser,
            emailAddress = emailAddress,
        )
    }

    data class EMLConfirmation(val details: EMLDetails, val isReturningUser: Boolean) : NavigationRoute() {
        override val screen = EMLConfirmationScreen(
            details = details,
            isReturningUser = isReturningUser,
        )
    }

    data class NewUser(val emailAddress: String) : NavigationRoute() {
        override val screen = NewUserScreen(emailAddress = emailAddress)
    }

    data class ReturningUser(val emailAddress: String) : NavigationRoute() {
        override val screen = ReturningUserScreen(emailAddress = emailAddress)
    }

    data class PasswordResetSent(val details: PasswordResetDetails) : NavigationRoute() {
        override val screen = PasswordResetSentScreen(details = details)
    }

    data class SetNewPassword(val emailAddress: String, val token: String) : NavigationRoute() {
        override val screen: AndroidScreen = SetPasswordScreen(emailAddress = emailAddress, token = token)
    }
}

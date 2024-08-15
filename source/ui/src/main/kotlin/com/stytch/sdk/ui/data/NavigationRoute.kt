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
        override val screen: OTPConfirmationScreen =
            OTPConfirmationScreen(
                resendParameters = details,
                isReturningUser = isReturningUser,
                emailAddress = emailAddress,
            )
    }

    data class EMLConfirmation(val details: EMLDetails, val isReturningUser: Boolean) : NavigationRoute() {
        override val screen: EMLConfirmationScreen =
            EMLConfirmationScreen(
                details = details,
                isReturningUser = isReturningUser,
            )
    }

    object NewUser : NavigationRoute() {
        override val screen: NewUserScreen = NewUserScreen
    }

    object ReturningUser : NavigationRoute() {
        override val screen: ReturningUserScreen = ReturningUserScreen
    }

    data class PasswordResetSent(val details: PasswordResetDetails) : NavigationRoute() {
        override val screen: PasswordResetSentScreen = PasswordResetSentScreen(details = details)
    }

    data class SetNewPassword(val token: String) : NavigationRoute() {
        override val screen: SetPasswordScreen = SetPasswordScreen(token = token)
    }
}

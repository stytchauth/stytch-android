package com.stytch.sdk.ui.data

import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.ui.screens.EMLConfirmationScreen
import com.stytch.sdk.ui.screens.NewUserScreen
import com.stytch.sdk.ui.screens.OTPConfirmationScreen
import com.stytch.sdk.ui.screens.PasswordResetSentScreen
import com.stytch.sdk.ui.screens.ReturningUserScreen

internal sealed class NavigationRoute {
    abstract fun getScreen(): AndroidScreen
    data class OTPConfirmation(
        val details: OTPDetails,
        val isReturningUser: Boolean,
        val emailAddress: String? = null,
    ) : NavigationRoute() {
        override fun getScreen() = OTPConfirmationScreen(
            resendParameters = details,
            isReturningUser = isReturningUser,
            emailAddress = emailAddress
        )
    }

    data class EMLConfirmation(val details: EMLDetails, val isReturningUser: Boolean) : NavigationRoute() {
        override fun getScreen() = EMLConfirmationScreen(
            details = details,
            isReturningUser = isReturningUser,
        )
    }

    data class NewUser(val emailAddress: String) : NavigationRoute() {
        override fun getScreen() = NewUserScreen(emailAddress = emailAddress)
    }

    data class ReturningUser(val emailAddress: String) : NavigationRoute() {
        override fun getScreen() = ReturningUserScreen(emailAddress = emailAddress)
    }

    data class PasswordResetSent(val details: PasswordResetDetails) : NavigationRoute() {
        override fun getScreen() = PasswordResetSentScreen(details = details)
    }
}

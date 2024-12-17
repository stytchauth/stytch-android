package com.stytch.sdk.ui.b2c.data

import com.stytch.sdk.ui.b2c.screens.EMLConfirmationScreen
import com.stytch.sdk.ui.b2c.screens.NewUserScreen
import com.stytch.sdk.ui.b2c.screens.OTPConfirmationScreen
import com.stytch.sdk.ui.b2c.screens.PasswordResetSentScreen
import com.stytch.sdk.ui.b2c.screens.ReturningUserScreen
import com.stytch.sdk.ui.b2c.screens.SetPasswordScreen
import io.mockk.mockk
import org.junit.Test

internal class NavigationRouteTest {
    @Test
    fun `OTPConfirmation returns correct screen`() {
        val screen = NavigationRoute.OTPConfirmation(mockk(), true, null)
        assert(screen.screen is OTPConfirmationScreen)
    }

    @Test
    fun `EMLConfirmation returns correct screen`() {
        val screen = NavigationRoute.EMLConfirmation(mockk(), true)
        assert(screen.screen is EMLConfirmationScreen)
    }

    @Test
    fun `NewUser returns correct screen`() {
        val screen = NavigationRoute.NewUser
        assert(screen.screen is NewUserScreen)
    }

    @Test
    fun `ReturningUser returns correct screen`() {
        val screen = NavigationRoute.ReturningUser
        assert(screen.screen is ReturningUserScreen)
    }

    @Test
    fun `PasswordResetSent returns correct screen`() {
        val screen = NavigationRoute.PasswordResetSent(mockk())
        assert(screen.screen is PasswordResetSentScreen)
    }

    @Test
    fun `SetNewPassword returns correct screen`() {
        val screen = NavigationRoute.SetNewPassword("")
        assert(screen.screen is SetPasswordScreen)
    }
}

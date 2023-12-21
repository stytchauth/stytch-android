package com.stytch.sdk.ui.tests

import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.robots.mainScreenRobot
import org.junit.Test

internal class MainScreenTest: BaseAndroidComposeTest() {
    override fun provideTestInstance() = this

    @Test
    fun defaultUIRendersAsExpected() {
        mainScreenRobot {
            headerIsVisible(shouldBeVisible = true)
            socialLoginButtonsAreVisible(shouldBeVisible = false)
            expectedTabsAreVisible(email = true, text = false, whatsapp = false)
            emailInputIsVisible(shouldBeVisible = true)
            emailErrorIsVisible(shouldBeVisible = false)
            watermarkIsVisible(shouldBeVisible = true)
        }
    }
}

package com.stytch.sdk.ui.b2c.tests

import com.stytch.sdk.ui.b2c.BaseAndroidComposeTest
import com.stytch.sdk.ui.b2c.data.DEFAULT_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.b2c.data.REALISTIC_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.b2c.robots.mainScreenRobot
import org.junit.Test

internal class MainScreenTest : BaseAndroidComposeTest() {
    override fun provideTestInstance() = this

    @Test
    fun defaultUIRendersAsExpected() {
        mainScreenRobot {
            clearAndSetContent(DEFAULT_STYTCH_UI_CONFIG)
            headerExists(shouldBeVisible = true)
            socialLoginButtonsExist(DEFAULT_STYTCH_UI_CONFIG.productConfig.oAuthOptions.providers.size)
            expectedTabsExist(email = false, text = false, whatsapp = false)
            continueButtonIsEnabled(false)
            emailInputExists(shouldBeVisible = true)
            enterEmailAddress(email = "robot@stytch.com")
            emailErrorExists(shouldBeVisible = false)
            continueButtonIsEnabled(true)
        }
    }

    @Test
    fun realisticUIRendersAsExpected() {
        mainScreenRobot {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            headerExists(shouldBeVisible = true)
            socialLoginButtonsExist(REALISTIC_STYTCH_UI_CONFIG.productConfig.oAuthOptions.providers.size)
            expectedTabsExist(email = true, text = true, whatsapp = true)
            tapEmailTabAndAssertIsShown()
            continueButtonIsEnabled(false)
            enterEmailAddress(email = "robot@stytch.com")
            emailErrorExists(shouldBeVisible = false)
            continueButtonIsEnabled(true)
            // tapTextTabAndAssertIsShown()
            enterPhoneNumber("5555550123")
            continueButtonIsEnabled(true)
            // tapWhatsAppTabAndAssertIsShown()
            verifyPhoneNumberIs("(555) 555-0123")
            continueButtonIsEnabled(true)
        }
    }
}

package com.stytch.sdk.ui.tests

import com.stytch.sdk.ui.BaseAndroidComposeTestWithDefaultConfig
import com.stytch.sdk.ui.BaseAndroidComposeTestWithRealisticConfig
import com.stytch.sdk.ui.data.DEFAULT_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.robots.mainScreenRobot
import org.junit.Test

internal class MainScreenTestWithDefaultConfig: BaseAndroidComposeTestWithDefaultConfig() {
    override fun provideTestInstance() = this

    @Test
    fun defaultUIRendersAsExpected() {
        mainScreenRobot {
            headerExists(shouldBeVisible = true)
            socialLoginButtonsExist(DEFAULT_STYTCH_UI_CONFIG.productConfig.oAuthOptions.providers.size)
            expectedTabsExist(email = false, text = false, whatsapp = false)
            emailInputExists(shouldBeVisible = true)
            emailErrorExists(shouldBeVisible = false)
            watermarkExists(shouldBeVisible = true)
        }
    }

    @Test
    fun emailErrorIsDisplayedForInvalidEmails() {
        mainScreenRobot {
            emailErrorExists(false)
            enterEmailAddress("a bad email address")
            emailErrorExists(true)
        }
    }
}

internal class MainScreenTestWithRealisticConfig: BaseAndroidComposeTestWithRealisticConfig() {
    override fun provideTestInstance() = this

    @Test
    fun realisticUIRendersAsExpected() {
        mainScreenRobot {
            headerExists(shouldBeVisible = true)
            socialLoginButtonsExist(REALISTIC_STYTCH_UI_CONFIG.productConfig.oAuthOptions.providers.size)
            expectedTabsExist(email = true, text = true, whatsapp = true)
            emailInputExists(shouldBeVisible = true)
            emailErrorExists(shouldBeVisible = false)
            watermarkExists(shouldBeVisible = false)
        }
    }

    @Test
    fun emailErrorIsDisplayedForInvalidEmails() {
        mainScreenRobot {
            emailErrorExists(false)
            enterEmailAddress("a bad email address")
            emailErrorExists(true)
        }
    }
}


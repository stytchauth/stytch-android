package com.stytch.sdk.ui.tests

import android.content.Intent
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.stytch.sdk.ui.AuthenticationActivity
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.data.DEFAULT_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.robots.mainScreenRobot
import com.stytch.sdk.ui.utils.createAndroidIntentComposeRule
import org.junit.Rule
import org.junit.Test

internal class MainScreenTestWithDefaultConfig: BaseAndroidComposeTest() {
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<AuthenticationActivity>, AuthenticationActivity> =
        createAndroidIntentComposeRule {
            Intent(it, AuthenticationActivity::class.java).apply {
                putExtra(AuthenticationActivity.STYTCH_UI_CONFIG_KEY, DEFAULT_STYTCH_UI_CONFIG)
            }
        }

    override fun provideTestInstance() = this

    @Test
    fun defaultUIRendersAsExpected() {
        mainScreenRobot(composeTestRule) {
            headerExists(shouldBeVisible = true)
            socialLoginButtonsExist(DEFAULT_STYTCH_UI_CONFIG.productConfig.oAuthOptions.providers.size)
            expectedTabsExist(email = false, text = false, whatsapp = false)
            emailInputExists(shouldBeVisible = true)
            emailErrorExists(shouldBeVisible = false)
            watermarkExists(shouldBeVisible = true)
            enterEmailAddress(email = "a bad email address")
            emailErrorExists(shouldBeVisible = true)
            continueButtonIsEnabled(false)
            enterEmailAddress(email = "robot@stytch.com")
            emailErrorExists(shouldBeVisible = false)
            continueButtonIsEnabled(true)
        }
    }
}

internal class MainScreenTestWithRealisticConfig: BaseAndroidComposeTest() {
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<AuthenticationActivity>, AuthenticationActivity> =
        createAndroidIntentComposeRule {
            Intent(it, AuthenticationActivity::class.java).apply {
                putExtra(AuthenticationActivity.STYTCH_UI_CONFIG_KEY, REALISTIC_STYTCH_UI_CONFIG)
            }
        }

    override fun provideTestInstance() = this

    @Test
    fun realisticUIRendersAsExpected() {
        mainScreenRobot(composeTestRule) {
            headerExists(shouldBeVisible = true)
            socialLoginButtonsExist(REALISTIC_STYTCH_UI_CONFIG.productConfig.oAuthOptions.providers.size)
            expectedTabsExist(email = true, text = true, whatsapp = true)
            emailInputExists(shouldBeVisible = true)
            emailErrorExists(shouldBeVisible = false)
            watermarkExists(shouldBeVisible = false)
            enterEmailAddress(email = "a bad email address")
            emailErrorExists(shouldBeVisible = true)
            tapEmailTabAndAssertIsShown()
            tapTextTabAndAssertIsShown()
            tapWhatsAppTabAndAssertIsShown()
            tapEmailTabAndAssertIsShown()
            continueButtonIsEnabled(false)
            enterEmailAddress(email = "robot@stytch.com")
            emailErrorExists(shouldBeVisible = false)
            continueButtonIsEnabled(true)
        }
    }
}


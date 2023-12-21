package com.stytch.sdk.ui.robots

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.stytch.sdk.ui.AuthenticationActivity
import com.stytch.sdk.ui.BaseAndroidComposeTestWithDefaultConfig
import com.stytch.sdk.ui.BaseAndroidComposeTestWithRealisticConfig
import com.stytch.sdk.ui.R

internal fun BaseAndroidComposeTestWithDefaultConfig.mainScreenRobot(
    func: MainScreenRobot.() -> Unit
) = MainScreenRobot(this.composeTestRule).apply(func)

internal fun BaseAndroidComposeTestWithRealisticConfig.mainScreenRobot(
    func: MainScreenRobot.() -> Unit
) = MainScreenRobot( this.composeTestRule).apply(func)

internal class MainScreenRobot(
    private val testRule: AndroidComposeTestRule<ActivityScenarioRule<AuthenticationActivity>, AuthenticationActivity>
): BaseRobotScreen(testRule) {
    private val header by lazy {
        testRule.onNode(hasText(getString(R.string.sign_up_or_login)))
    }

    private val socialLoginButtons by lazy {
        testRule.onAllNodes(hasContentDescription(getString(R.string.semantics_oauth_button)))
    }

    private val emailTab by lazy {
        testRule.onNode(
            hasContentDescription(getString(R.string.semantics_tabs))
                .and(hasAnyDescendant(hasText(getString(R.string.email))))
        )
    }

    private val textTab by lazy {
        testRule.onNode(
            hasContentDescription(getString(R.string.semantics_tabs))
                .and(hasAnyDescendant(hasText(getString(R.string.text))))
        )
    }

    private val whatsappTab by lazy {
        testRule.onNode(
            hasContentDescription(getString(R.string.semantics_tabs))
                .and(hasAnyDescendant(hasText(getString(R.string.whatsapp))))
        )
    }

    private val emailInput by lazy {
        testRule.onNode(hasContentDescription(getString(R.string.semantics_email_input)))
    }

    private val emailError by lazy {
        testRule.onNode(hasContentDescription(getString(R.string.semantics_email_error)))
    }

    private val watermark by lazy {
        testRule.onNode(hasContentDescription(getString(R.string.powered_by_stytch)))
    }

    private fun nodeExists(node: SemanticsNodeInteraction, shouldBeVisible: Boolean) {
        if (shouldBeVisible) node.assertExists() else node.assertDoesNotExist()
    }

    fun headerExists(shouldBeVisible: Boolean) = nodeExists(header, shouldBeVisible)

    fun socialLoginButtonsExist(count: Int) = socialLoginButtons.assertCountEquals(count)

    fun expectedTabsExist(email: Boolean, text: Boolean, whatsapp: Boolean) {
        if (email) nodeExists(emailTab, true)
        if (text) nodeExists(textTab, true)
        if (whatsapp) nodeExists(whatsappTab, true)
    }

    fun emailInputExists(shouldBeVisible: Boolean) = nodeExists(emailInput, shouldBeVisible)

    fun enterEmailAddress(email: String) {
        emailInput.performTextInput(email)
    }

    fun emailErrorExists(shouldBeVisible: Boolean) = nodeExists(emailError, shouldBeVisible)

    fun watermarkExists(shouldBeVisible: Boolean) = nodeExists(watermark, shouldBeVisible )
}

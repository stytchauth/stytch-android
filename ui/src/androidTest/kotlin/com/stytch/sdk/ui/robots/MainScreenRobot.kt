package com.stytch.sdk.ui.robots

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.stytch.sdk.ui.AuthenticationActivity
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.R

internal fun BaseAndroidComposeTest.mainScreenRobot(
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<AuthenticationActivity>, AuthenticationActivity>,
    func: MainScreenRobot.() -> Unit
) = MainScreenRobot(composeTestRule).apply(func)

internal class MainScreenRobot(
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<AuthenticationActivity>, AuthenticationActivity>
): BaseRobotScreen(composeTestRule) {
    private val header by lazy {
        composeTestRule.onNode(hasText(getString(R.string.sign_up_or_login)))
    }

    private val socialLoginButtons by lazy {
        composeTestRule.onAllNodes(hasContentDescription(getString(R.string.semantics_oauth_button)))
    }

    private val emailTab by lazy {
        composeTestRule.onNode(
            hasText(getString(R.string.email)).and(
                hasAnyAncestor(hasContentDescription(getString(R.string.semantics_tabs)))
            )
        )
    }

    private val textTab by lazy {
        composeTestRule.onNode(
            hasText(getString(R.string.text)).and(
                hasAnyAncestor(hasContentDescription(getString(R.string.semantics_tabs)))
            )
        )
    }

    private val whatsappTab by lazy {
        composeTestRule.onNode(
            hasText(getString(R.string.whatsapp)).and(
                hasAnyAncestor(hasContentDescription(getString(R.string.semantics_tabs)))
            )
        )
    }

    private val emailInput by lazy {
        composeTestRule.onNode(hasContentDescription(getString(R.string.semantics_email_input)))
    }

    private val emailError by lazy {
        composeTestRule.onNode(hasContentDescription(getString(R.string.semantics_email_error)))
    }

    private val watermark by lazy {
        composeTestRule.onNode(hasContentDescription(getString(R.string.powered_by_stytch)))
    }

    private val phoneInput by lazy {
        composeTestRule.onNode(hasContentDescription(getString(R.string.semantics_phone_input)))
    }

    private val continueButton by lazy {
        composeTestRule.onNode(hasText(getString(R.string.button_continue)))
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

    fun watermarkExists(shouldBeVisible: Boolean) = nodeExists(watermark, shouldBeVisible)

    fun tapEmailTabAndAssertIsShown() {
        emailTab.performClick().assertIsSelected()
        emailInput.assertExists()
    }

    fun tapTextTabAndAssertIsShown() {
        textTab.performClick().assertIsSelected()
        phoneInput.assertExists()
    }

    fun tapWhatsAppTabAndAssertIsShown() {
        whatsappTab.performClick().assertIsSelected()
        phoneInput.assertExists()
    }

    fun continueButtonIsEnabled(enabled: Boolean) = if (enabled) {
        continueButton.assertIsEnabled()
    } else {
        continueButton.assertIsNotEnabled()
    }
}

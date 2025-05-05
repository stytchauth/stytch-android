package com.stytch.sdk.ui.b2c.robots

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.BaseAndroidComposeTest
import com.stytch.sdk.ui.b2c.screens.MainScreen

internal fun BaseAndroidComposeTest.mainScreenRobot(func: MainScreenRobot.() -> Unit) =
    MainScreenRobot(this).apply(func)

internal class MainScreenRobot(
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(baseAndroidComposeTest.composeTestRule, MainScreen) {
    private val header by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_main_screen_header))
    }

    private val socialLoginButtons by lazy {
        composeTestRule.onAllNodesWithContentDescription(getString(R.string.stytch_b2c_semantics_oauth_button))
    }

    private val emailTab by lazy {
        composeTestRule.onNode(
            hasText(getString(R.string.stytch_email_label)).and(
                hasAnyAncestor(hasContentDescription(getString(R.string.stytch_b2c_semantics_tabs))),
            ),
        )
    }

    private val textTab by lazy {
        composeTestRule.onNode(
            hasText(getString(R.string.stytch_b2c_sms_tab_title)).and(
                hasAnyAncestor(hasContentDescription(getString(R.string.stytch_b2c_semantics_tabs))),
            ),
        )
    }

    private val whatsappTab by lazy {
        composeTestRule.onNode(
            hasText(getString(R.string.stytch_b2c_whatsapp_tab_title)).and(
                hasAnyAncestor(hasContentDescription(getString(R.string.stytch_b2c_semantics_tabs))),
            ),
        )
    }

    private val emailInput by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.stytch_semantics_email_input))
    }

    private val emailError by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.stytch_semantics_email_error))
    }

    private val phoneInput by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.stytch_semantics_phone_input))
    }

    private val continueButton by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_continue_button_text))
    }

    private fun nodeExists(
        node: SemanticsNodeInteraction,
        shouldBeVisible: Boolean,
    ) {
        if (shouldBeVisible) node.assertExists() else node.assertDoesNotExist()
    }

    fun headerExists(shouldBeVisible: Boolean) = nodeExists(header, shouldBeVisible)

    fun socialLoginButtonsExist(count: Int) = socialLoginButtons.assertCountEquals(count)

    fun expectedTabsExist(
        email: Boolean,
        text: Boolean,
        whatsapp: Boolean,
    ) {
        if (email) nodeExists(emailTab, true)
        if (text) nodeExists(textTab, true)
        if (whatsapp) nodeExists(whatsappTab, true)
    }

    fun emailInputExists(shouldBeVisible: Boolean) = nodeExists(emailInput, shouldBeVisible)

    fun enterEmailAddress(email: String) {
        emailInput.performTextInput(email)
    }

    fun emailErrorExists(shouldBeVisible: Boolean) = nodeExists(emailError, shouldBeVisible)

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

    fun continueButtonIsEnabled(enabled: Boolean) =
        if (enabled) {
            continueButton.assertIsEnabled()
        } else {
            continueButton.assertIsNotEnabled()
        }

    fun enterPhoneNumber(number: String) {
        phoneInput.performTextInput(number)
    }

    fun verifyPhoneNumberIs(number: String) {
        phoneInput.assertTextEquals(number)
    }
}

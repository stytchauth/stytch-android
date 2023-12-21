package com.stytch.sdk.ui.robots

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.performTextInput
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.R


internal fun BaseAndroidComposeTest.mainScreenRobot(
    func: MainScreenRobot.() -> Unit
) = MainScreenRobot(this).apply(func)

internal class MainScreenRobot(
    baseAndroidComposeTest: BaseAndroidComposeTest
): BaseRobotScreen(baseAndroidComposeTest.composeTestRule)  {
    private val header by lazy {
        composeTestRule.onNode(hasText(getString(R.string.sign_up_or_login)))
    }

    private val socialLoginButtons by lazy {
        composeTestRule.onAllNodes(hasContentDescription(getString(R.string.semantics_oauth_button)))
    }

    private val emailTab by lazy {
        composeTestRule.onNode(hasText(getString(R.string.email)))
    }

    private val textTab by lazy {
        composeTestRule.onNode(hasText(getString(R.string.text)))
    }

    private val whatsappTab by lazy {
        composeTestRule.onNode(hasText(getString(R.string.whatsapp)))
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

    private fun nodeIsVisible(node: SemanticsNodeInteraction, shouldBeVisible: Boolean) {
        if (shouldBeVisible) node.assertIsDisplayed() else node.assertDoesNotExist()
    }

    fun headerIsVisible(shouldBeVisible: Boolean) = nodeIsVisible(header, shouldBeVisible)

    fun socialLoginButtonsAreVisible(shouldBeVisible: Boolean) = if (shouldBeVisible) {
        socialLoginButtons.assertAny(isEnabled())
    } else {
        socialLoginButtons.assertCountEquals(0)
    }

    fun expectedTabsAreVisible(email: Boolean, text: Boolean, whatsapp: Boolean) {
        if (email) nodeIsVisible(emailTab, true)
        if (text) nodeIsVisible(textTab, true)
        if (whatsapp) nodeIsVisible(whatsappTab, true)
    }

    fun emailInputIsVisible(shouldBeVisible: Boolean) = nodeIsVisible(emailInput, shouldBeVisible)

    fun enterEmailAddress(email: String) {
        emailInput.performTextInput(email)
    }

    fun emailErrorIsVisible(shouldBeVisible: Boolean) = nodeIsVisible(emailError, shouldBeVisible)

    fun watermarkIsVisible(shouldBeVisible: Boolean) = nodeIsVisible(watermark, shouldBeVisible )
}

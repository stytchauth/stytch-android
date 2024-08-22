package com.stytch.sdk.ui.robots

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.stytch.sdk.R
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.screens.EMLConfirmationScreen

internal fun BaseAndroidComposeTest.emlConfirmationScreenRobot(
    emlDetails: EMLDetails,
    isReturningUser: Boolean,
    func: EMLConfirmationScreenRobot.() -> Unit,
) = EMLConfirmationScreenRobot(emlDetails, isReturningUser, this).apply(func)

internal class EMLConfirmationScreenRobot(
    emlDetails: EMLDetails,
    isReturningUser: Boolean,
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(baseAndroidComposeTest.composeTestRule, EMLConfirmationScreen(emlDetails, isReturningUser)) {
    private val pageTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.check_your_email), substring = true)
    }

    private val loginLinkText by lazy {
        composeTestRule.onNodeWithText(getString(R.string.login_link_sent), substring = true)
    }

    private val resendLinkText by lazy {
        composeTestRule.onNodeWithText(getString(R.string.didnt_get_it), substring = true)
    }

    private val passwordButton by lazy {
        composeTestRule.onNodeWithText(getString(R.string.create_password_instead), substring = true)
    }

    fun pageTitleExists() = pageTitle.assertExists()

    fun loginLinkTextExists() = loginLinkText.assertExists()

    fun resendLinkTextExists() = resendLinkText.assertExists()

    fun passwordButtonExists(shouldExist: Boolean) {
        if (shouldExist) {
            passwordButton.assertExists()
        } else {
            passwordButton.assertDoesNotExist()
        }
    }

    fun clickResendLink() {
        resendLinkText.performClick()
    }
}

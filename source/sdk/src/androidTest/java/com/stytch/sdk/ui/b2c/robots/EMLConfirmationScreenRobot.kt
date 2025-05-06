package com.stytch.sdk.ui.b2c.robots

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.BaseAndroidComposeTest
import com.stytch.sdk.ui.b2c.data.EMLDetails
import com.stytch.sdk.ui.b2c.screens.EMLConfirmationScreen

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
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_check_your_email), substring = true)
    }

    private val loginLinkText by lazy {
        composeTestRule.onNodeWithText(
            getString(R.string.stytch_b2c_login_link_sent_to_create_password, "robot@stytch.com"),
            substring = true,
        )
    }

    private val resendLinkText by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_didnt_get_it_resend_link), substring = true)
    }

    private val passwordButton by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_create_password_instead), substring = true)
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

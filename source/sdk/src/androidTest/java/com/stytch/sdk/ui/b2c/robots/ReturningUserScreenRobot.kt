package com.stytch.sdk.ui.b2c.robots

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.BaseAndroidComposeTest
import com.stytch.sdk.ui.b2c.screens.ReturningUserScreen

internal fun BaseAndroidComposeTest.returningUserScreenRobot(func: ReturningUserScreenRobot.() -> Unit) =
    ReturningUserScreenRobot(this).apply(func)

internal class ReturningUserScreenRobot(
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(baseAndroidComposeTest.composeTestRule, ReturningUserScreen) {
    private val pageTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_log_in))
    }

    private val emailAndPasswordEntry by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.stytch_semantics_email_password_entry))
    }

    private val forgotPassword by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_forgot_password))
    }

    private val stytchButtonEML by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_email_me_a_login_link))
    }

    private val stytchButtonOTP by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_email_me_a_login_code))
    }

    fun pageTitleExists() = pageTitle.assertExists()

    fun emailPasswordEntryExists() = emailAndPasswordEntry.assertExists()

    fun forgotPasswordExists() = forgotPassword.assertExists()

    fun emlButtonExists(shouldExist: Boolean) {
        if (shouldExist) {
            stytchButtonEML.assertExists()
        } else {
            stytchButtonEML.assertDoesNotExist()
        }
    }

    fun otpButtonExists(shouldExist: Boolean) {
        if (shouldExist) {
            stytchButtonOTP.assertExists()
        } else {
            stytchButtonOTP.assertDoesNotExist()
        }
    }
}

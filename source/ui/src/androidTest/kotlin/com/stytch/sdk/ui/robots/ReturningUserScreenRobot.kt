package com.stytch.sdk.ui.robots

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.screens.ReturningUserScreen

internal fun BaseAndroidComposeTest.returningUserScreenRobot(func: ReturningUserScreenRobot.() -> Unit) =
    ReturningUserScreenRobot(this).apply(func)

internal class ReturningUserScreenRobot(
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(baseAndroidComposeTest.composeTestRule, ReturningUserScreen) {
    private val pageTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.log_in))
    }

    private val emailAndPasswordEntry by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.semantics_email_password_entry))
    }

    private val forgotPassword by lazy {
        composeTestRule.onNodeWithText(getString(R.string.forgot_password))
    }

    private val stytchButtonEML by lazy {
        composeTestRule.onNodeWithText(getString(R.string.email_me_a_login_link))
    }

    private val stytchButtonOTP by lazy {
        composeTestRule.onNodeWithText(getString(R.string.email_me_a_login_code))
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

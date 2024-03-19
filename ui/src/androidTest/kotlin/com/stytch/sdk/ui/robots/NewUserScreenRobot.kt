package com.stytch.sdk.ui.robots

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.screens.NewUserScreen

internal fun BaseAndroidComposeTest.newUserScreenRobot(func: NewUserScreenRobot.() -> Unit) =
    NewUserScreenRobot(this).apply(func)

internal class NewUserScreenRobot(
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(baseAndroidComposeTest.composeTestRule, NewUserScreen) {
    private val emlOrOtpTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.choose_how))
    }

    private val passwordTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.create_account))
    }

    private val stytchButtonEML by lazy {
        composeTestRule.onNodeWithText(getString(R.string.email_me_a_login_link))
    }

    private val stytchButtonOTP by lazy {
        composeTestRule.onNodeWithText(getString(R.string.email_me_a_login_code))
    }

    private val emailAndPasswordEntry by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.semantics_email_password_entry))
    }

    fun emlOrOtpTitleExists(shouldExist: Boolean) {
        if (shouldExist) {
            emlOrOtpTitle.assertExists()
        } else {
            emlOrOtpTitle.assertDoesNotExist()
        }
    }

    fun passwordTitleExists(shouldExist: Boolean) {
        if (shouldExist) {
            passwordTitle.assertExists()
        } else {
            passwordTitle.assertDoesNotExist()
        }
    }

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

    fun emailPasswordEntryExists() = emailAndPasswordEntry.assertExists()
}

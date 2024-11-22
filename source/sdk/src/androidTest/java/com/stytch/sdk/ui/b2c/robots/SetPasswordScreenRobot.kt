package com.stytch.sdk.ui.b2c.robots

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.BaseAndroidComposeTest
import com.stytch.sdk.ui.b2c.screens.SetPasswordScreen

internal fun BaseAndroidComposeTest.setPasswordScreenRobot(func: SetPasswordScreenRobot.() -> Unit) =
    SetPasswordScreenRobot(this).apply(func)

internal class SetPasswordScreenRobot(
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(baseAndroidComposeTest.composeTestRule, SetPasswordScreen("")) {
    private val pageTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.set_new_password))
    }

    private val emailAndPasswordEntry by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.semantics_email_password_entry))
    }

    fun pageTitleExists() = pageTitle.assertExists()

    fun emailPasswordEntryExists() = emailAndPasswordEntry.assertExists()
}

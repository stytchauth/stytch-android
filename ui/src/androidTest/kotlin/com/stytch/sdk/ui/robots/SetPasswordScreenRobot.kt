package com.stytch.sdk.ui.robots

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.screens.SetPasswordScreen

internal fun BaseAndroidComposeTest.setPasswordScreenRobot(
    func: SetPasswordScreenRobot.() -> Unit
) = SetPasswordScreenRobot(this).apply(func)

internal class SetPasswordScreenRobot(
    baseAndroidComposeTest: BaseAndroidComposeTest
): BaseRobotScreen(baseAndroidComposeTest.composeTestRule, SetPasswordScreen("")) {
    private val pageTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.set_new_password))
    }

    private val emailAndPasswordEntry by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.semantics_email_password_entry))
    }

    fun pageTitleExists() = pageTitle.assertExists()

    fun emailPasswordEntryExists() = emailAndPasswordEntry.assertExists()
}
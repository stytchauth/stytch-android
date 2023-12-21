package com.stytch.sdk.ui.robots

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
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

    fun headerIsVisible(shouldBeVisible: Boolean) = if (shouldBeVisible) {
        header.assertIsDisplayed()
    } else {
        header.assertIsNotDisplayed()
    }
}

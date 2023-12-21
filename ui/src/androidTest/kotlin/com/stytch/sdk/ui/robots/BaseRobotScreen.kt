package com.stytch.sdk.ui.robots

import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.stytch.sdk.ui.AuthenticationActivity

internal abstract class BaseRobotScreen(
    protected val composeTestRule:
        AndroidComposeTestRule<ActivityScenarioRule<AuthenticationActivity>, AuthenticationActivity>
) {
    protected fun getString(@StringRes resourceId: Int) =
        composeTestRule.activity.getString(resourceId)

}
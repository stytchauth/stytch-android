package com.stytch.sdk.ui.robots

import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.ui.AuthenticationActivity
import com.stytch.sdk.ui.StytchAuthenticationApp
import com.stytch.sdk.ui.data.StytchUIConfig
import com.stytch.sdk.ui.theme.StytchTheme

internal abstract class BaseRobotScreen(
    protected val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<AuthenticationActivity>, AuthenticationActivity>,
    private val screen: AndroidScreen
) {
    protected fun getString(@StringRes resourceId: Int) =
        composeTestRule.activity.getString(resourceId)

    fun setContent(uiConfig: StytchUIConfig) {
        composeTestRule.activity.setContent {
            StytchTheme(config = uiConfig) {
                StytchAuthenticationApp(
                    bootstrapData = uiConfig.bootstrapData,
                    screen = screen,
                    productConfig = uiConfig.productConfig,
                    onInvalidConfig = { }
                )
            }
        }
    }
}
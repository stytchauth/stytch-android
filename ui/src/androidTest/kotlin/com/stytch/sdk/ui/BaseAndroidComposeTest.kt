package com.stytch.sdk.ui

import android.content.Intent
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.data.StytchStyles
import com.stytch.sdk.ui.data.StytchUIConfig
import com.stytch.sdk.ui.utils.createAndroidIntentComposeRule
import org.junit.After
import org.junit.Before
import org.junit.Rule

private val DEFAULT_STYTCH_UI_CONFIG = StytchUIConfig(
    productConfig = StytchProductConfig(),
    styles = StytchStyles(),
    bootstrapData = BootstrapData(),
    publicToken = "",
)

internal abstract class BaseAndroidComposeTest {
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<
        ActivityScenarioRule<AuthenticationActivity>,
        AuthenticationActivity
    > = createAndroidIntentComposeRule {
            Intent(it, AuthenticationActivity::class.java).apply {
                putExtra(AuthenticationActivity.STYTCH_UI_CONFIG_KEY, DEFAULT_STYTCH_UI_CONFIG)
            }
        }

    abstract fun provideTestInstance(): Any

    @Before
    open fun setUp() {
    }

    @After
    open fun tearDown() {
    }
}

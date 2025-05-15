package com.stytch.sdk.ui.b2c

import android.content.Intent
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.ui.b2c.data.DEFAULT_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.utils.createAndroidIntentComposeRule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Rule

internal abstract class BaseAndroidComposeTest {
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<AuthenticationActivity>, AuthenticationActivity> =
        createAndroidIntentComposeRule {
            MainScope().launch {
                StytchClient.configure(it)
            }
            Intent(it, AuthenticationActivity::class.java).apply {
                // we launch with a default config just so the activity doesn't auto exit.
                // We override the actual content in the robots themselves
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

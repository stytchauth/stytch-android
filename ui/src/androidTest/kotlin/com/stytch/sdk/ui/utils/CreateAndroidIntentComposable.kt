// from https://stackoverflow.com/a/69920214
package com.stytch.sdk.ui.utils

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule

/**
 * Factory method to provide Android specific implementation of createComposeRule, for a given
 * activity class type A that needs to be launched via an intent.
 *
 * @param intentFactory A lambda that provides a Context that can used to create an intent. A intent needs to be returned.
 */
internal inline fun <A : ComponentActivity> createAndroidIntentComposeRule(
    intentFactory: (context: Context) -> Intent,
): AndroidComposeTestRule<ActivityScenarioRule<A>, A> {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val intent = intentFactory(context)

    return AndroidComposeTestRule(
        activityRule = ActivityScenarioRule(intent),
        activityProvider = { scenarioRule -> scenarioRule.getActivity() },
    )
}

/**
 * Gets the activity from a scenarioRule.
 *
 * https://androidx.tech/artifacts/compose.ui/ui-test-junit4/1.0.0-alpha11-source/androidx/compose/ui/test/junit4/AndroidComposeTestRule.kt.html
 */
internal fun <A : ComponentActivity> ActivityScenarioRule<A>.getActivity(): A {
    var activity: A? = null
    scenario.onActivity {
        activity = it
    }
    return activity ?: error("Activity was not set in the ActivityScenarioRule!")
}

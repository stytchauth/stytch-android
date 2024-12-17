package com.stytch.sdk.ui.b2c

import androidx.activity.ComponentActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import org.junit.Rule
import org.junit.Test

internal class B2BAuthenticationActivityTest {
    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testExpectedErrorWhenNoUIConfiguration() {
        val result = ActivityScenario.launchActivityForResult(AuthenticationActivity::class.java).result
        assert(result.resultCode == ComponentActivity.RESULT_OK)
        result.resultData.setExtrasClassLoader(AuthenticationActivity::class.java.classLoader)
        val error =
            result.resultData.extras
                ?.getParcelable(AuthenticationActivity.STYTCH_RESULT_KEY, StytchResult.Error::class.java)
        val expected = StytchResult.Error(StytchUIInvalidConfiguration("No UI Configuration Provided"))
        assert(error == expected)
    }
}

package com.stytch.sdk.ui.robots

import com.stytch.sdk.R
import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.ui.BaseAndroidComposeTest

internal fun BaseAndroidComposeTest.stytchAuthenticationAppRobot(func: StytchAuthenticationAppRobot.() -> Unit) =
    StytchAuthenticationAppRobot(this).apply(func)

internal class StytchAuthenticationAppRobot(
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(baseAndroidComposeTest.composeTestRule, null) {
    fun isEMLAndOTPError(error: StytchUIInvalidConfiguration) {
        assert(error.message == getString(R.string.eml_and_otp_error))
    }
}

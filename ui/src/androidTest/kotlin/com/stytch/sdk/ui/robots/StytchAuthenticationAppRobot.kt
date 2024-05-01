package com.stytch.sdk.ui.robots

import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.R

internal fun BaseAndroidComposeTest.stytchAuthenticationAppRobot(func: StytchAuthenticationAppRobot.() -> Unit) =
    StytchAuthenticationAppRobot(this).apply(func)

internal class StytchAuthenticationAppRobot(
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(baseAndroidComposeTest.composeTestRule, null) {
    fun isEMLAndOTPError(error: StytchUIInvalidConfiguration) {
        assert(error.message == getString(R.string.eml_and_otp_error))
    }

    fun isMisconfiguredError(error: StytchUIInvalidConfiguration) {
        assert(error.message == getString(R.string.misconfigured_products_and_options))
    }
}

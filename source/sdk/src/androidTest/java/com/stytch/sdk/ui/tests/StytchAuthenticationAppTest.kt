package com.stytch.sdk.ui.tests

import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.data.EML_AND_OTP_ERROR_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.robots.stytchAuthenticationAppRobot
import org.junit.Test

internal class StytchAuthenticationAppTest : BaseAndroidComposeTest() {
    override fun provideTestInstance() = this

    @Test
    fun incompatibleProductsReturnsError() {
        stytchAuthenticationAppRobot {
            var error: StytchUIInvalidConfiguration? = null
            clearAndSetContent(EML_AND_OTP_ERROR_STYTCH_UI_CONFIG) {
                error = it
            }
            await()
            isEMLAndOTPError(error!!)
        }
    }
}

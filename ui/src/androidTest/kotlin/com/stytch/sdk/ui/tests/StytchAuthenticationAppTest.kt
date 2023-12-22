package com.stytch.sdk.ui.tests

import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.data.EML_AND_OTP_ERROR_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.data.NO_PASSWORD_EML_OR_OTP_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.robots.stytchAuthenticationAppRobot
import org.junit.Test

internal class StytchAuthenticationAppTest: BaseAndroidComposeTest() {
    override fun provideTestInstance() = this

    @Test
    fun incompatibleProductsReturnsError() {
        stytchAuthenticationAppRobot {
            var error: StytchUIInvalidConfiguration? = null
            setContent(EML_AND_OTP_ERROR_STYTCH_UI_CONFIG) {
                error = it
            }
            isEMLAndOTPError(error!!)
        }
    }

    @Test
    fun misconfiguredProductsAndOptionsReturnsError() {
        stytchAuthenticationAppRobot {
            var error: StytchUIInvalidConfiguration? = null
            setContent(NO_PASSWORD_EML_OR_OTP_STYTCH_UI_CONFIG) {
                error = it
            }
            isMisconfiguredError(error!!)
        }
    }
}
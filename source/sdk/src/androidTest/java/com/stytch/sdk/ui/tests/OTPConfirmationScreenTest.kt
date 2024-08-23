package com.stytch.sdk.ui.tests

import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_EOTP
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_PASSWORD_ONLY
import com.stytch.sdk.ui.robots.otpConfirmationScreenRobot
import org.junit.Test

internal class OTPConfirmationScreenTest : BaseAndroidComposeTest() {
    override fun provideTestInstance() = this

    private val resendParameters =
        OTPDetails.EmailOTP(
            methodId = "",
            parameters = OTP.EmailOTP.Parameters(email = "robot@stytch.com"),
        )

    @Test
    fun defaultDisplaysAsExpected() {
        otpConfirmationScreenRobot(
            resendParameters = resendParameters,
            isReturningUser = false,
            emailAddress = "robot@stytch.com",
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_EOTP)
            backButtonExists()
            pageTitleExists()
            passcodeSentTextExists()
            otpEntryExists()
            passwordButtonExists(false)
        }
    }

    @Test
    fun returningUserWithNoPasswordsDisplaysAsExpected() {
        otpConfirmationScreenRobot(
            resendParameters = resendParameters,
            isReturningUser = false,
            emailAddress = "robot@stytch.com",
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_EOTP)
            backButtonExists()
            pageTitleExists()
            passcodeSentTextExists()
            otpEntryExists()
            passwordButtonExists(false)
        }
    }

    @Test
    fun returningUserWithPasswordsDisplaysAsExpected() {
        otpConfirmationScreenRobot(
            resendParameters = resendParameters,
            isReturningUser = true,
            emailAddress = "robot@stytch.com",
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_PASSWORD_ONLY)
            backButtonExists()
            pageTitleExists()
            passcodeSentTextExists()
            otpEntryExists()
            passwordButtonExists(true)
        }
    }

    @Test
    fun uiUpdatesWithStateChangeAsExpected() {
        otpConfirmationScreenRobot(
            resendParameters = resendParameters,
            isReturningUser = false,
            emailAddress = "robot@stytch.com",
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            loadingDialogExists(false)
            setLoadingDialogVisible(true)
            loadingDialogExists(true)
            resendDialogExists(false)
            setResendDialogVisible(true)
            resendDialogExists(true)
        }
    }
}

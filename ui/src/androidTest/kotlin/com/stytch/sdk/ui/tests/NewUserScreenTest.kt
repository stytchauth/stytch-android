package com.stytch.sdk.ui.tests

import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.data.ApplicationUIState
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_EML
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_EOTP
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_PASSWORD_ONLY
import com.stytch.sdk.ui.robots.newUserScreenRobot
import org.junit.Test

internal class NewUserScreenTest:  BaseAndroidComposeTest() {
    override fun provideTestInstance() = this

    @Test
    fun withEMLDisplaysAsExpected() {
        newUserScreenRobot {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_EML)
            backButtonExists()
            emlOrOtpTitleExists(true)
            passwordTitleExists(false)
            emlButtonExists(true)
            otpButtonExists(false)
            emailPasswordEntryExists()
        }
    }

    @Test
    fun withEmailOTPDisplaysAsExpected() {
        newUserScreenRobot {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_EOTP)
            backButtonExists()
            emlOrOtpTitleExists(true)
            passwordTitleExists(false)
            emlButtonExists(false)
            otpButtonExists(true)
            emailPasswordEntryExists()
        }
    }

    @Test
    fun withPasswordDisplaysAsExpected() {
        newUserScreenRobot {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_PASSWORD_ONLY)
            backButtonExists()
            emlOrOtpTitleExists(false)
            passwordTitleExists(true)
            emlButtonExists(false)
            otpButtonExists(false)
            emailPasswordEntryExists()
        }
    }

    @Test
    fun loadingDialogShowsAsExpected() {
        newUserScreenRobot {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_EOTP)
            loadingDialogExists(false)
            setLoadingDialog(true)
            loadingDialogExists(true)
        }
    }
}
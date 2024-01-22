package com.stytch.sdk.ui.tests

import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_EML
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_EOTP
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_PASSWORD_ONLY
import com.stytch.sdk.ui.robots.returningUserScreenRobot
import org.junit.Test

internal class ReturningUserScreenTest:  BaseAndroidComposeTest() {
    override fun provideTestInstance() = this

    @Test
    fun withEMLDisplaysAsExpected() {
        returningUserScreenRobot {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_EML)
            backButtonExists()
            pageTitleExists()
            emailPasswordEntryExists()
            forgotPasswordExists()
            emlButtonExists(true)
            otpButtonExists(false)
        }
    }

    @Test
    fun withEmailOTPDisplaysAsExpected() {
        returningUserScreenRobot {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_EOTP)
            backButtonExists()
            pageTitleExists()
            emailPasswordEntryExists()
            forgotPasswordExists()
            emlButtonExists(false)
            otpButtonExists(true)
        }
    }

    @Test
    fun withPasswordOnlyDisplaysAsExpected() {
        returningUserScreenRobot {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_PASSWORD_ONLY)
            backButtonExists()
            pageTitleExists()
            emailPasswordEntryExists()
            forgotPasswordExists()
            emlButtonExists(false)
            otpButtonExists(false)
        }
    }

    @Test
    fun uiUpdatesWithStateChangeAsExpected() {
        returningUserScreenRobot {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            loadingDialogExists(false)
            setLoadingDialogVisible(true)
            loadingDialogExists(true)
            setGenericErrorMessage("My error message")
            genericErrorMessageExists("My error message")
        }
    }
}
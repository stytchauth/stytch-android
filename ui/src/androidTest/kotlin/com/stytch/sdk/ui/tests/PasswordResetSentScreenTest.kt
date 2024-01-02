package com.stytch.sdk.ui.tests

import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.data.PasswordResetType
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_EML
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_EOTP
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_PASSWORD_ONLY
import com.stytch.sdk.ui.robots.passwordResetSentScreenRobot
import org.junit.Test

internal class PasswordResetSentScreenTest:  BaseAndroidComposeTest() {
    override fun provideTestInstance() = this

    @Test
    fun forgotPasswordDisplaysAsExpected() {
        passwordResetSentScreenRobot(
            details = PasswordResetDetails(
                parameters = Passwords.ResetByEmailStartParameters(""),
                resetType = PasswordResetType.FORGOT_PASSWORD
            )
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            forgotPasswordTitleExists(true)
            setNewPasswordTitleExists(false)
            forgotPasswordTextExists(true)
            noPasswordTextExists(false)
            breachedPasswordTextExists(false)
            dedupePasswordTextExists(false)
            resendTextExists()
        }
    }
    @Test
    fun noPasswordDisplaysAsExpected() {
        passwordResetSentScreenRobot(
            details = PasswordResetDetails(
                parameters = Passwords.ResetByEmailStartParameters(""),
                resetType = PasswordResetType.NO_PASSWORD_SET
            )
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            forgotPasswordTitleExists(false)
            setNewPasswordTitleExists(true)
            forgotPasswordTextExists(false)
            noPasswordTextExists(true)
            breachedPasswordTextExists(false)
            dedupePasswordTextExists(false)
            resendTextExists()
        }
    }
    @Test
    fun breachedPasswordDisplaysAsExpected() {
        passwordResetSentScreenRobot(
            details = PasswordResetDetails(
                parameters = Passwords.ResetByEmailStartParameters(""),
                resetType = PasswordResetType.BREACHED
            )
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            forgotPasswordTitleExists(false)
            setNewPasswordTitleExists(true)
            forgotPasswordTextExists(false)
            noPasswordTextExists(false)
            breachedPasswordTextExists(true)
            dedupePasswordTextExists(false)
            resendTextExists()
        }
    }
    @Test
    fun dedupePasswordDisplaysAsExpected() {
        passwordResetSentScreenRobot(
            details = PasswordResetDetails(
                parameters = Passwords.ResetByEmailStartParameters(""),
                resetType = PasswordResetType.DEDUPE
            )
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            forgotPasswordTitleExists(false)
            setNewPasswordTitleExists(true)
            forgotPasswordTextExists(false)
            noPasswordTextExists(false)
            breachedPasswordTextExists(false)
            dedupePasswordTextExists(true)
            resendTextExists()
        }
    }

    @Test
    fun noEMLAndNoOTPDoesntShowButton() {
        passwordResetSentScreenRobot(
            details = PasswordResetDetails(
                parameters = Passwords.ResetByEmailStartParameters(""),
                resetType = PasswordResetType.FORGOT_PASSWORD
            )
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_PASSWORD_ONLY)
            emlButtonExists(false)
            otpButtonExists(false)
        }
    }

    @Test
    fun emlShowsCorrectButton() {
        passwordResetSentScreenRobot(
            details = PasswordResetDetails(
                parameters = Passwords.ResetByEmailStartParameters(""),
                resetType = PasswordResetType.FORGOT_PASSWORD
            )
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_EML)
            emlButtonExists(true)
            otpButtonExists(false)
        }
    }

    @Test
    fun otpShowsCorrectButton() {
        passwordResetSentScreenRobot(
            details = PasswordResetDetails(
                parameters = Passwords.ResetByEmailStartParameters(""),
                resetType = PasswordResetType.FORGOT_PASSWORD
            )
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_EOTP)
            emlButtonExists(false)
            otpButtonExists(true)
        }
    }

    @Test
    fun uiUpdatesWithStateChangeAsExpected() {
        passwordResetSentScreenRobot(
            details = PasswordResetDetails(
                parameters = Passwords.ResetByEmailStartParameters(""),
                resetType = PasswordResetType.NO_PASSWORD_SET
            )
        ) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            loadingDialogExists(false)
            setLoadingDialogVisible(true)
            loadingDialogExists(true)
            resendDialogExists(false)
            setResendDialogVisible(true)
            resendDialogExists(true)
            setGenericErrorMessage("My error message")
            genericErrorMessageExists("My error message")
        }
    }
}
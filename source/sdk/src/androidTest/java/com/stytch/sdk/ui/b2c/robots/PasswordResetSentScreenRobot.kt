package com.stytch.sdk.ui.b2c.robots

import androidx.compose.ui.test.onNodeWithText
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.BaseAndroidComposeTest
import com.stytch.sdk.ui.b2c.data.PasswordResetDetails
import com.stytch.sdk.ui.b2c.screens.PasswordResetSentScreen

internal fun BaseAndroidComposeTest.passwordResetSentScreenRobot(
    details: PasswordResetDetails,
    func: PasswordResetSentScreenRobot.() -> Unit,
) = PasswordResetSentScreenRobot(details, this).apply(func)

internal class PasswordResetSentScreenRobot(
    details: PasswordResetDetails,
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(baseAndroidComposeTest.composeTestRule, PasswordResetSentScreen(details)) {
    private val forgotPasswordTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_forgot_password_title))
    }

    private val setNewPasswordTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_password_reset_sent_title))
    }

    private val forgotPasswordText by lazy {
        composeTestRule.onNodeWithText(
            getString(R.string.stytch_b2c_forgot_password_body, "robot@stytch.com"),
            substring = true,
        )
    }

    private val noPasswordText by lazy {
        composeTestRule.onNodeWithText(
            getString(R.string.stytch_b2c_email_create_password_body, "robot@stytch.com"),
        )
    }

    private val breachedPasswordText by lazy {
        composeTestRule.onNodeWithText(
            getString(R.string.stytch_b2c_password_reset_breached_body, "robot@stytch.com"),
            substring = true,
        )
    }

    private val dedupePasswordText by lazy {
        composeTestRule.onNodeWithText(
            getString(R.string.stytch_b2c_password_reset_dedupe_body, "robot@stytch.com"),
            substring = true,
        )
    }

    private val resendText by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_button_resend_link), substring = true)
    }

    private val stytchButtonEML by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_button_login_link))
    }

    private val stytchButtonOTP by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_button_login_code))
    }

    fun forgotPasswordTitleExists(shouldExist: Boolean) {
        if (shouldExist) {
            forgotPasswordTitle.assertExists()
        } else {
            forgotPasswordTitle.assertDoesNotExist()
        }
    }

    fun setNewPasswordTitleExists(shouldExist: Boolean) {
        if (shouldExist) {
            setNewPasswordTitle.assertExists()
        } else {
            setNewPasswordTitle.assertDoesNotExist()
        }
    }

    fun forgotPasswordTextExists(shouldExist: Boolean) {
        if (shouldExist) {
            forgotPasswordText.assertExists()
        } else {
            forgotPasswordText.assertDoesNotExist()
        }
    }

    fun noPasswordTextExists(shouldExist: Boolean) {
        if (shouldExist) {
            noPasswordText.assertExists()
        } else {
            noPasswordText.assertDoesNotExist()
        }
    }

    fun breachedPasswordTextExists(shouldExist: Boolean) {
        if (shouldExist) {
            breachedPasswordText.assertExists()
        } else {
            breachedPasswordText.assertDoesNotExist()
        }
    }

    fun dedupePasswordTextExists(shouldExist: Boolean) {
        if (shouldExist) {
            dedupePasswordText.assertExists()
        } else {
            dedupePasswordText.assertDoesNotExist()
        }
    }

    fun resendTextExists() = resendText.assertExists()

    fun emlButtonExists(shouldExist: Boolean) {
        if (shouldExist) {
            stytchButtonEML.assertExists()
        } else {
            stytchButtonEML.assertDoesNotExist()
        }
    }

    fun otpButtonExists(shouldExist: Boolean) {
        if (shouldExist) {
            stytchButtonOTP.assertExists()
        } else {
            stytchButtonOTP.assertDoesNotExist()
        }
    }
}

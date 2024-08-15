package com.stytch.sdk.ui.robots

import androidx.compose.ui.test.onNodeWithText
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.screens.PasswordResetSentScreen

internal fun BaseAndroidComposeTest.passwordResetSentScreenRobot(
    details: PasswordResetDetails,
    func: PasswordResetSentScreenRobot.() -> Unit,
) = PasswordResetSentScreenRobot(details, this).apply(func)

internal class PasswordResetSentScreenRobot(
    details: PasswordResetDetails,
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(baseAndroidComposeTest.composeTestRule, PasswordResetSentScreen(details)) {
    private val forgotPasswordTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.forgot_password))
    }

    private val setNewPasswordTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.check_email_new_password))
    }

    private val forgotPasswordText by lazy {
        composeTestRule.onNodeWithText(getString(R.string.reset_password_link_sent), substring = true)
    }

    private val noPasswordText by lazy {
        composeTestRule.onNodeWithText(
            getString(R.string.login_link_sent) + "   " + getString(R.string.create_password_for_account),
        )
    }

    private val breachedPasswordText by lazy {
        composeTestRule.onNodeWithText(getString(R.string.breached_password), substring = true)
    }

    private val dedupePasswordText by lazy {
        composeTestRule.onNodeWithText(getString(R.string.secure_your_account), substring = true)
    }

    private val resendText by lazy {
        composeTestRule.onNodeWithText(getString(R.string.didnt_get_it), substring = true)
    }

    private val stytchButtonEML by lazy {
        composeTestRule.onNodeWithText(getString(R.string.email_me_a_login_link))
    }

    private val stytchButtonOTP by lazy {
        composeTestRule.onNodeWithText(getString(R.string.email_me_a_login_code))
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

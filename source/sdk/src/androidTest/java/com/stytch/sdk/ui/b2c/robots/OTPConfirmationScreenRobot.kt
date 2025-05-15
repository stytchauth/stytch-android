package com.stytch.sdk.ui.b2c.robots

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.BaseAndroidComposeTest
import com.stytch.sdk.ui.b2c.data.OTPDetails
import com.stytch.sdk.ui.b2c.screens.OTPConfirmationScreen

internal fun BaseAndroidComposeTest.otpConfirmationScreenRobot(
    resendParameters: OTPDetails,
    isReturningUser: Boolean,
    emailAddress: String?,
    func: OTPConfirmationScreenRobot.() -> Unit,
) = OTPConfirmationScreenRobot(resendParameters, isReturningUser, emailAddress, this).apply(func)

internal class OTPConfirmationScreenRobot(
    resendParameters: OTPDetails,
    isReturningUser: Boolean,
    emailAddress: String?,
    baseAndroidComposeTest: BaseAndroidComposeTest,
) : BaseRobotScreen(
        baseAndroidComposeTest.composeTestRule,
        OTPConfirmationScreen(
            resendParameters = resendParameters,
            isReturningUser = isReturningUser,
            emailAddress = emailAddress,
        ),
    ) {
    private val pageTitle by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_otp_confirmation_title))
    }

    private val passcodeSentText by lazy {
        composeTestRule.onNodeWithText(
            getString(R.string.stytch_passcode_sent_confirmation, "robot@stytch.com"),
            substring = true,
        )
    }

    private val otpEntry by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.stytch_semantics_otp_entry))
    }

    private val passwordButton by lazy {
        composeTestRule.onNodeWithText(getString(R.string.stytch_b2c_button_create_password), substring = true)
    }

    fun pageTitleExists() = pageTitle.assertExists()

    fun passcodeSentTextExists() = passcodeSentText.assertExists()

    fun otpEntryExists() = otpEntry.assertExists()

    fun passwordButtonExists(shouldExist: Boolean) {
        if (shouldExist) {
            passwordButton.assertExists()
        } else {
            passwordButton.assertDoesNotExist()
        }
    }
}

package com.stytch.sdk.ui.tests

import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.ui.BaseAndroidComposeTest
import com.stytch.sdk.ui.data.ApplicationUIState
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.data.REALISTIC_STYTCH_UI_CONFIG_NO_PASSWORD
import com.stytch.sdk.ui.robots.emlConfirmationScreenRobot
import org.junit.Test

internal class EMLConfirmationScreenTest:  BaseAndroidComposeTest() {
    override fun provideTestInstance() = this
    private val details = EMLDetails(
        parameters = MagicLinks.EmailMagicLinks.Parameters(
            email = "robot@stytch.com"
        )
    )

    @Test
    fun newUserDoesNotShowPasswordOption() {
        emlConfirmationScreenRobot(details, false) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            backButtonExists()
            pageTitleExists()
            loginLinkTextExists()
            resendLinkTextExists()
            passwordButtonExists(false)
            resendDialogExists(false)
        }
    }

    @Test
    fun returningUserNoPasswordsDoesNotShowPasswordOption() {
        emlConfirmationScreenRobot(details, true) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG_NO_PASSWORD)
            backButtonExists()
            pageTitleExists()
            loginLinkTextExists()
            resendLinkTextExists()
            passwordButtonExists(false)
            resendDialogExists(false)
        }
    }

    @Test
    fun returningUserWithPasswordsShowsPasswordOption() {
        emlConfirmationScreenRobot(details, true) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            backButtonExists()
            pageTitleExists()
            loginLinkTextExists()
            resendLinkTextExists()
            passwordButtonExists(true)
            resendDialogExists(false)
        }
    }

    @Test
    fun clickingResendLinkShowsDialog() {
        emlConfirmationScreenRobot(details, true) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            resendDialogExists(false)
            clickResendLink()
            resendDialogExists(true)
        }
    }

    @Test
    fun genericErrorMessageStateShowsError() {
        emlConfirmationScreenRobot(details, true) {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            setState(ApplicationUIState().copy(
                genericErrorMessage = "My Test Error"
            ))
            genericErrorMessageExists("My Test Error")
        }
    }
}
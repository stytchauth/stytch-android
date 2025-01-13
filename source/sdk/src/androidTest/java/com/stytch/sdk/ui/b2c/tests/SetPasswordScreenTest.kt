package com.stytch.sdk.ui.b2c.tests

import com.stytch.sdk.ui.b2c.BaseAndroidComposeTest
import com.stytch.sdk.ui.b2c.data.REALISTIC_STYTCH_UI_CONFIG
import com.stytch.sdk.ui.b2c.robots.setPasswordScreenRobot
import org.junit.Test

internal class SetPasswordScreenTest : BaseAndroidComposeTest() {
    override fun provideTestInstance() = this

    @Test
    fun displaysAsExpected() {
        setPasswordScreenRobot {
            clearAndSetContent(REALISTIC_STYTCH_UI_CONFIG)
            backButtonExists()
            pageTitleExists()
            emailPasswordEntryExists()
            loadingDialogExists(false)
            setLoadingDialogVisible(true)
            loadingDialogExists(true)
            setGenericErrorMessage("My Error Message")
            genericErrorMessageExists("My Error Message")
        }
    }
}

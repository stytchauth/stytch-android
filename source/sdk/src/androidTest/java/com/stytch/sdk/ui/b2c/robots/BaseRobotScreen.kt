package com.stytch.sdk.ui.b2c.robots

import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.R
import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.ui.b2c.AuthenticationActivity
import com.stytch.sdk.ui.b2c.StytchAuthenticationApp
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.GenericErrorDetails
import com.stytch.sdk.ui.b2c.data.StytchUIConfig
import com.stytch.sdk.ui.b2c.theme.StytchThemeProvider

internal abstract class BaseRobotScreen(
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<AuthenticationActivity>, AuthenticationActivity>,
    private val screen: AndroidScreen?,
) {
    protected fun getString(
        @StringRes resourceId: Int,
        vararg arguments: String,
    ) = composeTestRule.activity.getString(resourceId, *arguments)

    private val backButton by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.stytch_semantics_back_button))
    }

    private val loadingDialog by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.stytch_semantics_loading_dialog))
    }

    private val resendDialog by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.stytch_semantics_alert_dialog))
    }

    fun clearAndSetContent(
        uiConfig: StytchUIConfig,
        onInvalidConfig: (StytchUIInvalidConfiguration) -> Unit = {},
    ) {
        composeTestRule.activity.setContent {}
        composeTestRule.waitForIdle()
        composeTestRule.activity.setContent {
            StytchThemeProvider(config = uiConfig) {
                StytchAuthenticationApp(
                    bootstrapData = uiConfig.bootstrapData,
                    screen = screen,
                    productConfig = uiConfig.productConfig,
                    onInvalidConfig = onInvalidConfig,
                )
            }
        }
    }

    fun await() {
        composeTestRule.waitForIdle()
    }

    fun setState(newState: ApplicationUIState) {
        composeTestRule.activity.savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = newState
    }

    fun setLoadingDialogVisible(visible: Boolean) {
        val existingState =
            composeTestRule.activity.savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] ?: ApplicationUIState()
        setState(existingState.copy(showLoadingDialog = visible))
    }

    fun setResendDialogVisible(visible: Boolean) {
        val existingState =
            composeTestRule.activity.savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] ?: ApplicationUIState()
        setState(existingState.copy(showResendDialog = visible))
    }

    fun setGenericErrorMessage(errorMessage: String) {
        val existingState =
            composeTestRule.activity.savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] ?: ApplicationUIState()
        setState(existingState.copy(genericErrorMessage = GenericErrorDetails(errorMessage)))
    }

    fun backButtonExists() = backButton.assertExists()

    fun genericErrorMessageExists(errorMessage: String) {
        composeTestRule.onNodeWithText(errorMessage).assertExists()
    }

    fun loadingDialogExists(shouldExist: Boolean) {
        if (shouldExist) {
            loadingDialog.assertExists()
        } else {
            loadingDialog.assertDoesNotExist()
        }
    }

    fun resendDialogExists(shouldExist: Boolean) {
        if (shouldExist) {
            resendDialog.assertExists()
        } else {
            resendDialog.assertDoesNotExist()
        }
    }
}

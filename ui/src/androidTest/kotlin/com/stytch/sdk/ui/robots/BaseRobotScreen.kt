package com.stytch.sdk.ui.robots

import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.ui.AuthenticationActivity
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.StytchAuthenticationApp
import com.stytch.sdk.ui.data.ApplicationUIState
import com.stytch.sdk.ui.data.StytchUIConfig
import com.stytch.sdk.ui.theme.StytchTheme

internal abstract class BaseRobotScreen(
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<AuthenticationActivity>, AuthenticationActivity>,
    private val screen: AndroidScreen?,
) {
    protected fun getString(
        @StringRes resourceId: Int,
    ) = composeTestRule.activity.getString(resourceId)

    private val backButton by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.back))
    }

    private val loadingDialog by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.semantics_loading_dialog))
    }

    private val resendDialog by lazy {
        composeTestRule.onNodeWithContentDescription(getString(R.string.semantics_alert_dialog))
    }

    fun clearAndSetContent(
        uiConfig: StytchUIConfig,
        onInvalidConfig: (StytchUIInvalidConfiguration) -> Unit = {},
    ) {
        composeTestRule.activity.setContent {}
        composeTestRule.waitForIdle()
        composeTestRule.activity.setContent {
            StytchTheme(config = uiConfig) {
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
        setState(existingState.copy(genericErrorMessage = errorMessage))
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

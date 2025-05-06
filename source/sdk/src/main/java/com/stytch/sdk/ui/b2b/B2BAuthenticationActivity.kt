package com.stytch.sdk.ui.b2b

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.AuthenticationResult
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetDeeplinkTokenPair
import com.stytch.sdk.ui.b2b.data.StytchB2BUIConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.theme.StytchB2BThemeProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

internal class B2BAuthenticationActivity : ComponentActivity() {
    private lateinit var uiConfig: StytchB2BUIConfig
    private val viewModel: B2BAuthenticationViewModel by viewModels {
        B2BAuthenticationViewModel.create(uiConfig.productConfig)
    }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiConfig = intent.getParcelableExtra(STYTCH_UI_CONFIG_KEY)
            ?: savedInstanceState?.getParcelable(STYTCH_UI_CONFIG_KEY)
            ?: run {
                returnAuthenticationResult(
                    AuthenticationResult.Error(StytchUIInvalidConfiguration("No UI Configuration Provided")),
                )
                return@onCreate
            }
        if (StytchB2BClient.isInitialized.value) {
            StytchB2BClient.events.logEvent(
                eventName = "render_b2b_login_screen",
                details = mapOf("options" to uiConfig.productConfig),
            )
        }
        setContent {
            val state = viewModel.stateFlow.collectAsState()
            val organizationSlug = uiConfig.productConfig.organizationSlug
            val activeOrganization = state.value.activeOrganization
            val isSearchingForOrganizationBySlug = state.value.isSearchingForOrganizationBySlug
            val authFlowType = state.value.authFlowType
            val currentRoute = state.value.currentRoute
            LaunchedEffect(
                organizationSlug,
                activeOrganization,
                isSearchingForOrganizationBySlug,
                authFlowType,
            ) {
                val isOrgFlowWithNoOrg = authFlowType == AuthFlowType.ORGANIZATION && activeOrganization == null
                if (isOrgFlowWithNoOrg && organizationSlug != null && !isSearchingForOrganizationBySlug) {
                    // search for the organization by slug
                    viewModel.performInitialOrgBySlugSearch(organizationSlug)
                } else if (isOrgFlowWithNoOrg && !isSearchingForOrganizationBySlug && currentRoute == Routes.Main) {
                    // Trying to launch an org flow without an org
                    viewModel.dispatch(SetB2BError(B2BErrorType.Organization))
                } else {
                    // All good to render
                    render()
                }
            }
        }
    }

    private fun render() {
        setContent {
            val state = viewModel.stateFlow.collectAsState()
            val currentRoute = state.value.currentRoute
            LaunchedEffect(currentRoute) {
                if (currentRoute == Routes.Success) {
                    returnAuthenticationResult(AuthenticationResult.Authenticated)
                }
            }
            StytchB2BThemeProvider(config = uiConfig) {
                StytchB2BAuthenticationApp(
                    dispatch = viewModel::dispatch,
                    createViewModel = viewModel::createViewModel,
                    rootAppState = state.value.toRootAppState(),
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.let {
                viewModel.dispatch(SetDeeplinkTokenPair(StytchB2BClient.parseDeeplink(it)))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(STYTCH_UI_CONFIG_KEY, uiConfig)
        super.onSaveInstanceState(outState)
    }

    private fun returnAuthenticationResult(result: AuthenticationResult) {
        if (StytchB2BClient.isInitialized.value) {
            when (result) {
                is AuthenticationResult.Authenticated -> StytchB2BClient.events.logEvent("ui_authentication_success")
                is AuthenticationResult.Error ->
                    StytchB2BClient.events.logEvent(
                        eventName = "ui_authentication_failure",
                        details = null,
                        error = result.error,
                    )
            }
        }
        val data =
            Intent().apply {
                putExtra(STYTCH_RESULT_KEY, result)
            }
        setResult(RESULT_OK, data)
        finish()
    }

    internal fun exitWithoutAuthenticating() {
        setResult(RESULT_CANCELED)
        finish()
    }

    internal companion object {
        internal const val STYTCH_UI_CONFIG_KEY = "STYTCH_B2B_UI_CONFIG"
        internal const val STYTCH_RESULT_KEY = "STYTCH_RESULT"

        internal fun createIntent(
            context: Context,
            uiConfig: StytchB2BUIConfig,
        ) = Intent(context, B2BAuthenticationActivity::class.java).apply {
            putExtra(STYTCH_UI_CONFIG_KEY, uiConfig)
        }
    }
}

private fun B2BUIState.toRootAppState() =
    RootAppState(
        currentRoute = currentRoute,
        deeplinkTokenPair = deeplinkTokenPair,
        errorDetails = errorDetails,
        isLoading = isLoading,
    )

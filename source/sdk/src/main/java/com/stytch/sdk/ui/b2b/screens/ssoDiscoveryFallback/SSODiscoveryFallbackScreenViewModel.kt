package com.stytch.sdk.ui.b2b.screens.ssoDiscoveryFallback

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.searchManager.SearchManager
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.SetActiveOrganization
import com.stytch.sdk.ui.b2b.data.SetAuthFlowType
import com.stytch.sdk.ui.b2b.data.SetGenericError
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseSSOStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class SSODiscoveryFallbackScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    private val useSSOStart = UseSSOStart()

    private val _uiState = MutableStateFlow(SSODiscoveryFallbackScreenUIState())
    val uiState = _uiState.asStateFlow()

    private fun handleSubmit(
        activity: Activity,
        slug: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            when (
                val response =
                    StytchB2BClient.searchManager.searchOrganization(
                        SearchManager.SearchOrganizationParameters(organizationSlug = slug),
                    )
            ) {
                is StytchResult.Success -> {
                    if (response.value.organization == null) {
                        _uiState.value = _uiState.value.copy(error = "Organization not found. Please try again.")
                        return@launch
                    }
                    dispatch(SetAuthFlowType(AuthFlowType.ORGANIZATION))
                    dispatch(SetActiveOrganization(response.value.organization))
                    val activeConnections = response.value.organization.ssoActiveConnections ?: emptyList()
                    if (activeConnections.size == 1) {
                        useSSOStart(activity, activeConnections[0].connectionId)
                    } else {
                        dispatch(SetNextRoute(Routes.Main))
                    }
                }
                is StytchResult.Error -> {
                    dispatch(SetGenericError(response.exception.message))
                }
            }
        }
    }

    fun handle(action: SSODiscoveryFallbackScreenAction) {
        when (action) {
            SSODiscoveryFallbackScreenAction.ResetEverything -> dispatch(ResetEverything)
            is SSODiscoveryFallbackScreenAction.Submit -> handleSubmit(action.activity, action.slug)
        }
    }
}

@JacocoExcludeGenerated
internal data class SSODiscoveryFallbackScreenUIState(
    val error: String? = null,
)

internal sealed class SSODiscoveryFallbackScreenAction {
    data object ResetEverything : SSODiscoveryFallbackScreenAction()

    @JacocoExcludeGenerated
    data class Submit(
        val activity: Activity,
        val slug: String,
    ) : SSODiscoveryFallbackScreenAction()
}

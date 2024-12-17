package com.stytch.sdk.ui.b2b

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.searchManager.SearchManager
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetActiveOrganization
import com.stytch.sdk.ui.b2b.data.SetAuthFlowType
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetIsSearchingForOrganizationBySlug
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.SetStytchError
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.domain.B2BUIStateMachine
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.utils.B2BUIViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class B2BAuthenticationViewModel(
    savedStateHandle: SavedStateHandle,
    private val stateMachine: B2BUIStateMachine,
    val stateFlow: StateFlow<B2BUIState>,
    productConfig: StytchB2BProductConfig,
) : ViewModel() {
    private val viewModelHelperFactory: ViewModelProvider.AndroidViewModelFactory =
        B2BUIViewModelFactory(
            stateFlow,
            stateMachine::dispatch,
            productConfig,
        )

    init {
        viewModelScope.launch {
            stateMachine.state.collect { newState ->
                savedStateHandle[B2BUIState.SAVED_STATE_KEY] = newState
            }
        }
        dispatch(SetAuthFlowType(productConfig.authFlowType))
        if (productConfig.authFlowType == AuthFlowType.DISCOVERY && stateFlow.value.currentRoute == null) {
            dispatch(SetNextRoute(Routes.Main))
        }
    }

    internal fun performInitialOrgBySlugSearch(slug: String) {
        dispatch(SetIsSearchingForOrganizationBySlug(true))
        viewModelScope.launch(Dispatchers.IO) {
            when (
                val response =
                    StytchB2BClient.searchManager.searchOrganization(
                        SearchManager.SearchOrganizationParameters(organizationSlug = slug),
                    )
            ) {
                is StytchResult.Success -> {
                    if (response.value.organization == null) {
                        return@launch dispatch(SetB2BError(B2BErrorType.Organization))
                    }
                    dispatch(SetActiveOrganization(response.value.organization))
                    dispatch(SetIsSearchingForOrganizationBySlug(false))
                    dispatch(SetNextRoute(Routes.Main))
                }
                is StytchResult.Error -> {
                    dispatch(SetIsSearchingForOrganizationBySlug(false))
                    dispatch(SetStytchError(response.exception))
                }
            }
        }
    }

    internal fun dispatch(action: B2BUIAction) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }

    internal fun <T : ViewModel> createViewModel(modelClass: Class<T>): T = viewModelHelperFactory.create(modelClass)

    companion object {
        fun create(productConfig: StytchB2BProductConfig): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    val stateFlow =
                        savedStateHandle.getStateFlow(
                            B2BUIState.SAVED_STATE_KEY,
                            B2BUIState(uiIncludedMfaMethods = productConfig.mfaProductInclude),
                        )
                    val stateMachine = B2BUIStateMachine(stateFlow.value)
                    B2BAuthenticationViewModel(savedStateHandle, stateMachine, stateFlow, productConfig)
                }
            }
    }
}

package com.stytch.sdk.ui.b2b.screens.discovery

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.network.models.DiscoveredOrganization
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UseDiscoveryIntermediateSessionExchange
import com.stytch.sdk.ui.b2b.usecases.UseDiscoveryOrganizationCreate
import com.stytch.sdk.ui.b2b.usecases.UseSSOStart
import com.stytch.sdk.ui.shared.data.EmailState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class DiscoveryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val useSSOStart = UseSSOStart()
    private val useDiscoveryIntermediateSessionExchange =
        UseDiscoveryIntermediateSessionExchange(productConfig, ::request)
    private val useDiscoveryOrganizationCreate = UseDiscoveryOrganizationCreate(::request)
    private val isCreatingStateFlow = MutableStateFlow(false)
    private val isExchangingStateFlow = MutableStateFlow(false)

    private val _discoveryScreenState =
        combine(isCreatingStateFlow, isExchangingStateFlow, state) { isCreating, isExchanging, state ->
            discoveryScreenStateMapper(isCreating, isExchanging, state)
        }
    val discoveryScreenState =
        _discoveryScreenState.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            discoveryScreenStateMapper(false, false, state.value),
        )

    private fun createOrganization() {
        isCreatingStateFlow.value = true
        viewModelScope.launch(Dispatchers.IO) {
            useDiscoveryOrganizationCreate()
                .onSuccess {
                    isCreatingStateFlow.value = false
                }.onFailure {
                    isCreatingStateFlow.value = false
                }
        }
    }

    private fun exchangeSessionForOrganization(organizationId: String) {
        isExchangingStateFlow.value = true
        viewModelScope.launch(Dispatchers.IO) {
            useDiscoveryIntermediateSessionExchange(organizationId)
                .onSuccess {
                    isExchangingStateFlow.value = false
                }.onFailure {
                    isExchangingStateFlow.value = false
                }
        }
    }

    fun handle(action: DiscoveryScreenActions) {
        when (action) {
            DiscoveryScreenActions.ResetEverything -> dispatch(ResetEverything)
            is DiscoveryScreenActions.CreateOrganization -> createOrganization()
            is DiscoveryScreenActions.ExchangeSessionForOrganization ->
                exchangeSessionForOrganization(action.organizationId)
            is DiscoveryScreenActions.StartSSO -> useSSOStart(action.context, action.connectionId)
        }
    }
}

@JacocoExcludeGenerated
internal data class DiscoveryScreenState(
    val isCreating: Boolean = false,
    val isExchanging: Boolean = false,
    val discoveredOrganizations: List<DiscoveredOrganization>? = null,
    val emailState: EmailState = EmailState(),
)

internal sealed class DiscoveryScreenActions {
    data object ResetEverything : DiscoveryScreenActions()

    internal data object CreateOrganization : DiscoveryScreenActions()

    @JacocoExcludeGenerated
    internal data class ExchangeSessionForOrganization(
        val organizationId: String,
    ) : DiscoveryScreenActions()

    @JacocoExcludeGenerated
    internal data class StartSSO(
        val context: Activity,
        val connectionId: String,
    ) : DiscoveryScreenActions()
}

private fun discoveryScreenStateMapper(
    isCreating: Boolean,
    isExchanging: Boolean,
    state: B2BUIState,
) = DiscoveryScreenState(
    isCreating = isCreating,
    isExchanging = isExchanging,
    discoveredOrganizations = state.discoveredOrganizations,
    emailState = state.emailState,
)

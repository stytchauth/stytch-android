package com.stytch.sdk.ui.b2b.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.searchManager.SearchManager
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
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
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.FormFieldStatus
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.components.StytchInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class SSODiscoveryFallbackScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    val useSSOStart = UseSSOStart()

    private val _uiState = MutableStateFlow(SSODiscoveryFallbackScreenUIState())
    val uiState = _uiState.asStateFlow()

    fun handleSubmit(
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
}

internal data class SSODiscoveryFallbackScreenUIState(
    val error: String? = null,
)

@Composable
internal fun SSODiscoveryFallbackScreen(
    createViewModel: CreateViewModel<SSODiscoveryFallbackScreenViewModel>,
    viewModel: SSODiscoveryFallbackScreenViewModel = createViewModel(SSODiscoveryFallbackScreenViewModel::class.java),
) {
    val (slug, setSlug) = remember { mutableStateOf("") }
    val state = viewModel.uiState.collectAsState()
    val activity = LocalActivity.current as Activity
    BackHandler(enabled = true) {
        viewModel.dispatch(ResetEverything)
    }
    Column {
        BackButton {
            viewModel.dispatch(ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = "Sorry, we couldn't find any connections")
        BodyText(
            text =
                "Please input the Organization's unique slug to continue. If you don't know the unique slug, log in" +
                    "through another method to view all of your available Organizations.",
        )
        StytchInput(
            modifier = Modifier.fillMaxWidth(),
            value = slug,
            onValueChange = setSlug,
            label = "Enter org slug",
        )
        state.value.error?.let {
            FormFieldStatus(isError = true, text = it)
        }
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(
            enabled = slug.isNotBlank(),
            text = "Continue",
            onClick = { viewModel.handleSubmit(activity, slug) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        BodyText(
            text = "Try another login method",
            modifier = Modifier.clickable { viewModel.dispatch(ResetEverything) },
        )
    }
}

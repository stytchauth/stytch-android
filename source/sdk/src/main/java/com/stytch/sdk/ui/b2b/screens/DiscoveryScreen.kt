package com.stytch.sdk.ui.b2b.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.DiscoveredOrganization
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.SetActiveOrganization
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.extensions.jitEligible
import com.stytch.sdk.ui.b2b.extensions.shouldAllowDirectLoginToOrganization
import com.stytch.sdk.ui.b2b.extensions.toInternalOrganizationData
import com.stytch.sdk.ui.b2b.usecases.UseDiscoveryIntermediateSessionExchange
import com.stytch.sdk.ui.b2b.usecases.UseDiscoveryOrganizationCreate
import com.stytch.sdk.ui.b2b.usecases.UseSSOStart
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.theme.LocalStytchB2BProductConfig
import com.stytch.sdk.ui.shared.theme.LocalStytchBootstrapData
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class DiscoveryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    val useSSOStart = UseSSOStart()
    private val useDiscoveryIntermediateSessionExchange =
        UseDiscoveryIntermediateSessionExchange(productConfig, ::request)
    private val useDiscoveryOrganizationCreate = UseDiscoveryOrganizationCreate(::request)

    private val _isCreatingStateFlow = MutableStateFlow(false)
    val isCreatingStateFlow = _isCreatingStateFlow.asStateFlow()

    private val _isExchangingStateFlow = MutableStateFlow(false)
    val isExchangingStateFlow = _isExchangingStateFlow.asStateFlow()

    fun createOrganization() {
        _isCreatingStateFlow.value = true
        viewModelScope.launch(Dispatchers.IO) {
            useDiscoveryOrganizationCreate()
                .onSuccess {
                    _isCreatingStateFlow.value = false
                }.onFailure {
                    _isCreatingStateFlow.value = false
                }
        }
    }

    fun exchangeSessionForOrganization(organizationId: String) {
        _isExchangingStateFlow.value = true
        viewModelScope.launch(Dispatchers.IO) {
            useDiscoveryIntermediateSessionExchange(organizationId)
                .onSuccess {
                    _isExchangingStateFlow.value = false
                }.onFailure {
                    _isExchangingStateFlow.value = false
                }
        }
    }
}

@Composable
internal fun DiscoveryScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<DiscoveryScreenViewModel>,
    viewModel: DiscoveryScreenViewModel = createViewModel(DiscoveryScreenViewModel::class.java),
) {
    val config = LocalStytchB2BProductConfig.current
    val theme = LocalStytchTheme.current
    val shouldDirectLoginConfigEnabled = config.directLoginForSingleMembership?.status == true
    val createOrganzationsEnabled = LocalStytchBootstrapData.current.createOrganizationEnabled
    val isCreatingState = viewModel.isCreatingStateFlow.collectAsStateWithLifecycle()
    val isExchangingState = viewModel.isExchangingStateFlow.collectAsStateWithLifecycle()
    val context = LocalActivity.current as Activity
    val shouldAutomaticallyCreateOrganization =
        createOrganzationsEnabled && config.directCreateOrganizationForNoMembership

    fun handleDiscoveryOrganizationStart(discoveredOrganization: DiscoveredOrganization) {
        val organization = discoveredOrganization.organization
        if (discoveredOrganization.primaryRequired != null) {
            val allowedAuthMethods = discoveredOrganization.primaryRequired.allowedAuthMethods ?: emptyList()
            if (allowedAuthMethods.firstOrNull() === AllowedAuthMethods.SSO &&
                organization.ssoDefaultConnectionId != null
            ) {
                viewModel.useSSOStart(context, organization.ssoDefaultConnectionId)
            } else {
                viewModel.dispatch(SetActiveOrganization(organization.toInternalOrganizationData()))
                viewModel.exchangeSessionForOrganization(organizationId = organization.organizationId)
            }
        } else {
            if (isExchangingState.value) return
            viewModel.exchangeSessionForOrganization(organizationId = organization.organizationId)
        }
    }

    fun handleDiscoveryOrganizationCreate() {
        if (isCreatingState.value) return
        viewModel.createOrganization()
    }

    BackHandler(enabled = true) {
        viewModel.dispatch(ResetEverything)
    }

    LaunchedEffect(shouldDirectLoginConfigEnabled, state.value.discoveredOrganizations) {
        val directLoginOrganization =
            (state.value.discoveredOrganizations ?: emptyList()).shouldAllowDirectLoginToOrganization(
                config.directLoginForSingleMembership,
            )
        if (directLoginOrganization != null && shouldDirectLoginConfigEnabled) {
            handleDiscoveryOrganizationStart(directLoginOrganization)
        }
    }

    LaunchedEffect(shouldAutomaticallyCreateOrganization) {
        if (shouldAutomaticallyCreateOrganization) {
            handleDiscoveryOrganizationCreate()
        }
    }

    Column {
        if (isExchangingState.value) {
            return LoggingInView(color = Color(theme.inputTextColor))
        }

        if (isCreatingState.value) {
            return LoadingView(color = Color(theme.inputTextColor))
        }

        if (state.value.discoveredOrganizations.isNullOrEmpty()) {
            return NoOrganizationsDiscovered(
                state = state,
                createOrganzationsEnabled = createOrganzationsEnabled,
                onCreateOrganization = viewModel::createOrganization,
                isCreatingOrganization = isCreatingState.value,
                onGoBack = { viewModel.dispatch(ResetEverything) },
            )
        }
        BackButton(onClick = { viewModel.dispatch(ResetEverything) })
        PageTitle(textAlign = TextAlign.Left, text = "Select an organization to continue")
        Column(modifier = Modifier.fillMaxWidth()) {
            state.value.discoveredOrganizations?.map { discoveredOrganization ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { handleDiscoveryOrganizationStart(discoveredOrganization) }
                            .border(1.dp, Color(theme.inputBorderColor), RoundedCornerShape(theme.inputBorderRadius)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Logo(
                            name = discoveredOrganization.organization.organizationName,
                            logoUrl = discoveredOrganization.organization.organizationLogoUrl,
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = discoveredOrganization.organization.organizationName,
                            color = Color(theme.primaryTextColor),
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ArrowText(type = discoveredOrganization.membership.type)
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(theme.primaryTextColor),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        if (createOrganzationsEnabled) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color(theme.disabledTextColor),
                )
                Spacer(modifier = Modifier.height(8.dp))
                StytchButton(
                    text = "Create an organization",
                    enabled = true,
                    onClick = ::handleDiscoveryOrganizationCreate,
                )
            }
        }
    }
}

@Composable
private fun LoggingInView(color: Color) {
    PageTitle(textAlign = TextAlign.Left, text = "Logging In...")
    CircularProgressIndicator(color = color)
}

@Composable
internal fun LoadingView(color: Color) {
    CircularProgressIndicator(color = color)
}

@Composable
private fun NoOrganizationsDiscovered(
    state: State<B2BUIState>,
    createOrganzationsEnabled: Boolean,
    onCreateOrganization: () -> Unit,
    isCreatingOrganization: Boolean,
    onGoBack: () -> Unit,
) {
    val config = LocalStytchB2BProductConfig.current
    val theme = LocalStytchTheme.current

    fun handleDiscoveryOrganizationCreate() {
        if (isCreatingOrganization) return
        onCreateOrganization()
    }

    if (isCreatingOrganization) {
        LoadingView(color = Color(theme.inputTextColor))
        return
    }

    if (createOrganzationsEnabled && config.allowCreateOrganization) {
        PageTitle(textAlign = TextAlign.Left, text = "Create an organization to get started")
        StytchButton(enabled = true, text = "Create an organization", onClick = ::handleDiscoveryOrganizationCreate)
        Spacer(modifier = Modifier.height(16.dp))
        BodyText(
            text =
                "${state.value.emailState.emailAddress} does not have an account. Think this is a mistake? " +
                    "Try a different email address, or contact your admin.",
        )
        StytchButton(enabled = true, onClick = onGoBack, text = "Try a different email address")
        return
    }

    PageTitle(
        textAlign = TextAlign.Left,
        text = "${state.value.emailState.emailAddress} does not belong to any organizations.",
    )
    BodyText(text = "Make sure your email address is correct. Otherwise, you might need to be invited by your admin.")
    StytchButton(enabled = true, onClick = onGoBack, text = "Try a different email address")
}

@Composable
private fun Logo(
    name: String,
    logoUrl: String?,
) {
    val theme = LocalStytchTheme.current
    if (!logoUrl.isNullOrEmpty()) {
        AsyncImage(
            model = logoUrl,
            contentDescription = name,
            modifier =
                Modifier
                    .height(40.dp)
                    .width(40.dp)
                    .border(
                        BorderStroke(Dp.Hairline, Color(theme.inputBorderColor)),
                        RoundedCornerShape(theme.buttonBorderRadius),
                    ),
        )
    } else {
        Box(
            modifier =
                Modifier
                    .height(40.dp)
                    .width(40.dp)
                    .border(
                        BorderStroke(Dp.Hairline, Color(theme.inputBorderColor)),
                        RoundedCornerShape(theme.buttonBorderRadius),
                    ).background(Color(theme.backgroundColor)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${name[0]}",
                color = Color(theme.primaryTextColor),
                fontWeight = FontWeight.W400,
                fontSize = 18.sp,
                lineHeight = 40.sp,
            )
        }
    }
}

@Composable
private fun ArrowText(type: String) {
    val theme = LocalStytchTheme.current
    val typography = LocalStytchTypography.current
    val text =
        if (jitEligible.contains(type) || type == "pending_member") {
            "Join"
        } else if (type == "invited_member") {
            "Accept Invite"
        } else {
            ""
        }
    Text(
        text = text,
        style =
            typography.body.copy(
                color = Color(theme.primaryTextColor),
                fontWeight = FontWeight.Bold,
            ),
    )
}

package com.stytch.sdk.ui.b2b.screens.discovery

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.DiscoveredOrganization
import com.stytch.sdk.ui.b2b.components.LoadingView
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.SetActiveOrganization
import com.stytch.sdk.ui.b2b.extensions.jitEligible
import com.stytch.sdk.ui.b2b.extensions.shouldAllowDirectLoginToOrganization
import com.stytch.sdk.ui.b2b.extensions.toInternalOrganizationData
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.theme.LocalStytchB2BProductConfig
import com.stytch.sdk.ui.shared.theme.LocalStytchBootstrapData
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography

@Composable
internal fun DiscoveryScreen(viewModel: DiscoveryScreenViewModel) {
    val config = LocalStytchB2BProductConfig.current
    val shouldDirectLoginConfigEnabled = config.directLoginForSingleMembership?.status == true
    val createOrganzationsEnabled = LocalStytchBootstrapData.current.createOrganizationEnabled
    val context = LocalActivity.current as Activity
    val shouldAutomaticallyCreateOrganization =
        createOrganzationsEnabled && config.directCreateOrganizationForNoMembership
    val discoveryScreenState = viewModel.discoveryScreenState.collectAsStateWithLifecycle()

    fun handleDiscoveryOrganizationStart(discoveredOrganization: DiscoveredOrganization) {
        val organization = discoveredOrganization.organization
        if (discoveredOrganization.primaryRequired != null) {
            val allowedAuthMethods = discoveredOrganization.primaryRequired.allowedAuthMethods ?: emptyList()
            if (allowedAuthMethods.firstOrNull() === AllowedAuthMethods.SSO &&
                organization.ssoDefaultConnectionId != null
            ) {
                viewModel.handleAction(DiscoveryScreenActions.StartSSO(context, organization.ssoDefaultConnectionId))
            } else {
                viewModel.dispatch(SetActiveOrganization(organization.toInternalOrganizationData()))
                viewModel.handleAction(
                    DiscoveryScreenActions.ExchangeSessionForOrganization(organizationId = organization.organizationId),
                )
            }
        } else {
            if (discoveryScreenState.value.isExchanging) return
            viewModel.handleAction(
                DiscoveryScreenActions.ExchangeSessionForOrganization(organizationId = organization.organizationId),
            )
        }
    }

    fun handleDiscoveryOrganizationCreate() {
        if (discoveryScreenState.value.isCreating) return
        viewModel.handleAction(DiscoveryScreenActions.CreateOrganization)
    }

    BackHandler(enabled = true) {
        viewModel.dispatch(ResetEverything)
    }

    LaunchedEffect(shouldDirectLoginConfigEnabled, discoveryScreenState.value.discoveredOrganizations) {
        val directLoginOrganization =
            (discoveryScreenState.value.discoveredOrganizations ?: emptyList()).shouldAllowDirectLoginToOrganization(
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

    return DiscoveryScreenComposable(
        discoveryScreenState = discoveryScreenState.value,
        createOrganzationsEnabled = createOrganzationsEnabled,
        handleCreateOrganization = ::handleDiscoveryOrganizationCreate,
        handleDiscoveryOrganizationStart = ::handleDiscoveryOrganizationStart,
        handleAction = viewModel::handleAction,
    )
}

@Composable
private fun DiscoveryScreenComposable(
    discoveryScreenState: DiscoveryScreenState,
    createOrganzationsEnabled: Boolean,
    handleCreateOrganization: () -> Unit = {},
    handleDiscoveryOrganizationStart: (DiscoveredOrganization) -> Unit = {},
    handleAction: (DiscoveryScreenActions) -> Unit = {},
) {
    val theme = LocalStytchTheme.current

    Column {
        if (discoveryScreenState.isExchanging) {
            return LoggingInView(color = Color(theme.inputTextColor))
        }

        if (discoveryScreenState.isCreating) {
            return LoadingView(color = Color(theme.inputTextColor))
        }

        if (discoveryScreenState.discoveredOrganizations.isNullOrEmpty()) {
            return NoOrganizationsDiscovered(
                emailState = discoveryScreenState.emailState,
                createOrganzationsEnabled = createOrganzationsEnabled,
                onCreateOrganization = { handleAction(DiscoveryScreenActions.CreateOrganization) },
                onGoBack = { handleAction(DiscoveryScreenActions.DispatchGlobalAction(ResetEverything)) },
            )
        }
        BackButton(onClick = { handleAction(DiscoveryScreenActions.DispatchGlobalAction(ResetEverything)) })
        PageTitle(textAlign = TextAlign.Left, text = "Select an organization to continue")
        Column(modifier = Modifier.fillMaxWidth()) {
            discoveryScreenState.discoveredOrganizations.map { discoveredOrganization ->
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
                    onClick = handleCreateOrganization,
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
private fun NoOrganizationsDiscovered(
    emailState: EmailState,
    createOrganzationsEnabled: Boolean,
    onCreateOrganization: () -> Unit,
    onGoBack: () -> Unit,
) {
    val config = LocalStytchB2BProductConfig.current

    if (createOrganzationsEnabled && config.allowCreateOrganization) {
        PageTitle(textAlign = TextAlign.Left, text = "Create an organization to get started")
        StytchButton(enabled = true, text = "Create an organization", onClick = onCreateOrganization)
        Spacer(modifier = Modifier.height(16.dp))
        BodyText(
            text =
                "${emailState.emailAddress} does not have an account. Think this is a mistake? " +
                    "Try a different email address, or contact your admin.",
        )
        StytchButton(enabled = true, onClick = onGoBack, text = "Try a different email address")
        return
    }

    PageTitle(
        textAlign = TextAlign.Left,
        text = "${emailState.emailAddress} does not belong to any organizations.",
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

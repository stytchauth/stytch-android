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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.stytch.sdk.R
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
                viewModel.handle(DiscoveryScreenActions.StartSSO(context, organization.ssoDefaultConnectionId))
            } else {
                viewModel.dispatch(SetActiveOrganization(organization.toInternalOrganizationData()))
                viewModel.handle(
                    DiscoveryScreenActions.ExchangeSessionForOrganization(organizationId = organization.organizationId),
                )
            }
        } else {
            if (discoveryScreenState.value.isExchanging) return
            viewModel.handle(
                DiscoveryScreenActions.ExchangeSessionForOrganization(organizationId = organization.organizationId),
            )
        }
    }

    fun handleDiscoveryOrganizationCreate() {
        if (discoveryScreenState.value.isCreating) return
        viewModel.handle(DiscoveryScreenActions.CreateOrganization)
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
        dispatch = viewModel::handle,
    )
}

@Composable
private fun DiscoveryScreenComposable(
    discoveryScreenState: DiscoveryScreenState,
    createOrganzationsEnabled: Boolean,
    handleCreateOrganization: () -> Unit = {},
    handleDiscoveryOrganizationStart: (DiscoveredOrganization) -> Unit = {},
    dispatch: (DiscoveryScreenActions) -> Unit = {},
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
                onCreateOrganization = { dispatch(DiscoveryScreenActions.CreateOrganization) },
                onGoBack = { dispatch(DiscoveryScreenActions.ResetEverything) },
            )
        }
        BackButton(onClick = { dispatch(DiscoveryScreenActions.ResetEverything) })
        PageTitle(textAlign = TextAlign.Left, text = stringResource(R.string.stytch_b2b_discovery_title))
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
                        modifier = Modifier.weight(0.9F),
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
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
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
                    text = stringResource(R.string.stytch_b2b_button_create_an_organization),
                    enabled = true,
                    onClick = handleCreateOrganization,
                )
            }
        }
    }
}

@Composable
private fun LoggingInView(color: Color) {
    PageTitle(textAlign = TextAlign.Left, text = stringResource(R.string.stytch_b2b_logging_in_title))
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
        PageTitle(
            textAlign = TextAlign.Left,
            text =
                stringResource(
                    R.string.stytch_b2b_discovery_no_organizations_create_one_title,
                ),
        )
        StytchButton(
            enabled = true,
            text = stringResource(R.string.stytch_b2b_button_create_an_organization),
            onClick = onCreateOrganization,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BodyText(
            text =
                stringResource(
                    R.string.stytch_b2b_discovery_no_organizations_create_one_body,
                    emailState.emailAddress,
                ),
        )
        StytchButton(
            enabled = true,
            onClick = onGoBack,
            text = stringResource(R.string.stytch_b2b_discovery_button_try_a_different_email_address),
        )
        return
    }

    PageTitle(
        textAlign = TextAlign.Left,
        text = stringResource(R.string.stytch_b2b_discovery_no_organizations_title, emailState.emailAddress),
    )
    BodyText(text = stringResource(R.string.stytch_b2b_discovery_no_organizations_body))
    StytchButton(
        enabled = true,
        onClick = onGoBack,
        text = stringResource(R.string.stytch_b2b_discovery_button_try_a_different_email_address),
    )
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
            contentScale = ContentScale.FillBounds,
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
            stringResource(R.string.stytch_b2b_discovery_organization_arrow_join)
        } else if (type == "invited_member") {
            stringResource(R.string.stytch_b2b_discovery_organization_arrow_invite)
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

package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.extensions.jitEligible
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.theme.LocalStytchB2BProductConfig
import com.stytch.sdk.ui.shared.theme.LocalStytchBootstrapData
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import kotlinx.coroutines.flow.StateFlow

internal class DiscoveryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction, productConfig)

@Composable
internal fun DiscoveryScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<DiscoveryScreenViewModel>,
    viewModel: DiscoveryScreenViewModel = createViewModel(DiscoveryScreenViewModel::class.java),
) {
}

@Composable
private fun LoggingInView(color: Color) {
    PageTitle(text = "Logging In...")
    CircularProgressIndicator(color = color)
}

@Composable
private fun LoadingView(color: Color) {
    CircularProgressIndicator(color = color)
}

@Composable
private fun NoOrganizationsDiscovered(
    state: State<B2BUIState>,
    onCreateOrganization: () -> Unit,
    onGoBack: () -> Unit,
) {
    val config = LocalStytchB2BProductConfig.current
    val theme = LocalStytchTheme.current
    val createOrganzationsEnabled = LocalStytchBootstrapData.current.createOrganizationEnabled
    var isCreateLoading by remember { mutableStateOf(false) }

    fun handleDiscoveryOrganizationCreate() {
        if (isCreateLoading) return
        isCreateLoading = true
        onCreateOrganization()
        isCreateLoading = false
    }

    if (isCreateLoading) {
        LoadingView(color = Color(theme.inputTextColor))
        return
    }

    if (createOrganzationsEnabled && !config.disableCreateOrganization) {
        PageTitle(text = "Create an organization to get started")
        StytchButton(enabled = true, text = "Create an organization", onClick = ::handleDiscoveryOrganizationCreate)
        BodyText(
            text =
                "${state.value.emailState.emailAddress} does not have an account. Think this is a mistake?" +
                    "Try a different email address, or contact your admin.",
        )
        return
    }

    PageTitle(text = "${state.value.emailState.emailAddress} does not belong to any organizations.")
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
    val text =
        if (jitEligible.contains(type) || type == "pending_member") {
            "Join"
        } else if (type == "invited_member") {
            "Accept Invite"
        } else {
            ""
        }
    Text(text = text, color = Color(theme.primaryTextColor))
}

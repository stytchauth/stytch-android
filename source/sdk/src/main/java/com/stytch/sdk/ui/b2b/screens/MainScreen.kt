package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ProductComponent
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.data.generateProductComponentsOrdering
import com.stytch.sdk.ui.b2b.usecases.UseEffectiveAuthConfig
import com.stytch.sdk.ui.b2b.usecases.UsePasswordsStrengthCheck
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPassword
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.DividerWithText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import kotlinx.coroutines.flow.StateFlow

internal class MainScreenViewModel(
    state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction, productConfig) {
    val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    val useUpdateMemberPassword = UseUpdateMemberPassword(state, ::dispatch)
    val usePasswordsStrengthCheck = UsePasswordsStrengthCheck(viewModelScope, state, ::dispatch, ::request)
    val useGetEffectiveAuthConfig = UseEffectiveAuthConfig(state, productConfig)
}

@Composable
internal fun MainScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<MainScreenViewModel>,
    viewModel: MainScreenViewModel = createViewModel(MainScreenViewModel::class.java),
) {
    val primaryAuthMethods = state.value.primaryAuthMethods
    val emailAddress = state.value.emailState.emailAddress
    val emailVerified = state.value.emailState.emailVerified
    val authFlowType = state.value.authFlowType
    val organization = state.value.activeOrganization
    val products = viewModel.useGetEffectiveAuthConfig.products.toList()
    val productComponentsOrdering = products.generateProductComponentsOrdering(authFlowType, organization)
    val title =
        when (authFlowType) {
            AuthFlowType.DISCOVERY -> "Sign up or log in"
            AuthFlowType.ORGANIZATION -> "Continue to ${organization?.organizationName ?: "..."}"
        }
    val showVerifyEmailCopy = emailAddress.isNotEmpty() && emailVerified == false && primaryAuthMethods.isNotEmpty()
    val theme = LocalStytchTheme.current
    Column {
        Row {
            AsyncImage(
                model = theme.logoUrl,
                contentDescription = null,
                modifier = Modifier.sizeIn(minWidth = 0.dp, maxWidth = 100.dp, minHeight = 0.dp, maxHeight = 50.dp),
            )
            AsyncImage(
                model = organization?.organizationLogoUrl,
                contentDescription = null,
                modifier = Modifier.sizeIn(minWidth = 0.dp, maxWidth = 100.dp, minHeight = 0.dp, maxHeight = 50.dp),
            )
        }
        if (!theme.hideHeaderText) {
            if (showVerifyEmailCopy) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    PageTitle(text = "Verify your email")
                    Spacer(modifier = Modifier.height(24.dp))
                    BodyText(text = "Confirm your email address with one of the following:")
                }
            } else {
                PageTitle(text = title)
            }
        }
        productComponentsOrdering.map { productComponent: ProductComponent ->
            when (productComponent) {
                ProductComponent.MagicLinkEmailForm -> BodyText(text = "MagicLinkEmailForm")
                ProductComponent.MagicLinkEmailDiscoveryForm -> BodyText(text = "MagicLinkEmailDiscoveryForm")
                ProductComponent.OAuthButtons -> BodyText(text = "OAuth buttons")
                ProductComponent.SSOButtons -> BodyText(text = "SSO buttons")
                ProductComponent.PasswordsEmailForm -> BodyText(text = "PasswordsEmailForm")
                ProductComponent.PasswordEMLCombined -> BodyText(text = "PasswordEMLCombined")
                ProductComponent.Divider -> DividerWithText(text = "or")
            }
        }
    }
}

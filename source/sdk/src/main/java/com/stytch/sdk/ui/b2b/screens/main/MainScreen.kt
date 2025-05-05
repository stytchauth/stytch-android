package com.stytch.sdk.ui.b2b.screens.main

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.ProductComponent
import com.stytch.sdk.ui.b2b.extensions.generateProductComponentsOrdering
import com.stytch.sdk.ui.b2b.extensions.toPainterResource
import com.stytch.sdk.ui.b2b.extensions.toTitle
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.DividerWithText
import com.stytch.sdk.ui.shared.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.shared.components.EmailEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.SocialLoginButton
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.utils.getStyledText

@Composable
internal fun MainScreen(viewModel: MainScreenViewModel) {
    val state = viewModel.mainScreenState.collectAsState()
    MainScreenComposable(
        state = state.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun MainScreenComposable(
    state: MainScreenState,
    dispatch: (MainScreenAction) -> Unit,
) {
    val primaryAuthMethods = state.primaryAuthMethods
    val emailAddress = state.emailState.emailAddress
    val emailVerified = state.emailState.emailVerified
    val authFlowType = state.authFlowType
    val organization = state.organizationData
    val productComponentsOrdering = state.products.generateProductComponentsOrdering(authFlowType, organization)
    val title =
        when (authFlowType) {
            AuthFlowType.DISCOVERY -> stringResource(R.string.stytch_b2b_sign_up_or_log_in)
            AuthFlowType.ORGANIZATION ->
                stringResource(
                    R.string.stytch_b2b_continue_to_org_name,
                    organization?.organizationName ?: "...",
                )
        }
    val showVerifyEmailCopy = emailAddress.isNotEmpty() && emailVerified == false && primaryAuthMethods.isNotEmpty()
    val theme = LocalStytchTheme.current
    val activity = LocalActivity.current as Activity
    val context = LocalContext.current
    if (state.products.isEmpty()) {
        dispatch(MainScreenAction.SetB2BError(B2BErrorType.NoAuthenticationMethodsFound))
        return
    }
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
                    PageTitle(text = stringResource(R.string.stytch_b2b_verify_your_email))
                    Spacer(modifier = Modifier.height(24.dp))
                    BodyText(
                        text =
                            stringResource(
                                R.string.stytch_b2b_confirm_your_email_address_with_one_of_the_following,
                            ),
                    )
                }
            } else {
                PageTitle(text = title)
            }
        }
        productComponentsOrdering.map { productComponent: ProductComponent ->
            when (productComponent) {
                ProductComponent.EmailForm -> {
                    EmailEntry(
                        emailState = state.emailState,
                        onEmailAddressChanged = { dispatch(MainScreenAction.UpdateMemberEmailAddress(it)) },
                        onEmailAddressSubmit = { dispatch(MainScreenAction.HandleEmailSubmit) },
                        keyboardActions =
                            KeyboardActions(onDone = {
                                dispatch(MainScreenAction.SetEmailShouldBeValidated)
                                dispatch(MainScreenAction.HandleEmailSubmit)
                            }),
                    )
                }
                ProductComponent.EmailDiscoveryForm -> {
                    EmailEntry(
                        emailState = state.emailState,
                        onEmailAddressChanged = { dispatch(MainScreenAction.UpdateMemberEmailAddress(it)) },
                        onEmailAddressSubmit = { dispatch(MainScreenAction.HandleEmailSubmit) },
                        keyboardActions =
                            KeyboardActions(onDone = {
                                dispatch(MainScreenAction.SetEmailShouldBeValidated)
                                dispatch(MainScreenAction.HandleEmailSubmit)
                            }),
                    )
                }
                ProductComponent.OAuthButtons -> {
                    state.oauthProviderSettings.map { provider ->
                        SocialLoginButton(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            iconDrawable = provider.type.toPainterResource(),
                            iconDescription = provider.type.name,
                            text = stringResource(R.string.stytch_b2b_continue_with_provider, provider.type.toTitle()),
                            onClick = { dispatch(MainScreenAction.StartOAuth(activity, provider)) },
                        )
                    }
                }
                ProductComponent.SSOButtons -> {
                    if (authFlowType == AuthFlowType.DISCOVERY) {
                        SocialLoginButton(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            text = stringResource(R.string.stytch_b2b_continue_with_sso),
                            iconDrawable = painterResource(R.drawable.sso),
                            iconDescription = "SSO",
                            onClick = { dispatch(MainScreenAction.StartSSODiscovery) },
                        )
                    } else {
                        organization?.ssoActiveConnections?.map { provider ->
                            SocialLoginButton(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                text = stringResource(R.string.stytch_b2b_continue_with_provider),
                                iconDrawable = provider.toPainterResource(),
                                iconDescription = provider.displayName,
                                onClick = {
                                    dispatch(
                                        MainScreenAction.StartSSO(
                                            activity,
                                            provider.connectionId,
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }
                ProductComponent.PasswordsEmailForm -> {
                    EmailAndPasswordEntry(
                        emailState = state.emailState,
                        onEmailAddressChanged = { dispatch(MainScreenAction.UpdateMemberEmailAddress(it)) },
                        passwordState = state.passwordState,
                        onPasswordChanged = { dispatch(MainScreenAction.UpdateMemberPassword(it)) },
                        onSubmit = { dispatch(MainScreenAction.HandlePasswordSubmit) },
                        onEmailAddressDone = { dispatch(MainScreenAction.SetEmailShouldBeValidated) },
                    )
                    BodyText(
                        text = context.getStyledText(R.string.stytch_b2b_having_trouble),
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp)
                                .clickable {
                                    dispatch(MainScreenAction.GoToPasswordForgot)
                                },
                    )
                }
                ProductComponent.PasswordEMLCombined -> {
                    EmailEntry(
                        emailState = state.emailState,
                        onEmailAddressChanged = { dispatch(MainScreenAction.UpdateMemberEmailAddress(it)) },
                        onEmailAddressSubmit = { dispatch(MainScreenAction.HandleEmailSubmit) },
                        keyboardActions =
                            KeyboardActions(onDone = {
                                dispatch(MainScreenAction.SetEmailShouldBeValidated)
                                dispatch(MainScreenAction.HandleEmailSubmit)
                            }),
                    )
                    StytchTextButton(text = stringResource(R.string.stytch_b2b_use_a_password_instead)) {
                        dispatch(MainScreenAction.GoToPasswordAuthenticate)
                    }
                }
                ProductComponent.Divider -> DividerWithText(text = stringResource(R.string.stytch_method_divider_text))
            }
        }
    }
}

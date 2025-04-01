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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.ProductComponent
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.extensions.generateProductComponentsOrdering
import com.stytch.sdk.ui.b2b.extensions.toPainterResource
import com.stytch.sdk.ui.b2b.extensions.toTitle
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.DividerWithText
import com.stytch.sdk.ui.shared.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.shared.components.EmailEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.SocialLoginButton
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun MainScreen(viewModel: MainScreenViewModel) {
    val state = viewModel.mainScreenState.collectAsState()
    MainScreenComposable(
        state = state.value,
        action = viewModel::handleAction,
    )
}

@Composable
private fun MainScreenComposable(
    state: MainScreenState,
    action: (MainScreenAction) -> Unit,
) {
    val primaryAuthMethods = state.primaryAuthMethods
    val emailAddress = state.emailState.emailAddress
    val emailVerified = state.emailState.emailVerified
    val authFlowType = state.authFlowType
    val organization = state.organizationData
    val productComponentsOrdering = state.products.generateProductComponentsOrdering(authFlowType, organization)
    val title =
        when (authFlowType) {
            AuthFlowType.DISCOVERY -> "Sign up or log in"
            AuthFlowType.ORGANIZATION -> "Continue to ${organization?.organizationName ?: "..."}"
        }
    val showVerifyEmailCopy = emailAddress.isNotEmpty() && emailVerified == false && primaryAuthMethods.isNotEmpty()
    val theme = LocalStytchTheme.current
    val context = LocalActivity.current as Activity
    if (state.products.isEmpty()) {
        action(MainScreenAction.DispatchGlobalAction(SetB2BError(B2BErrorType.NoAuthenticationMethodsFound)))
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
                ProductComponent.EmailForm -> {
                    EmailEntry(
                        emailState = state.emailState,
                        onEmailAddressChanged = {
                            action(
                                MainScreenAction.UpdateMemberEmailAddress(
                                    it,
                                ),
                            )
                        },
                        onEmailAddressSubmit = { action(MainScreenAction.HandleEmailSubmit) },
                        keyboardActions =
                            KeyboardActions(onDone = {
                                action(MainScreenAction.SetEmailShouldBeValidated)
                                action(MainScreenAction.HandleEmailSubmit)
                            }),
                    )
                }
                ProductComponent.EmailDiscoveryForm -> {
                    EmailEntry(
                        emailState = state.emailState,
                        onEmailAddressChanged = {
                            action(
                                MainScreenAction.UpdateMemberEmailAddress(
                                    it,
                                ),
                            )
                        },
                        onEmailAddressSubmit = { action(MainScreenAction.HandleEmailSubmit) },
                        keyboardActions =
                            KeyboardActions(onDone = {
                                action(MainScreenAction.SetEmailShouldBeValidated)
                                action(MainScreenAction.HandleEmailSubmit)
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
                            text = "Continue with ${provider.type.toTitle()}",
                            onClick = { action(MainScreenAction.StartOAuth(context, provider)) },
                        )
                    }
                }
                ProductComponent.SSOButtons -> {
                    if (authFlowType == AuthFlowType.DISCOVERY) {
                        SocialLoginButton(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            text = "Continue with SSO",
                            iconDrawable = painterResource(R.drawable.sso),
                            iconDescription = "SSO",
                            onClick = { action(MainScreenAction.StartSSODiscovery) },
                        )
                    } else {
                        organization?.ssoActiveConnections?.map { provider ->
                            SocialLoginButton(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                text = "Continue with ${provider.displayName}",
                                iconDrawable = provider.toPainterResource(),
                                iconDescription = provider.displayName,
                                onClick = {
                                    action(
                                        MainScreenAction.StartSSO(
                                            context,
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
                        onEmailAddressChanged = {
                            action(
                                MainScreenAction.UpdateMemberEmailAddress(
                                    it,
                                ),
                            )
                        },
                        passwordState = state.passwordState,
                        onPasswordChanged = { action(MainScreenAction.UpdateMemberPassword(it)) },
                        onSubmit = { action(MainScreenAction.HandlePasswordSubmit) },
                        onEmailAddressDone = { action(MainScreenAction.SetEmailShouldBeValidated) },
                    )
                    val signInText =
                        buildAnnotatedString {
                            append("Having trouble signing in? ")
                            append(
                                AnnotatedString(
                                    text = "Get help",
                                    spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
                                ),
                            )
                        }
                    BodyText(
                        text = signInText,
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp)
                                .clickable {
                                    action(
                                        MainScreenAction.DispatchGlobalAction(SetNextRoute(Routes.PasswordForgot)),
                                    )
                                },
                    )
                }
                ProductComponent.PasswordEMLCombined -> {
                    EmailEntry(
                        emailState = state.emailState,
                        onEmailAddressChanged = {
                            action(
                                MainScreenAction.UpdateMemberEmailAddress(
                                    it,
                                ),
                            )
                        },
                        onEmailAddressSubmit = { action(MainScreenAction.HandleEmailSubmit) },
                        keyboardActions =
                            KeyboardActions(onDone = {
                                action(MainScreenAction.SetEmailShouldBeValidated)
                                action(MainScreenAction.HandleEmailSubmit)
                            }),
                    )
                    StytchTextButton(text = "Use a password instead") {
                        action(MainScreenAction.DispatchGlobalAction(SetNextRoute(Routes.PasswordAuthenticate)))
                    }
                }
                ProductComponent.Divider -> DividerWithText(text = "or")
            }
        }
    }
}

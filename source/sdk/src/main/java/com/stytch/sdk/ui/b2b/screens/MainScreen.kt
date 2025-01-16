package com.stytch.sdk.ui.b2b.screens

import android.app.Activity
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
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.B2BOAuthProviders
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ProductComponent
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetLoading
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProduct
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.data.generateProductComponentsOrdering
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseEffectiveAuthConfig
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPDiscoverySend
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPLoginOrSignup
import com.stytch.sdk.ui.b2b.usecases.UseMagicLinksDiscoverySend
import com.stytch.sdk.ui.b2b.usecases.UseMagicLinksEmailLoginOrSignup
import com.stytch.sdk.ui.b2b.usecases.UseNonMemberPasswordReset
import com.stytch.sdk.ui.b2b.usecases.UseOAuthStart
import com.stytch.sdk.ui.b2b.usecases.UsePasswordAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UsePasswordDiscoveryAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseSSOStart
import com.stytch.sdk.ui.b2b.usecases.UseSearchMember
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailShouldBeValidated
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPassword
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.DividerWithText
import com.stytch.sdk.ui.shared.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.shared.components.EmailEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.SocialLoginButton
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class MainScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    val useGetEffectiveAuthConfig = UseEffectiveAuthConfig(state, productConfig)
    val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    val useUpdateMemberPassword = UseUpdateMemberPassword(state, ::dispatch)
    val useMagicLinksEmailLoginOrSignup =
        UseMagicLinksEmailLoginOrSignup(viewModelScope, state, ::dispatch, productConfig, ::request)
    val useMagicLinksDiscoverySend =
        UseMagicLinksDiscoverySend(viewModelScope, productConfig, state, ::dispatch, ::request)
    val useSSOStart = UseSSOStart()
    val useOAuthStart = UseOAuthStart(state)
    val useSearchMember = UseSearchMember(::request)
    val usePasswordsAuthenticate = UsePasswordAuthenticate(viewModelScope, state, ::dispatch, productConfig, ::request)
    val usePasswordDiscoveryAuthenticate =
        UsePasswordDiscoveryAuthenticate(viewModelScope, state, ::dispatch, ::request)
    val useNonMemberPasswordReset =
        UseNonMemberPasswordReset(viewModelScope, state, ::dispatch, productConfig, ::request)
    val useUpdateMemberEmailShouldBeValidated = UseUpdateMemberEmailShouldBeValidated(state, ::dispatch)
    val useEmailOTPLoginOrSignup = UseEmailOTPLoginOrSignup(viewModelScope, state, ::dispatch, productConfig, ::request)
    val useEmailOTPDiscoverySend = UseEmailOTPDiscoverySend(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val enableEml = productConfig.products.contains(StytchB2BProduct.EMAIL_MAGIC_LINKS)
    private val enableOtp = productConfig.products.contains(StytchB2BProduct.EMAIL_OTP)

    fun handleEmailPasswordSubmit() {
        val emailAddress = state.value.emailState.emailAddress
        val organization = state.value.activeOrganization
        if (emailAddress.isBlank()) return
        viewModelScope.launch {
            dispatch(SetLoading(true))
            if (organization != null) {
                useSearchMember(
                    emailAddress = emailAddress,
                    organizationId = organization.organizationId,
                ).onSuccess { response ->
                    dispatch(SetLoading(false))
                    if (!response.member?.memberPasswordId.isNullOrEmpty()) {
                        usePasswordsAuthenticate()
                    } else {
                        useNonMemberPasswordReset()
                    }
                }.onFailure {
                    dispatch(SetLoading(false))
                }
            } else {
                usePasswordDiscoveryAuthenticate()
            }
        }
    }

    fun handleEmailSubmit() {
        if (state.value.activeOrganization != null || state.value.mfaPrimaryInfoState != null) {
            if (enableEml && enableOtp) {
                dispatch(SetNextRoute(Routes.EmailMethodSelection))
            } else if (enableEml) {
                useMagicLinksEmailLoginOrSignup()
            } else if (enableOtp) {
                useEmailOTPLoginOrSignup()
            }
        } else {
            if (enableEml && enableOtp) {
                dispatch(SetNextRoute(Routes.EmailMethodSelection))
            } else if (enableEml) {
                useMagicLinksDiscoverySend()
            } else if (enableOtp) {
                useEmailOTPDiscoverySend()
            }
        }
    }
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
    val oauthProviderSettings = viewModel.useGetEffectiveAuthConfig.oauthProviderSettings.toList()
    val productComponentsOrdering = products.generateProductComponentsOrdering(authFlowType, organization)
    val title =
        when (authFlowType) {
            AuthFlowType.DISCOVERY -> "Sign up or log in"
            AuthFlowType.ORGANIZATION -> "Continue to ${organization?.organizationName ?: "..."}"
        }
    val showVerifyEmailCopy = emailAddress.isNotEmpty() && emailVerified == false && primaryAuthMethods.isNotEmpty()
    val theme = LocalStytchTheme.current
    val context = LocalContext.current as Activity
    if (products.isEmpty()) {
        viewModel.dispatch(SetB2BError(B2BErrorType.NoAuthenticationMethodsFound))
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
                        emailState = state.value.emailState,
                        onEmailAddressChanged = { viewModel.useUpdateMemberEmailAddress(it) },
                        onEmailAddressSubmit = { viewModel.handleEmailSubmit() },
                        keyboardActions =
                            KeyboardActions(onDone = {
                                viewModel.useUpdateMemberEmailShouldBeValidated(true)
                                viewModel.handleEmailSubmit()
                            }),
                    )
                }
                ProductComponent.EmailDiscoveryForm -> {
                    EmailEntry(
                        emailState = state.value.emailState,
                        onEmailAddressChanged = { viewModel.useUpdateMemberEmailAddress(it) },
                        onEmailAddressSubmit = { viewModel.handleEmailSubmit() },
                        keyboardActions =
                            KeyboardActions(onDone = {
                                viewModel.useUpdateMemberEmailShouldBeValidated(true)
                                viewModel.handleEmailSubmit()
                            }),
                    )
                }
                ProductComponent.OAuthButtons -> {
                    oauthProviderSettings.map { provider ->
                        SocialLoginButton(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            iconDrawable = provider.type.toPainterResource(),
                            iconDescription = provider.type.name,
                            text = "Continue with ${provider.type.toTitle()}",
                            onClick = { viewModel.useOAuthStart(context = context, providerConfig = provider) },
                        )
                    }
                }
                ProductComponent.SSOButtons -> {
                    organization?.ssoActiveConnections?.map { provider ->
                        SocialLoginButton(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            text = "Continue with ${provider.displayName}",
                            onClick = { viewModel.useSSOStart(context, provider.connectionId) },
                        )
                    }
                }
                ProductComponent.PasswordsEmailForm -> {
                    EmailAndPasswordEntry(
                        emailState = state.value.emailState,
                        onEmailAddressChanged = { viewModel.useUpdateMemberEmailAddress(it) },
                        passwordState = state.value.passwordState,
                        onPasswordChanged = { viewModel.useUpdateMemberPassword(it) },
                        onSubmit = viewModel::handleEmailPasswordSubmit,
                        onEmailAddressDone = { viewModel.useUpdateMemberEmailShouldBeValidated(true) },
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
                                    viewModel.dispatch(SetNextRoute(Routes.PasswordForgot))
                                },
                    )
                }
                ProductComponent.PasswordEMLCombined -> {
                    EmailEntry(
                        emailState = state.value.emailState,
                        onEmailAddressChanged = { viewModel.useUpdateMemberEmailAddress(it) },
                        onEmailAddressSubmit = { viewModel.handleEmailSubmit() },
                        keyboardActions =
                            KeyboardActions(onDone = {
                                viewModel.useUpdateMemberEmailShouldBeValidated(true)
                                viewModel.handleEmailSubmit()
                            }),
                    )
                    StytchTextButton(text = "Use a password instead") {
                        viewModel.dispatch(SetNextRoute(Routes.PasswordAuthenticate))
                    }
                }
                ProductComponent.Divider -> DividerWithText(text = "or")
            }
        }
    }
}

@Composable
private fun B2BOAuthProviders.toPainterResource(): Painter =
    when (this) {
        B2BOAuthProviders.GOOGLE -> painterResource(id = R.drawable.google)
        B2BOAuthProviders.MICROSOFT -> painterResource(id = R.drawable.microsoft)
        B2BOAuthProviders.GITHUB -> painterResource(id = R.drawable.github)
        B2BOAuthProviders.SLACK -> painterResource(id = R.drawable.slack)
        B2BOAuthProviders.HUBSPOT -> painterResource(id = R.drawable.hubspot)
    }

private fun B2BOAuthProviders.toTitle(): String =
    when (this) {
        B2BOAuthProviders.GOOGLE -> "Google"
        B2BOAuthProviders.MICROSOFT -> "Microsoft"
        B2BOAuthProviders.GITHUB -> "Github"
        B2BOAuthProviders.SLACK -> "Slack"
        B2BOAuthProviders.HUBSPOT -> "Hubspot"
    }

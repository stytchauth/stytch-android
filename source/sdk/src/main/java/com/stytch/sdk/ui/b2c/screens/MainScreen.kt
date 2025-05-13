package com.stytch.sdk.ui.b2c.screens

import android.os.Parcelable
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.R
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.biometrics.Biometrics
import com.stytch.sdk.ui.b2c.AuthenticationActivity
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.OAuthProvider
import com.stytch.sdk.ui.b2c.data.OTPOptions
import com.stytch.sdk.ui.b2c.data.StytchProductConfig
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.DividerWithText
import com.stytch.sdk.ui.shared.components.EmailEntry
import com.stytch.sdk.ui.shared.components.LoadingDialog
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.PhoneEntry
import com.stytch.sdk.ui.shared.components.SocialLoginButton
import com.stytch.sdk.ui.shared.theme.LocalStytchProductConfig
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
internal object MainScreen : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val productConfig = LocalStytchProductConfig.current
        val context = LocalActivity.current as AuthenticationActivity
        val viewModel =
            viewModel<MainScreenViewModel>(
                factory = MainScreenViewModel.factory(context.savedStateHandle),
            )
        val uiState = viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.eventFlow.collectLatest {
                when (it) {
                    is EventState.NavigationRequested -> navigator.push(it.navigationRoute.screen)
                    is EventState.Authenticated -> context.returnAuthenticationResult(it.result)
                    else -> {}
                }
            }
        }
        MainScreenComposable(
            uiState = uiState.value,
            onStartOAuthLogin = { provider, config -> viewModel.onStartOAuthLogin(context, provider, config) },
            onEmailAddressChanged = viewModel::onEmailAddressChanged,
            onEmailAddressSubmit = viewModel::onEmailAddressSubmit,
            onCountryCodeChanged = viewModel::onCountryCodeChanged,
            onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
            sendSmsOtp = { viewModel.sendSmsOTP(it, productConfig.locale) },
            sendWhatsAppOTP = { viewModel.sendWhatsAppOTP(it, productConfig.locale) },
            exitWithoutAuthenticating = context::exitWithoutAuthenticating,
            productComponents = viewModel.getProductComponents(productConfig.products, context),
            tabTypes = viewModel.getTabTitleOrdering(productConfig.products, productConfig.otpOptions.methods),
        )
    }
}

@Composable
private fun MainScreenComposable(
    uiState: ApplicationUIState,
    onStartOAuthLogin: (OAuthProvider, StytchProductConfig) -> Unit,
    onEmailAddressChanged: (String) -> Unit,
    onEmailAddressSubmit: (StytchProductConfig) -> Unit,
    onCountryCodeChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    sendSmsOtp: (OTPOptions) -> Unit,
    sendWhatsAppOTP: (OTPOptions) -> Unit,
    exitWithoutAuthenticating: () -> Unit,
    productComponents: List<ProductComponent>,
    tabTypes: List<TabTypes>,
) {
    val productConfig = LocalStytchProductConfig.current
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    val tabTitles =
        tabTypes.map {
            when (it) {
                TabTypes.EMAIL -> stringResource(id = R.string.stytch_email_label)
                TabTypes.SMS -> stringResource(id = R.string.stytch_b2c_sms_tab_title)
                TabTypes.WHATSAPP -> stringResource(id = R.string.stytch_b2c_whatsapp_tab_title)
            }
        }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val phoneState = uiState.phoneNumberState
    val emailState = uiState.emailState
    val semanticsOAuthButton = stringResource(id = R.string.stytch_b2c_semantics_oauth_button)
    val context = LocalActivity.current as FragmentActivity
    val scope = rememberCoroutineScope()

    fun loginWithBiometrics() {
        scope.launch {
            StytchClient.biometrics.authenticate(
                Biometrics.AuthenticateParameters(
                    context = context,
                ),
            )
        }
    }

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        BackButton { exitWithoutAuthenticating() }
        if (!theme.hideHeaderText) {
            PageTitle(text = stringResource(id = R.string.stytch_b2c_main_screen_header))
        }
        productComponents.map {
            when (it) {
                ProductComponent.BUTTONS -> {
                    productConfig.oAuthOptions.providers.map {
                        SocialLoginButton(
                            modifier =
                                Modifier
                                    .padding(bottom = 12.dp)
                                    .semantics {
                                        contentDescription = semanticsOAuthButton
                                    },
                            onClick = { onStartOAuthLogin(it, productConfig) },
                            iconDrawable = painterResource(id = it.iconDrawable),
                            iconDescription = stringResource(id = it.iconText),
                            text = stringResource(id = it.text),
                        )
                    }
                }
                ProductComponent.BIOMETRICS -> {
                    SocialLoginButton(
                        modifier = Modifier.padding(bottom = 12.dp),
                        onClick = ::loginWithBiometrics,
                        imageVector = Icons.Default.Fingerprint,
                        text = stringResource(R.string.stytch_b2c_continue_with_biometrics),
                    )
                }
                ProductComponent.DIVIDER -> {
                    DividerWithText(
                        modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                        text = stringResource(id = R.string.stytch_method_divider_text),
                    )
                }
                ProductComponent.INPUTS -> {
                    if (tabTitles.size > 1) {
                        val semanticTabs = stringResource(id = R.string.stytch_b2c_semantics_tabs)
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color(theme.backgroundColor),
                            modifier =
                                Modifier
                                    .padding(bottom = 12.dp)
                                    .semantics { contentDescription = semanticTabs },
                            indicator = { tabPositions ->
                                SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = Color(theme.primaryTextColor),
                                )
                            },
                        ) {
                            tabTitles.forEachIndexed { index, title ->
                                Tab(
                                    selected = index == selectedTabIndex,
                                    onClick = { selectedTabIndex = index },
                                    modifier = Modifier.height(48.dp),
                                ) {
                                    Text(
                                        text = title,
                                        style =
                                            type.body2.copy(
                                                color = Color(theme.primaryTextColor),
                                                lineHeight = 48.sp,
                                            ),
                                    )
                                }
                            }
                        }
                    }
                    when (tabTitles[selectedTabIndex]) {
                        stringResource(id = R.string.stytch_email_label) ->
                            EmailEntry(
                                emailState = emailState,
                                onEmailAddressChanged = onEmailAddressChanged,
                                onEmailAddressSubmit = { onEmailAddressSubmit(productConfig) },
                            )
                        stringResource(id = R.string.stytch_b2c_sms_tab_title) ->
                            PhoneEntry(
                                countryCode = phoneState.countryCode,
                                onCountryCodeChanged = onCountryCodeChanged,
                                phoneNumber = phoneState.phoneNumber,
                                onPhoneNumberChanged = onPhoneNumberChanged,
                                onPhoneNumberSubmit = { sendSmsOtp(productConfig.otpOptions) },
                                statusText = phoneState.error,
                            )
                        stringResource(id = R.string.stytch_b2c_whatsapp_tab_title) ->
                            PhoneEntry(
                                countryCode = phoneState.countryCode,
                                onCountryCodeChanged = onCountryCodeChanged,
                                phoneNumber = phoneState.phoneNumber,
                                onPhoneNumberChanged = onPhoneNumberChanged,
                                onPhoneNumberSubmit = { sendWhatsAppOTP(productConfig.otpOptions) },
                                statusText = phoneState.error,
                            )
                        else -> Text(stringResource(id = R.string.stytch_b2c_misconfigured_otp_warning))
                    }
                }
            }
        }
        uiState.genericErrorMessage?.display()
    }
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }
}

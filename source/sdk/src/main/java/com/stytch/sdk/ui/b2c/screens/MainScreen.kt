package com.stytch.sdk.ui.b2c.screens

import android.os.Parcelable
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.AuthenticationActivity
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.OAuthProvider
import com.stytch.sdk.ui.b2c.data.OTPMethods
import com.stytch.sdk.ui.b2c.data.OTPOptions
import com.stytch.sdk.ui.b2c.data.StytchProduct
import com.stytch.sdk.ui.b2c.data.StytchProductConfig
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.DividerWithText
import com.stytch.sdk.ui.shared.components.EmailEntry
import com.stytch.sdk.ui.shared.components.FormFieldStatus
import com.stytch.sdk.ui.shared.components.LoadingDialog
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.PhoneEntry
import com.stytch.sdk.ui.shared.components.SocialLoginButton
import com.stytch.sdk.ui.shared.theme.LocalStytchProductConfig
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal object MainScreen : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val productConfig = LocalStytchProductConfig.current
        val context = LocalActivity.current as AuthenticationActivity
        val viewModel = viewModel<MainScreenViewModel>(factory = MainScreenViewModel.factory(context.savedStateHandle))
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
) {
    val productConfig = LocalStytchProductConfig.current
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    val hasButtons = productConfig.products.contains(StytchProduct.OAUTH)
    val hasInput =
        productConfig.products.any {
            listOf(StytchProduct.OTP, StytchProduct.PASSWORDS, StytchProduct.EMAIL_MAGIC_LINKS).contains(it)
        }
    val hasEmail =
        productConfig.products.any {
            listOf(StytchProduct.EMAIL_MAGIC_LINKS, StytchProduct.PASSWORDS).contains(it)
        } ||
            productConfig.otpOptions.methods.contains(OTPMethods.EMAIL)
    val hasDivider = hasButtons && hasInput
    val tabTitles =
        mutableListOf<String>().apply {
            if (hasEmail) {
                add(stringResource(id = R.string.email))
            }
            if (productConfig.products.contains(StytchProduct.OTP)) {
                if (productConfig.otpOptions.methods.contains(OTPMethods.SMS)) {
                    add(stringResource(id = R.string.text))
                }
                if (productConfig.otpOptions.methods.contains(OTPMethods.WHATSAPP)) {
                    add(stringResource(id = R.string.whatsapp))
                }
            }
        }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val phoneState = uiState.phoneNumberState
    val emailState = uiState.emailState
    val semanticsOAuthButton = stringResource(id = R.string.semantics_oauth_button)

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        BackButton { exitWithoutAuthenticating() }
        if (!theme.hideHeaderText) {
            PageTitle(text = stringResource(id = R.string.sign_up_or_login))
        }
        if (productConfig.products.contains(StytchProduct.OAUTH)) {
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
        if (hasDivider) {
            DividerWithText(
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                text = stringResource(id = R.string.or),
            )
        }
        if (hasInput && tabTitles.isNotEmpty()) { // sanity check
            if (tabTitles.size > 1) {
                val semanticTabs = stringResource(id = R.string.semantics_tabs)
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color(theme.backgroundColor),
                    modifier = Modifier.padding(bottom = 12.dp).semantics { contentDescription = semanticTabs },
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
                stringResource(id = R.string.email) ->
                    EmailEntry(
                        emailState = emailState,
                        onEmailAddressChanged = onEmailAddressChanged,
                        onEmailAddressSubmit = { onEmailAddressSubmit(productConfig) },
                    )
                stringResource(id = R.string.text) ->
                    PhoneEntry(
                        countryCode = phoneState.countryCode,
                        onCountryCodeChanged = onCountryCodeChanged,
                        phoneNumber = phoneState.phoneNumber,
                        onPhoneNumberChanged = onPhoneNumberChanged,
                        onPhoneNumberSubmit = { sendSmsOtp(productConfig.otpOptions) },
                        statusText = phoneState.error,
                    )
                stringResource(id = R.string.whatsapp) ->
                    PhoneEntry(
                        countryCode = phoneState.countryCode,
                        onCountryCodeChanged = onCountryCodeChanged,
                        phoneNumber = phoneState.phoneNumber,
                        onPhoneNumberChanged = onPhoneNumberChanged,
                        onPhoneNumberSubmit = { sendWhatsAppOTP(productConfig.otpOptions) },
                        statusText = phoneState.error,
                    )
                else -> Text(stringResource(id = R.string.misconfigured_otp))
            }
        }
        uiState.genericErrorMessage?.let {
            FormFieldStatus(text = it, isError = true)
        }
    }
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }
}

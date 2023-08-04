package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.ui.AuthenticationActivity
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.components.BackButton
import com.stytch.sdk.ui.components.DividerWithText
import com.stytch.sdk.ui.components.EmailEntry
import com.stytch.sdk.ui.components.FormFieldStatus
import com.stytch.sdk.ui.components.LoadingDialog
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.components.PhoneEntry
import com.stytch.sdk.ui.components.SocialLoginButton
import com.stytch.sdk.ui.data.OAuthProvider
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.theme.LocalStytchProductConfig
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal object MainScreen : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<MainScreenViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current as AuthenticationActivity
        val uiState = viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.navigationFlow.collectLatest {
                navigator.push(it.getScreen())
            }
        }
        MainScreenComposable(
            uiState = uiState.value,
            onStartOAuthLogin = { provider, config -> viewModel.onStartOAuthLogin(context, provider, config) },
            onEmailAddressChanged = viewModel::onEmailAddressChanged,
            onEmailAddressSubmit = viewModel::onEmailAddressSubmit,
            onCountryCodeChanged = viewModel::onCountryCodeChanged,
            onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
            sendSmsOtp = viewModel::sendSmsOTP,
            sendWhatsAppOTP = viewModel::sendWhatsAppOTP,
            exitWithoutAuthenticating = context::exitWithoutAuthenticating
        )
    }
}

@Composable
private fun MainScreenComposable(
    uiState: MainScreenUiState,
    onStartOAuthLogin: (OAuthProvider, StytchProductConfig) -> Unit,
    onEmailAddressChanged: (String) -> Unit,
    onEmailAddressSubmit: (StytchProductConfig) -> Unit,
    onCountryCodeChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    sendSmsOtp: (OTPOptions) -> Unit,
    sendWhatsAppOTP: (OTPOptions) -> Unit,
    exitWithoutAuthenticating: () -> Unit
) {
    val productConfig = LocalStytchProductConfig.current
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    val hasButtons = productConfig.products.contains(StytchProduct.OAUTH)
    val hasInput = productConfig.products.any {
        listOf(StytchProduct.OTP, StytchProduct.PASSWORDS, StytchProduct.EMAIL_MAGIC_LINKS).contains(it)
    }
    val hasEmail = productConfig.products.any {
        listOf(StytchProduct.EMAIL_MAGIC_LINKS, StytchProduct.PASSWORDS).contains(it)
    } || productConfig.otpOptions.methods.contains(OTPMethods.EMAIL)
    val hasDivider = hasButtons && hasInput
    val tabTitles = mutableListOf<String>().apply {
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
    var selectedTabIndex by remember { mutableStateOf(0) }
    val phoneState = uiState.phoneNumberState
    val emailState = uiState.emailState

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        BackButton { exitWithoutAuthenticating() }
        if (!theme.hideHeaderText) {
            PageTitle(text = stringResource(id = R.string.sign_up_or_login))
        }
        if (productConfig.products.contains(StytchProduct.OAUTH)) {
            productConfig.oAuthOptions.providers.map {
                SocialLoginButton(
                    modifier = Modifier.padding(bottom = 12.dp),
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
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color(theme.backgroundColor),
                    modifier = Modifier.padding(bottom = 12.dp),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(theme.primaryTextColor)
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = { selectedTabIndex = index },
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = title,
                                style = type.body2.copy(
                                    color = Color(theme.primaryTextColor),
                                    lineHeight = 48.sp
                                )
                            )
                        }
                    }
                }
            }
            when (tabTitles[selectedTabIndex]) {
                stringResource(id = R.string.email) -> EmailEntry(
                    emailState = emailState,
                    onEmailAddressChanged = onEmailAddressChanged,
                    onEmailAddressSubmit = { onEmailAddressSubmit(productConfig) },
                )
                stringResource(id = R.string.text) -> PhoneEntry(
                    countryCode = phoneState.countryCode,
                    onCountryCodeChanged = onCountryCodeChanged,
                    phoneNumber = phoneState.phoneNumber,
                    onPhoneNumberChanged = onPhoneNumberChanged,
                    onPhoneNumberSubmit = { sendSmsOtp(productConfig.otpOptions) },
                    statusText = phoneState.error
                )
                stringResource(id = R.string.whatsapp) -> PhoneEntry(
                    countryCode = phoneState.countryCode,
                    onCountryCodeChanged = onCountryCodeChanged,
                    phoneNumber = phoneState.phoneNumber,
                    onPhoneNumberChanged = onPhoneNumberChanged,
                    onPhoneNumberSubmit = { sendWhatsAppOTP(productConfig.otpOptions) },
                    statusText = phoneState.error
                )
                else -> Text(stringResource(id = R.string.misconfigured_otp))
            }
        }
        uiState.genericErrorMessage?.let {
            FormFieldStatus(text = it, isError = true)
        }
    }
    if (uiState.showLoadingOverlay) {
        LoadingDialog()
    }
}

package com.stytch.sdk.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.components.DividerWithText
import com.stytch.sdk.ui.components.EmailEntry
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.components.PhoneEntry
import com.stytch.sdk.ui.components.SocialLoginButton
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography

internal data class MainScreen(
    val productConfig: StytchProductConfig
) : Screen {
    @Composable
    override fun Content() {
        val viewModel = viewModel<MainScreenViewModel>()
        MainScreenComposable(productConfig = productConfig, viewModel = viewModel)
    }
}

@Composable
private fun MainScreenComposable(
    productConfig: StytchProductConfig,
    viewModel: MainScreenViewModel,
) {
    if (
        productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS) &&
        productConfig.otpOptions?.methods?.contains(OTPMethods.EMAIL) == true
    ) {
        error(stringResource(id = R.string.eml_and_otp_error))
    }
    val navigator = LocalNavigator.currentOrThrow
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    val context = LocalContext.current as ComponentActivity
    val hasButtons = productConfig.products.contains(StytchProduct.OAUTH)
    val hasInput = productConfig.products.any {
        listOf(StytchProduct.OTP, StytchProduct.PASSWORDS, StytchProduct.EMAIL_MAGIC_LINKS).contains(it)
    }
    val hasEmail = productConfig.products.any {
        listOf(StytchProduct.EMAIL_MAGIC_LINKS, StytchProduct.PASSWORDS).contains(it)
    }
    val hasDivider = hasButtons && hasInput
    val tabTitles = mutableListOf<String>().apply {
        if (hasEmail) {
            add(stringResource(id = R.string.email))
        }
        if (productConfig.otpOptions?.methods?.contains(OTPMethods.SMS) == true) {
            add(stringResource(id = R.string.text))
        }
        if (productConfig.otpOptions?.methods?.contains(OTPMethods.WHATSAPP) == true) {
            add(stringResource(id = R.string.whatsapp))
        }
    }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val phoneState = viewModel.phoneState.collectAsState()
    val emailState = viewModel.emailState.collectAsState()

    Column {
        if (!theme.hideHeaderText) {
            PageTitle(text = stringResource(id = R.string.sign_up_or_login))
        }
        if (productConfig.products.contains(StytchProduct.OAUTH)) {
            productConfig.oAuthOptions?.providers?.map {
                SocialLoginButton(
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = { viewModel.onStartOAuthLogin(context, it, productConfig) },
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
                            style = type.tab.copy(
                                color = Color(theme.primaryTextColor),
                                lineHeight = 48.sp
                            )
                        )
                    }
                }
            }
            when (tabTitles[selectedTabIndex]) {
                stringResource(id = R.string.email) -> EmailEntry(
                    emailAddress = emailState.value.emailAddress,
                    onEmailAddressChanged = viewModel::onEmailAddressChanged,
                    onEmailAddressSubmit = {
                        if (productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS)) {
                            // send EML
                            viewModel.sendEmailMagicLink()
                        } else {
                            // send OTP
                            viewModel.sendEmailOTP()
                        }
                    },
                    emailAddressError = emailState.value.error,
                    statusText = emailState.value.error
                )
                stringResource(id = R.string.text) -> PhoneEntry(
                    countryCode = phoneState.value.countryCode,
                    onCountryCodeChanged = viewModel::onCountryCodeChanged,
                    phoneNumber = phoneState.value.phoneNumber,
                    onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
                    onPhoneNumberSubmit = viewModel::sendSmsOTP,
                    statusText = phoneState.value.error
                )
                stringResource(id = R.string.whatsapp) -> PhoneEntry(
                    countryCode = phoneState.value.countryCode,
                    onCountryCodeChanged = viewModel::onCountryCodeChanged,
                    phoneNumber = phoneState.value.phoneNumber,
                    onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
                    onPhoneNumberSubmit = viewModel::sendWhatsAppOTP,
                    statusText = phoneState.value.error
                )
                else -> Text(stringResource(id = R.string.misconfigured_otp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

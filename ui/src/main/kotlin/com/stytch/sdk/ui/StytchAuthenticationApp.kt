package com.stytch.sdk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.screens.MainScreen
import com.stytch.sdk.ui.theme.LocalStytchProductConfig
import com.stytch.sdk.ui.theme.LocalStytchTheme

@Composable
internal fun StytchAuthenticationApp(
    modifier: Modifier = Modifier,
    bootstrapData: BootstrapData,
) {
    val productConfig = LocalStytchProductConfig.current
    if (
        productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS) && // EML
        productConfig.otpOptions.methods.contains(OTPMethods.EMAIL) // Email OTP
    ) {
        error(stringResource(id = R.string.eml_and_otp_error))
    }
    if (
        !productConfig.products.contains(StytchProduct.PASSWORDS) && // no passwords
        !productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS) && // no EML
        !productConfig.otpOptions.methods.contains(OTPMethods.EMAIL) // no Email OTP
    ) {
        error(stringResource(id = R.string.misconfigured_products_and_options))
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(LocalStytchTheme.current.backgroundColor),
    ) {
        Column(
            modifier = Modifier
                .padding(start = 32.dp, top = 64.dp, end = 32.dp, bottom = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Navigator(MainScreen)
            if (!bootstrapData.disableSDKWatermark) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.powered_by_stytch),
                        contentDescription = stringResource(id = R.string.powered_by_stytch),
                    )
                }
            }
        }
    }
}

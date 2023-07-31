package com.stytch.sdk.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.components.DividerWithText
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.components.SocialLoginButton
import com.stytch.sdk.ui.data.OAuthProvider
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.theme.LocalStytchTheme

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
    val theme = LocalStytchTheme.current
    val context = LocalContext.current as ComponentActivity
    val navigator = LocalNavigator.currentOrThrow
    val hasDivider =
        productConfig.products.contains(StytchProduct.OAUTH) && productConfig.products.any {
            listOf(StytchProduct.OTP, StytchProduct.PASSWORDS, StytchProduct.EMAIL_MAGIC_LINKS).contains(it)
        }
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
    }
}

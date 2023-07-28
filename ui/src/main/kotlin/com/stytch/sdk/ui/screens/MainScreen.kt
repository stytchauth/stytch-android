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
import androidx.hilt.navigation.compose.hiltViewModel
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.components.DividerWithText
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.components.SocialLoginButton
import com.stytch.sdk.ui.data.OAuthOptions
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.theme.LocalStytchTheme

@Composable
internal fun MainScreen(
    productConfig: StytchProductConfig,
    viewModel: MainScreenViewModel = hiltViewModel(),
) {
    val theme = LocalStytchTheme.current
    val context = LocalContext.current as ComponentActivity
    Column {
        if (!theme.hideHeaderText) {
            PageTitle(text = stringResource(id = R.string.sign_up_or_login))
        }
        SocialLoginButton(
            modifier = Modifier.padding(bottom = 12.dp),
            onClick = {
                viewModel.onStartGoogle(
                    context,
                    null,//productConfig.googleOauthOptions?.clientId,
                    productConfig.oAuthOptions
                )
            },
            iconDrawable = painterResource(id = R.drawable.google),
            iconDescription = stringResource(id = R.string.google_logo),
            text = stringResource(id = R.string.continue_with_google),
        )
        SocialLoginButton(
            modifier = Modifier.padding(bottom = 24.dp),
            onClick = { viewModel.onStartApple(context, productConfig.oAuthOptions) },
            iconDrawable = painterResource(id = R.drawable.apple),
            iconDescription = stringResource(id = R.string.apple_logo),
            text = stringResource(id = R.string.continue_with_apple),
        )
        DividerWithText(
            modifier = Modifier.padding(bottom = 24.dp),
            text = stringResource(id = R.string.or),
        )
    }
}

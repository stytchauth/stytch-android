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
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.screens.MainScreen
import com.stytch.sdk.ui.theme.LocalStytchTheme

@Composable
internal fun StytchAuthenticationApp(
    modifier: Modifier = Modifier,
    productConfig: StytchProductConfig,
    disableWatermark: Boolean,
    exit: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(LocalStytchTheme.current.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 32.dp, top = 64.dp, end = 32.dp, bottom = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Navigator(
                screen = MainScreen(
                    productConfig = productConfig,
                    exit = exit,
                )
            )
            if (!disableWatermark) {
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

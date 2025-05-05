package com.stytch.sdk.ui.b2c

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.stytch.sdk.R
import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.ui.b2c.data.StytchProductConfig
import com.stytch.sdk.ui.b2c.screens.MainScreen
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun StytchAuthenticationApp(
    modifier: Modifier = Modifier,
    bootstrapData: BootstrapData,
    productConfig: StytchProductConfig,
    screen: AndroidScreen? = null,
    onInvalidConfig: (StytchUIInvalidConfiguration) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(LocalStytchTheme.current.backgroundColor),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(start = 0.dp, top = 64.dp, end = 0.dp, bottom = 0.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            Row(modifier = Modifier.fillMaxSize().weight(1f).padding(32.dp, 0.dp)) {
                Navigator(listOfNotNull(MainScreen, screen)) { navigator ->
                    screen?.let {
                        navigator.push(it)
                    }
                    CurrentScreen()
                }
            }
            if (!bootstrapData.disableSDKWatermark) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(color = Color(255, 255, 255, 128))
                            .padding(0.dp, 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        modifier = Modifier.height(19.dp),
                        painter = painterResource(id = R.drawable.powered_by_stytch),
                        contentDescription = stringResource(id = R.string.stytch_b2c_powered_by_stytch),
                    )
                }
            }
        }
    }
}

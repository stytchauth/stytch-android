package com.stytch.stytchexampleapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.b2c.StytchUI
import com.stytch.sdk.ui.b2c.data.BiometricsOptions
import com.stytch.sdk.ui.b2c.data.OTPMethods
import com.stytch.sdk.ui.b2c.data.OTPOptions
import com.stytch.sdk.ui.b2c.data.StytchProduct
import com.stytch.sdk.ui.b2c.data.StytchProductConfig
import com.stytch.stytchexampleapp.ui.screens.loading.LoadingScreen
import com.stytch.stytchexampleapp.ui.screens.login.LoginRoute
import com.stytch.stytchexampleapp.ui.screens.profile.ProfileRoute
import com.stytch.stytchexampleapp.ui.theme.StytchAndroidSDKTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state = viewModel.authenticationState.collectAsState()
            val navController = rememberNavController()

            fun navigateTo(route: Route) {
                navController.navigate(route.route)
            }
            val stytchUi =
                StytchUI
                    .Builder()
                    .apply {
                        activity(this@MainActivity)
                        productConfig(
                            StytchProductConfig(
                                products = listOf(StytchProduct.BIOMETRICS, StytchProduct.OTP),
                                otpOptions = OTPOptions(methods = listOf(OTPMethods.SMS)),
                                biometricsOptions = BiometricsOptions(showBiometricRegistrationOnLogin = true),
                            ),
                        )
                        onAuthenticated {
                            when (it) {
                                is StytchResult.Success -> navigateTo(Route.Profile)
                                is StytchResult.Error -> navigateTo(Route.Login)
                            }
                        }
                    }.build()
            StytchAndroidSDKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .paint(
                                    painter = painterResource(id = R.drawable.hellosocks_background),
                                    contentScale = ContentScale.Crop,
                                ),
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(
                                        top = innerPadding.calculateTopPadding(),
                                        bottom = innerPadding.calculateBottomPadding(),
                                        start = 32.dp,
                                        end = 32.dp,
                                    ).verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Column(
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .background(Color.White)
                                        .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(text = "Hello Socks", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(32.dp))
                                NavHost(navController = navController, startDestination = Route.Loading.route) {
                                    composable(Route.Loading.route) {
                                        LoadingScreen(
                                            authenticationState = state.value,
                                            navigateTo = ::navigateTo,
                                        )
                                    }
                                    composable(Route.Login.route) {
                                        LoginRoute(
                                            authenticationState = state.value,
                                            navigateTo = ::navigateTo,
                                        )
                                    }
                                    composable(Route.Profile.route) {
                                        ProfileRoute(
                                            authenticationState = state.value,
                                            navigateTo = ::navigateTo,
                                        )
                                    }
                                    composable(Route.UILogin.route) {
                                        LaunchedEffect(Unit) {
                                            stytchUi.authenticate()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

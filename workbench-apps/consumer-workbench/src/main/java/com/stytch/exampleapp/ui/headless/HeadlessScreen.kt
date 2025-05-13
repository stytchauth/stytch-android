package com.stytch.exampleapp.ui.headless

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stytch.exampleapp.ui.headless.biometrics.BiometricsScreen
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HeadlessScreen() {
    val viewModel = HeadlessScreenViewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    var selectedProductIndex by remember { mutableIntStateOf(-1) }
    val responseState = viewModel.responseState.collectAsState()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        "Products",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    AllHeadlessProducts.mapIndexed { index, item ->
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text(text = item.title) },
                            selected = index == selectedProductIndex,
                            onClick = {
                                selectedProductIndex = index
                                navController.navigate(item)
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                        )
                    }
                }
            }
        },
    ) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Show menu") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                )
            },
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    style = MaterialTheme.typography.headlineLarge,
                    text = "Stytch Headless",
                )
                NavHost(
                    modifier = Modifier.weight(0.5f),
                    navController = navController,
                    startDestination = HeadlessProducts.None,
                ) {
                    composable<HeadlessProducts.None> {
                        Text("Select a product from the side drawer to test it out")
                    }
                    composable<HeadlessProducts.Biometrics> {
                        BiometricsScreen(viewModel::setResponseState)
                    }
                    composable<HeadlessProducts.MagicLinks> {
                    }
                    composable<HeadlessProducts.OAuth> {
                    }
                    composable<HeadlessProducts.OTP> {
                    }
                    composable<HeadlessProducts.Passkeys> {
                    }
                    composable<HeadlessProducts.Passwords> {
                    }
                    composable<HeadlessProducts.Sessions> {
                    }
                    composable<HeadlessProducts.TOTP> {
                    }
                }
                Column(
                    modifier = Modifier.weight(0.5f).verticalScroll(rememberScrollState()),
                ) {
                    when (val response = responseState.value) {
                        is HeadlessMethodResponseState.None -> {}
                        is HeadlessMethodResponseState.Loading -> CircularProgressIndicator()
                        is HeadlessMethodResponseState.Response ->
                            SelectionContainer {
                                Text(response.result.toFriendlyDisplay())
                            }
                    }
                }
            }
        }
    }
}

@Serializable sealed class HeadlessProducts(
    val title: String,
) {
    @Serializable data object None : HeadlessProducts("Home")

    @Serializable data object Biometrics : HeadlessProducts("Biometrics")

    @Serializable data object MagicLinks : HeadlessProducts("Magic Links")

    @Serializable data object OAuth : HeadlessProducts("OAuth")

    @Serializable data object OTP : HeadlessProducts("OTP")

    @Serializable data object Passkeys : HeadlessProducts("Passkeys")

    @Serializable data object Passwords : HeadlessProducts("Passwords")

    @Serializable data object Sessions : HeadlessProducts("Sessions")

    @Serializable data object TOTP : HeadlessProducts("TOTP")
}

val AllHeadlessProducts =
    listOf(
        HeadlessProducts.Biometrics,
        HeadlessProducts.MagicLinks,
        HeadlessProducts.OAuth,
        HeadlessProducts.OTP,
        HeadlessProducts.Passkeys,
        HeadlessProducts.Passwords,
        HeadlessProducts.Sessions,
        HeadlessProducts.TOTP,
    )

fun <T : Any> StytchResult<T>.toFriendlyDisplay() =
    when (this) {
        is StytchResult.Success<*> -> this.toString()
        is StytchResult.Error -> {
            var message = "Name: ${exception}\nDescription: ${exception.message}"
            if (exception is StytchAPIError) {
                message += "\nURL: ${(exception as StytchAPIError).url}"
            }
            message
        }
    }

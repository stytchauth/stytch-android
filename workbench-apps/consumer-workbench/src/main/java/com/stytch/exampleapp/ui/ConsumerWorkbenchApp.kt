package com.stytch.exampleapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stytch.exampleapp.ui.headless.HeadlessScreen
import com.stytch.exampleapp.ui.home.HomeScreen
import com.stytch.exampleapp.ui.ui.UIScreen
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.collections.mapIndexed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerWorkbenchApp(state: ConsumerWorkbenchAppUIState) {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomBar(
                destinations = listOf(RootRoute.Home, RootRoute.Headless, RootRoute.UI),
                onNavigateTo = { navController.navigate(it) },
            )
        },
    ) { innerPadding ->
        Surface(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = RootRoute.Home,
            ) {
                composable<RootRoute.Home> { HomeScreen(state) }
                composable<RootRoute.Headless> { HeadlessScreen() }
                composable<RootRoute.UI> { UIScreen(state) }
            }
        }
    }
}

@Composable
fun BottomBar(
    destinations: List<RootRoute>,
    onNavigateTo: (RootRoute) -> Unit,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    NavigationBar {
        destinations.mapIndexed { index, destination ->
            NavigationBarItem(
                selected = index == selectedTabIndex,
                onClick = {
                    selectedTabIndex = index
                    onNavigateTo(destination)
                },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                label = { Text(destination.title) },
            )
        }
    }
}

@Serializable sealed class RootRoute(
    val title: String,
    @Contextual val icon: ImageVector,
) {
    @Serializable
    data object Home : RootRoute("Home", Icons.Outlined.Home)

    @Serializable
    data object Headless : RootRoute("Headless", Icons.Outlined.Build)

    @Serializable
    data object UI : RootRoute("UI", Icons.Outlined.Star)
}

package com.stytch.exampleapp.b2b.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stytch.exampleapp.b2b.DiscoveryViewModel
import com.stytch.exampleapp.b2b.HomeViewModel
import com.stytch.exampleapp.b2b.MemberViewModel
import com.stytch.exampleapp.b2b.PasswordsViewModel
import com.stytch.exampleapp.b2b.R
import com.stytch.exampleapp.b2b.SSOViewModel

val items = listOf(
    Screen.Main,
    Screen.Passwords,
    Screen.Discovery,
    Screen.SSO,
    Screen.Member
)

@Composable
fun AppScreen(
    homeViewModel: HomeViewModel,
    passwordsViewModel: PasswordsViewModel,
    discoveryViewModel: DiscoveryViewModel,
    ssoViewModel: SSOViewModel,
    memberViewModel: MemberViewModel,
) {
    val navController = rememberNavController()
    val intermediateSessionTokenValue = homeViewModel.intermediateSessionToken.collectAsState()
    Scaffold(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        contentColor = MaterialTheme.colors.onBackground,
        topBar = { Toolbar(toolbarText = stringResource(id = R.string.app_name)) },
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.iconVector, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        content = { padding ->
            NavHost(navController, startDestination = Screen.Main.route, Modifier.padding(padding)) {
                composable(Screen.Main.route) { MainScreen(viewModel = homeViewModel) }
                composable(Screen.Passwords.route) { PasswordsScreen(viewModel = passwordsViewModel) }
                composable(Screen.Discovery.route) {
                    DiscoveryScreen(
                        viewModel = discoveryViewModel,
                        intermediateSessionToken = intermediateSessionTokenValue.value
                    )
                }
                composable(Screen.SSO.route) { SSOScreen(viewModel = ssoViewModel) }
                composable(Screen.Member.route) { MemberScreen(viewModel = memberViewModel) }
            }
        }
    )
}

@Composable
fun Toolbar(toolbarText: String) {
    TopAppBar(
        title = {
            Text(
                text = toolbarText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 2.dp
    )
}

sealed class Screen(val route: String, @StringRes val resourceId: Int, val iconVector: ImageVector) {
    object Main : Screen("main", R.string.home, Icons.Filled.Home)
    object Passwords : Screen("passwords", R.string.passwords, Icons.Filled.AccountBox)
    object Discovery : Screen("discovery", R.string.discovery, Icons.Filled.Build)
    object SSO : Screen("sso", R.string.sso, Icons.Filled.Add)
    object Member : Screen("member", R.string.member, Icons.Filled.AccountCircle)
}

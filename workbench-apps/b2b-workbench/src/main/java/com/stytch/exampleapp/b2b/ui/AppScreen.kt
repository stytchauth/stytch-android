package com.stytch.exampleapp.b2b.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.stytch.exampleapp.b2b.OAuthViewModel
import com.stytch.exampleapp.b2b.OTPViewModel
import com.stytch.exampleapp.b2b.OrganizationViewModel
import com.stytch.exampleapp.b2b.PasswordsViewModel
import com.stytch.exampleapp.b2b.R
import com.stytch.exampleapp.b2b.RecoveryCodesViewModel
import com.stytch.exampleapp.b2b.SCIMViewModel
import com.stytch.exampleapp.b2b.SSOViewModel
import com.stytch.exampleapp.b2b.TOTPViewModel

val items =
    listOf(
        Screen.Main,
        Screen.Passwords,
        Screen.Discovery,
        Screen.SSO,
        Screen.Member,
        Screen.Organization,
        Screen.OTP,
        Screen.TOTP,
        Screen.RecoveryCodes,
        Screen.OAuth,
        Screen.SCIM,
    )

@Composable
fun AppScreen(
    homeViewModel: HomeViewModel,
    passwordsViewModel: PasswordsViewModel,
    discoveryViewModel: DiscoveryViewModel,
    ssoViewModel: SSOViewModel,
    memberViewModel: MemberViewModel,
    organizationViewModel: OrganizationViewModel,
    otpViewModel: OTPViewModel,
    totpViewModel: TOTPViewModel,
    recoveryCodesViewModel: RecoveryCodesViewModel,
    oAuthViewModel: OAuthViewModel,
    scimViewModel: SCIMViewModel,
) {
    val navController = rememberNavController()
    Scaffold(
        modifier =
            Modifier
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
                        icon = { },
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
                        },
                    )
                }
            }
        },
        content = { padding ->
            NavHost(navController, startDestination = Screen.Main.route, Modifier.padding(padding)) {
                composable(Screen.Main.route) { MainScreen(viewModel = homeViewModel) }
                composable(Screen.Passwords.route) { PasswordsScreen(viewModel = passwordsViewModel) }
                composable(Screen.Discovery.route) { DiscoveryScreen(viewModel = discoveryViewModel) }
                composable(Screen.SSO.route) { SSOScreen(viewModel = ssoViewModel) }
                composable(Screen.Member.route) { MemberScreen(viewModel = memberViewModel) }
                composable(Screen.Organization.route) { OrganizationScreen(viewModel = organizationViewModel) }
                composable(Screen.OTP.route) { OTPScreen(viewModel = otpViewModel) }
                composable(Screen.TOTP.route) { TOTPScreen(viewModel = totpViewModel) }
                composable(Screen.RecoveryCodes.route) { RecoveryCodesScreen(viewModel = recoveryCodesViewModel) }
                composable(Screen.OAuth.route) { OAuthScreen(viewModel = oAuthViewModel) }
                composable(Screen.SCIM.route) { ScimScreen(viewModel = scimViewModel) }
            }
        },
    )
}

@Composable
fun Toolbar(toolbarText: String) {
    TopAppBar(
        title = {
            Text(
                text = toolbarText,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 2.dp,
    )
}

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
) {
    object Main : Screen("main", R.string.home)

    object Passwords : Screen("passwords", R.string.passwords)

    object Discovery : Screen("discovery", R.string.discovery)

    object SSO : Screen("sso", R.string.sso)

    object Member : Screen("member", R.string.member)

    object Organization : Screen("organization", R.string.organization)

    object OTP : Screen("otp", R.string.otp)

    object TOTP : Screen("totp", R.string.totp)

    object RecoveryCodes : Screen("recovery-codes", R.string.recovery_codes)

    object OAuth : Screen("oauth", R.string.oauth)

    object SCIM : Screen("scim", R.string.scim_scim)
}

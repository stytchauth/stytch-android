package com.stytch.sdk.ui.b2b

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.screens.DeepLinkParserScreen
import com.stytch.sdk.ui.b2b.screens.DiscoveryScreen
import com.stytch.sdk.ui.b2b.screens.EmailConfirmationScreen
import com.stytch.sdk.ui.b2b.screens.ErrorScreen
import com.stytch.sdk.ui.b2b.screens.MainScreen
import com.stytch.sdk.ui.b2b.screens.PasswordAuthenticateScreen
import com.stytch.sdk.ui.b2b.screens.PasswordForgotScreen
import com.stytch.sdk.ui.b2b.screens.PasswordResetScreen
import com.stytch.sdk.ui.b2b.screens.PasswordSetNewScreen
import com.stytch.sdk.ui.shared.components.LoadingDialog
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.theme.LocalStytchBootstrapData
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun StytchB2BAuthenticationApp(
    state: State<B2BUIState>,
    dispatch: Dispatch,
    createViewModel: CreateViewModel<ViewModel>,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val currentBackStackEntryState = navController.currentBackStackEntryAsState()
    val bootstrapData = LocalStytchBootstrapData.current

    fun <T : ViewModel> createViewModelHelper(modelClass: Class<T>): T =
        createViewModel(modelClass as Class<ViewModel>) as T

    val isSearchingForOrganizationBySlug = state.value.isSearchingForOrganizationBySlug
    val activeOrganization = state.value.activeOrganization
    val currentRoute = state.value.currentRoute
    val authFlowType = state.value.authFlowType
    val deeplinkTokenPair = state.value.deeplinkTokenPair
    val startDestination = if (deeplinkTokenPair != null) Routes.DeeplinkParser else Routes.Main

    LaunchedEffect(currentRoute) {
        currentRoute?.let {
            navController.navigate(it)
        }
    }

    LaunchedEffect(currentBackStackEntryState.value) {
        dispatch(SetNextRoute(null))
    }

    LaunchedEffect(
        isSearchingForOrganizationBySlug,
        activeOrganization,
        currentRoute,
        authFlowType,
    ) {
        if (
            !isSearchingForOrganizationBySlug &&
            activeOrganization == null &&
            currentRoute == Routes.Main &&
            authFlowType == AuthFlowType.ORGANIZATION
        ) {
            // Trying to launch an org flow without an org
            dispatch(SetB2BError(B2BErrorType.Organization))
        }
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(LocalStytchTheme.current.backgroundColor),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(start = 32.dp, top = 64.dp, end = 32.dp, bottom = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            NavHost(navController = navController, startDestination = startDestination) {
                composable<Routes.DeeplinkParser> {
                    DeepLinkParserScreen(state = state, createViewModel = ::createViewModelHelper)
                }
                composable<Routes.Discovery> {
                    DiscoveryScreen(state = state, createViewModel = ::createViewModelHelper)
                }
                composable<Routes.EmailConfirmation> {
                    EmailConfirmationScreen(
                        state = state,
                        route = Routes.EmailConfirmation,
                        createViewModel = ::createViewModelHelper,
                    )
                }
                composable<Routes.Error> {
                    ErrorScreen(state = state)
                }
                composable<Routes.Main> {
                    MainScreen(state = state, createViewModel = ::createViewModelHelper)
                }
                composable<Routes.MFAEnrollmentSelection> {
                    PageTitle(text = "MFAEnrollmentSelection")
                }
                composable<Routes.PasswordAuthenticate> {
                    PasswordAuthenticateScreen(state = state, createViewModel = ::createViewModelHelper)
                }
                composable<Routes.PasswordForgot> {
                    PasswordForgotScreen(state = state, createViewModel = ::createViewModelHelper)
                }
                composable<Routes.PasswordReset> {
                    PasswordResetScreen(state = state, createViewModel = ::createViewModelHelper)
                }
                composable<Routes.PasswordResetVerifyConfirmation> {
                    EmailConfirmationScreen(
                        state = state,
                        route = Routes.PasswordResetVerifyConfirmation,
                        createViewModel = ::createViewModelHelper,
                    )
                }
                composable<Routes.PasswordSetNew> {
                    PasswordSetNewScreen(state = state, createViewModel = ::createViewModelHelper)
                }
                composable<Routes.PasswordSetNewConfirmation> {
                    EmailConfirmationScreen(
                        state = state,
                        route = Routes.PasswordSetNewConfirmation,
                        createViewModel = ::createViewModelHelper,
                    )
                }
                composable<Routes.RecoveryCodeEntry> {
                    PageTitle(text = "RecoveryCodeEntry")
                }
                composable<Routes.RecoveryCodeSave> {
                    PageTitle(text = "RecoveryCodeSave")
                }
                composable<Routes.SMSOTPEnrollment> {
                    PageTitle(text = "SMSOTPEnrollment")
                }
                composable<Routes.SMSOTPEntry> {
                    PageTitle(text = "SMSOTPEntry")
                }
                composable<Routes.Success> {
                    PageTitle(text = "Success")
                }
                composable<Routes.TOTPEnrollment> {
                    PageTitle(text = "TOTPEnrollment")
                }
                composable<Routes.TOTPEntry> {
                    PageTitle(text = "TOTPEntry")
                }
            }
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
        if (state.value.isLoading) {
            LoadingDialog()
        }
    }
}

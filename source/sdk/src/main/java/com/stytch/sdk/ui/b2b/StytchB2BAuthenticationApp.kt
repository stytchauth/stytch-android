package com.stytch.sdk.ui.b2b

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetGenericError
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.screens.DeepLinkParserScreen
import com.stytch.sdk.ui.b2b.screens.DiscoveryScreen
import com.stytch.sdk.ui.b2b.screens.EmailConfirmationScreen
import com.stytch.sdk.ui.b2b.screens.EmailMethodSelectionScreen
import com.stytch.sdk.ui.b2b.screens.EmailOTPEntryScreen
import com.stytch.sdk.ui.b2b.screens.ErrorScreen
import com.stytch.sdk.ui.b2b.screens.LoadingView
import com.stytch.sdk.ui.b2b.screens.MFAEnrollmentSelectionScreen
import com.stytch.sdk.ui.b2b.screens.MainScreen
import com.stytch.sdk.ui.b2b.screens.PasswordAuthenticateScreen
import com.stytch.sdk.ui.b2b.screens.PasswordForgotScreen
import com.stytch.sdk.ui.b2b.screens.PasswordResetScreen
import com.stytch.sdk.ui.b2b.screens.PasswordSetNewScreen
import com.stytch.sdk.ui.b2b.screens.RecoveryCodesEntryScreen
import com.stytch.sdk.ui.b2b.screens.RecoveryCodesSaveScreen
import com.stytch.sdk.ui.b2b.screens.SMSOTPEnrollmentScreen
import com.stytch.sdk.ui.b2b.screens.SMSOTPEntryScreen
import com.stytch.sdk.ui.b2b.screens.SuccessScreen
import com.stytch.sdk.ui.b2b.screens.TOTPEnrollmentScreen
import com.stytch.sdk.ui.b2b.screens.TOTPEntryScreen
import com.stytch.sdk.ui.shared.components.FormFieldStatus
import com.stytch.sdk.ui.shared.components.LoadingDialog
import com.stytch.sdk.ui.shared.theme.LocalStytchBootstrapData
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun StytchB2BAuthenticationApp(
    modifier: Modifier = Modifier,
    state: State<B2BUIState>,
    dispatch: Dispatch,
    createViewModel: CreateViewModel<ViewModel>,
) {
    val navController = rememberNavController()
    val bootstrapData = LocalStytchBootstrapData.current
    val theme = LocalStytchTheme.current

    fun <T : ViewModel> createViewModelHelper(modelClass: Class<T>): T =
        createViewModel(modelClass as Class<ViewModel>) as T

    val isSearchingForOrganizationBySlug = state.value.isSearchingForOrganizationBySlug
    val activeOrganization = state.value.activeOrganization
    val currentRoute = state.value.currentRoute
    val authFlowType = state.value.authFlowType
    val deeplinkTokenPair = state.value.deeplinkTokenPair
    val startDestination =
        if (deeplinkTokenPair != null) {
            Routes.DeeplinkParser
        } else {
            currentRoute ?: Routes.Loading
        }

    LaunchedEffect(currentRoute) {
        currentRoute?.let {
            navController.navigate(it) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = false
                    inclusive = true
                }
            }
        }
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
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        color = Color(LocalStytchTheme.current.backgroundColor),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(start = 0.dp, top = 64.dp, end = 0.dp, bottom = 0.dp)
                    .fillMaxSize(),
        ) {
            Row(modifier = Modifier.fillMaxSize().weight(1f).padding(32.dp, 0.dp)) {
                NavHost(navController = navController, startDestination = startDestination) {
                    composable<Routes.Loading> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            LoadingView(color = Color(theme.primaryTextColor))
                        }
                    }
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
                        ErrorScreen(state = state, createViewModel = ::createViewModelHelper)
                    }
                    composable<Routes.Main> {
                        MainScreen(state = state, createViewModel = ::createViewModelHelper)
                    }
                    composable<Routes.MFAEnrollmentSelection> {
                        MFAEnrollmentSelectionScreen(createViewModel = ::createViewModelHelper)
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
                        RecoveryCodesEntryScreen(createViewModel = ::createViewModelHelper)
                    }
                    composable<Routes.RecoveryCodeSave> {
                        RecoveryCodesSaveScreen(state = state, createViewModel = ::createViewModelHelper)
                    }
                    composable<Routes.SMSOTPEnrollment> {
                        SMSOTPEnrollmentScreen(state = state, createViewModel = ::createViewModelHelper)
                    }
                    composable<Routes.SMSOTPEntry> {
                        SMSOTPEntryScreen(state = state, createViewModel = ::createViewModelHelper)
                    }
                    composable<Routes.Success> {
                        SuccessScreen()
                    }
                    composable<Routes.TOTPEnrollment> {
                        TOTPEnrollmentScreen(createViewModel = ::createViewModelHelper)
                    }
                    composable<Routes.TOTPEntry> {
                        TOTPEntryScreen(createViewModel = ::createViewModelHelper)
                    }
                    composable<Routes.EmailMethodSelection> {
                        EmailMethodSelectionScreen(createViewModel = ::createViewModelHelper)
                    }
                    composable<Routes.EmailOTPEntry> {
                        EmailOTPEntryScreen(createViewModel = ::createViewModelHelper)
                    }
                }
                state.value.errorToastText?.let {
                    FormFieldStatus(text = it, isError = true, autoDismiss = {
                        dispatch(SetGenericError(null))
                    })
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

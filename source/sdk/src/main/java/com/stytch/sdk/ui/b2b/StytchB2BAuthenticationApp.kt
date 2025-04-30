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
import com.stytch.sdk.common.DeeplinkTokenPair
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.components.LoadingView
import com.stytch.sdk.ui.b2b.data.SetGenericError
import com.stytch.sdk.ui.b2b.navigation.Route
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.screens.deepLinkParser.DeepLinkParserScreen
import com.stytch.sdk.ui.b2b.screens.deepLinkParser.DeepLinkParserScreenViewModel
import com.stytch.sdk.ui.b2b.screens.discovery.DiscoveryScreen
import com.stytch.sdk.ui.b2b.screens.discovery.DiscoveryScreenViewModel
import com.stytch.sdk.ui.b2b.screens.emailConfirmation.EmailConfirmationScreen
import com.stytch.sdk.ui.b2b.screens.emailConfirmation.EmailConfirmationScreenRoute
import com.stytch.sdk.ui.b2b.screens.emailConfirmation.EmailConfirmationScreenViewModel
import com.stytch.sdk.ui.b2b.screens.emailMethodSelection.EmailMethodSelectionScreen
import com.stytch.sdk.ui.b2b.screens.emailMethodSelection.EmailMethodSelectionScreenViewModel
import com.stytch.sdk.ui.b2b.screens.emailOTPEntry.EmailOTPEntryScreen
import com.stytch.sdk.ui.b2b.screens.emailOTPEntry.EmailOTPEntryScreenViewModel
import com.stytch.sdk.ui.b2b.screens.error.ErrorScreen
import com.stytch.sdk.ui.b2b.screens.error.ErrorScreenViewModel
import com.stytch.sdk.ui.b2b.screens.main.MainScreen
import com.stytch.sdk.ui.b2b.screens.main.MainScreenViewModel
import com.stytch.sdk.ui.b2b.screens.mfaEnrollmentSelection.MFAEnrollmentSelectionScreen
import com.stytch.sdk.ui.b2b.screens.mfaEnrollmentSelection.MFAEnrollmentSelectionScreenViewModel
import com.stytch.sdk.ui.b2b.screens.passwordAuthenticate.PasswordAuthenticateScreen
import com.stytch.sdk.ui.b2b.screens.passwordAuthenticate.PasswordAuthenticateScreenViewModel
import com.stytch.sdk.ui.b2b.screens.passwordForgot.PasswordForgotScreen
import com.stytch.sdk.ui.b2b.screens.passwordForgot.PasswordForgotScreenViewModel
import com.stytch.sdk.ui.b2b.screens.passwordReset.PasswordResetScreen
import com.stytch.sdk.ui.b2b.screens.passwordReset.PasswordResetScreenViewModel
import com.stytch.sdk.ui.b2b.screens.passwordSetNew.PasswordSetNewScreen
import com.stytch.sdk.ui.b2b.screens.passwordSetNew.PasswordSetNewScreenViewModel
import com.stytch.sdk.ui.b2b.screens.recoveryCodesEntry.RecoveryCodesEntryScreen
import com.stytch.sdk.ui.b2b.screens.recoveryCodesEntry.RecoveryCodesEntryScreenViewModel
import com.stytch.sdk.ui.b2b.screens.recoveryCodesSave.RecoveryCodesSaveScreen
import com.stytch.sdk.ui.b2b.screens.recoveryCodesSave.RecoveryCodesSaveScreenViewModel
import com.stytch.sdk.ui.b2b.screens.smsOTPEnrollment.SMSOTPEnrollmentScreen
import com.stytch.sdk.ui.b2b.screens.smsOTPEnrollment.SMSOTPEnrollmentScreenViewModel
import com.stytch.sdk.ui.b2b.screens.smsOTPEntry.SMSOTPEntryScreen
import com.stytch.sdk.ui.b2b.screens.smsOTPEntry.SMSOTPEntryScreenViewModel
import com.stytch.sdk.ui.b2b.screens.ssoDiscoveryEmail.SSODiscoveryEmailScreen
import com.stytch.sdk.ui.b2b.screens.ssoDiscoveryEmail.SSODiscoveryEmailScreenViewModel
import com.stytch.sdk.ui.b2b.screens.ssoDiscoveryFallback.SSODiscoveryFallbackScreen
import com.stytch.sdk.ui.b2b.screens.ssoDiscoveryFallback.SSODiscoveryFallbackScreenViewModel
import com.stytch.sdk.ui.b2b.screens.ssoDiscoveryMenu.SSODiscoveryMenuScreen
import com.stytch.sdk.ui.b2b.screens.ssoDiscoveryMenu.SSODiscoveryMenuScreenViewModel
import com.stytch.sdk.ui.b2b.screens.success.SuccessScreen
import com.stytch.sdk.ui.b2b.screens.totpEnrollment.TOTPEnrollmentScreen
import com.stytch.sdk.ui.b2b.screens.totpEnrollment.TOTPEnrollmentScreenViewModel
import com.stytch.sdk.ui.b2b.screens.totpEntry.TOTPEntryScreen
import com.stytch.sdk.ui.b2b.screens.totpEntry.TOTPEntryScreenViewModel
import com.stytch.sdk.ui.shared.components.FormFieldStatus
import com.stytch.sdk.ui.shared.components.LoadingDialog
import com.stytch.sdk.ui.shared.theme.LocalStytchBootstrapData
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun StytchB2BAuthenticationApp(
    modifier: Modifier = Modifier,
    dispatch: Dispatch,
    rootAppState: RootAppState = RootAppState(),
    createViewModel: CreateViewModel<ViewModel>,
) {
    val navController = rememberNavController()
    val bootstrapData = LocalStytchBootstrapData.current
    val theme = LocalStytchTheme.current

    fun <T : ViewModel> createViewModelHelper(modelClass: Class<T>): T =
        createViewModel(modelClass as Class<ViewModel>) as T

    val startDestination =
        if (rootAppState.deeplinkTokenPair != null) {
            Routes.DeeplinkParser
        } else {
            rootAppState.currentRoute ?: Routes.Loading
        }

    val deepLinkViewModel = createViewModelHelper(DeepLinkParserScreenViewModel::class.java)
    val discoveryViewModel = createViewModelHelper(DiscoveryScreenViewModel::class.java)
    val emailConfirmationScreenViewModel = createViewModelHelper(EmailConfirmationScreenViewModel::class.java)
    val emailMethodSelectionViewModel = createViewModelHelper(EmailMethodSelectionScreenViewModel::class.java)
    val emailOTPEntryViewModel = createViewModelHelper(EmailOTPEntryScreenViewModel::class.java)
    val errorViewModel = createViewModelHelper(ErrorScreenViewModel::class.java)
    val mainViewModel = createViewModelHelper(MainScreenViewModel::class.java)
    val mfaEnrollmentViewModel = createViewModelHelper(MFAEnrollmentSelectionScreenViewModel::class.java)
    val passwordAuthenticateViewModel = createViewModelHelper(PasswordAuthenticateScreenViewModel::class.java)
    val passwordForgotViewModel = createViewModelHelper(PasswordForgotScreenViewModel::class.java)
    val passwordResetViewModel = createViewModelHelper(PasswordResetScreenViewModel::class.java)
    val passwordSetNewViewModel = createViewModelHelper(PasswordSetNewScreenViewModel::class.java)
    val recoveryCodesEntryViewModel = createViewModelHelper(RecoveryCodesEntryScreenViewModel::class.java)
    val recoveryCodesSaveViewModel = createViewModelHelper(RecoveryCodesSaveScreenViewModel::class.java)
    val smsOTPEnrollmentViewModel = createViewModelHelper(SMSOTPEnrollmentScreenViewModel::class.java)
    val smsOTPEntryViewModel = createViewModelHelper(SMSOTPEntryScreenViewModel::class.java)
    val ssoDiscoveryEmailViewModel = createViewModelHelper(SSODiscoveryEmailScreenViewModel::class.java)
    val ssoDiscoveryFallbackViewModel = createViewModelHelper(SSODiscoveryFallbackScreenViewModel::class.java)
    val ssoDiscoveryMenuViewModel = createViewModelHelper(SSODiscoveryMenuScreenViewModel::class.java)
    val totpEnrollmentViewModel = createViewModelHelper(TOTPEnrollmentScreenViewModel::class.java)
    val totpEntryViewModel = createViewModelHelper(TOTPEntryScreenViewModel::class.java)

    LaunchedEffect(rootAppState.currentRoute) {
        rootAppState.currentRoute?.let {
            navController.navigate(it) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = false
                    inclusive = true
                }
            }
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
                        DeepLinkParserScreen(
                            viewModel = deepLinkViewModel,
                            deepLinkTokenPair = rootAppState.deeplinkTokenPair,
                        )
                    }
                    composable<Routes.Discovery> {
                        DiscoveryScreen(viewModel = discoveryViewModel)
                    }
                    composable<Routes.EmailConfirmation> {
                        EmailConfirmationScreen(
                            route = EmailConfirmationScreenRoute.EmailConfirmation,
                            viewModel = emailConfirmationScreenViewModel,
                        )
                    }
                    composable<Routes.EmailMethodSelection> {
                        EmailMethodSelectionScreen(viewModel = emailMethodSelectionViewModel)
                    }
                    composable<Routes.EmailOTPEntry> {
                        EmailOTPEntryScreen(viewModel = emailOTPEntryViewModel)
                    }
                    composable<Routes.Error> {
                        ErrorScreen(viewModel = errorViewModel)
                    }
                    composable<Routes.Main> {
                        MainScreen(viewModel = mainViewModel)
                    }
                    composable<Routes.MFAEnrollmentSelection> {
                        MFAEnrollmentSelectionScreen(viewModel = mfaEnrollmentViewModel)
                    }
                    composable<Routes.PasswordAuthenticate> {
                        PasswordAuthenticateScreen(viewModel = passwordAuthenticateViewModel)
                    }
                    composable<Routes.PasswordForgot> {
                        PasswordForgotScreen(viewModel = passwordForgotViewModel)
                    }
                    composable<Routes.PasswordReset> {
                        PasswordResetScreen(viewModel = passwordResetViewModel)
                    }
                    composable<Routes.PasswordResetVerifyConfirmation> {
                        EmailConfirmationScreen(
                            route = EmailConfirmationScreenRoute.PasswordResetVerifyConfirmation,
                            viewModel = emailConfirmationScreenViewModel,
                        )
                    }
                    composable<Routes.PasswordSetNew> {
                        PasswordSetNewScreen(viewModel = passwordSetNewViewModel)
                    }
                    composable<Routes.PasswordSetNewConfirmation> {
                        EmailConfirmationScreen(
                            route = EmailConfirmationScreenRoute.PasswordSetNewConfirmation,
                            viewModel = emailConfirmationScreenViewModel,
                        )
                    }
                    composable<Routes.RecoveryCodeEntry> {
                        RecoveryCodesEntryScreen(viewModel = recoveryCodesEntryViewModel)
                    }
                    composable<Routes.RecoveryCodeSave> {
                        RecoveryCodesSaveScreen(viewModel = recoveryCodesSaveViewModel)
                    }
                    composable<Routes.SMSOTPEnrollment> {
                        SMSOTPEnrollmentScreen(viewModel = smsOTPEnrollmentViewModel)
                    }
                    composable<Routes.SMSOTPEntry> {
                        SMSOTPEntryScreen(viewModel = smsOTPEntryViewModel)
                    }
                    composable<Routes.SSODiscoveryEmail> {
                        SSODiscoveryEmailScreen(viewModel = ssoDiscoveryEmailViewModel)
                    }
                    composable<Routes.SSODiscoveryFallback> {
                        SSODiscoveryFallbackScreen(viewModel = ssoDiscoveryFallbackViewModel)
                    }
                    composable<Routes.SSODiscoveryMenu> {
                        SSODiscoveryMenuScreen(viewModel = ssoDiscoveryMenuViewModel)
                    }
                    composable<Routes.Success> {
                        SuccessScreen()
                    }
                    composable<Routes.TOTPEnrollment> {
                        TOTPEnrollmentScreen(viewModel = totpEnrollmentViewModel)
                    }
                    composable<Routes.TOTPEntry> {
                        TOTPEntryScreen(viewModel = totpEntryViewModel)
                    }
                }
                rootAppState.errorToastText?.let {
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
        if (rootAppState.isLoading) {
            LoadingDialog()
        }
    }
}

@JacocoExcludeGenerated
internal data class RootAppState(
    val currentRoute: Route? = null,
    val deeplinkTokenPair: DeeplinkTokenPair? = null,
    val isLoading: Boolean = false,
    val errorToastText: String? = null,
)

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
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.shared.components.LoadingDialog
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.theme.LocalStytchB2BProductConfig
import com.stytch.sdk.ui.shared.theme.LocalStytchBootstrapData
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun StytchB2BAuthenticationApp(
    state: State<B2BUIState?>,
    dispatch: Dispatch,
    savedStateHandle: SavedStateHandle,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val bootstrapData = LocalStytchBootstrapData.current
    val productConfig = LocalStytchB2BProductConfig.current
    val startDestination = Routes.Main
    val currentState = state.value ?: return
    LaunchedEffect(currentState.currentRoute) {
        navController.navigate(currentState.currentRoute)
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
                composable<Routes.Discovery> {
                    PageTitle(text = "Discovery")
                }
                composable<Routes.EmailConfirmation> {
                    PageTitle(text = "EmailConfirmation")
                }
                composable<Routes.Error> {
                    PageTitle(text = "Error")
                }
                composable<Routes.Main> {
                    PageTitle(text = "Main")
                }
                composable<Routes.MFAEnrollmentSelection> {
                    PageTitle(text = "MFAEnrollmentSelection")
                }
                composable<Routes.PasswordAuthenticate> {
                    PageTitle(text = "PasswordAuthenticate")
                }
                composable<Routes.PasswordForgot> {
                    PageTitle(text = "PasswordForgot")
                }
                composable<Routes.PasswordReset> {
                    PageTitle(text = "PasswordReset")
                }
                composable<Routes.PasswordResetVerifyConfirmation> {
                    PageTitle(text = "PasswordResetVerifyConfirmation")
                }
                composable<Routes.PasswordSetNew> {
                    PageTitle(text = "PasswordSetNew")
                }
                composable<Routes.PasswordSetNewConfirmation> {
                    PageTitle(text = "PasswordSetNewConfirmation")
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
        if (state.value?.isLoading == true) {
            LoadingDialog()
        }
    }
}

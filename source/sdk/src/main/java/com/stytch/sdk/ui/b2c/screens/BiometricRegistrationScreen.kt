package com.stytch.sdk.ui.b2c.screens

import android.os.Parcelable
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.R
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.b2c.AuthenticationActivity
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.components.StytchTextButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class BiometricRegistrationScreen(
    val previousAuthenticationResult: StytchResult<*>,
) : AndroidScreen(),
    Parcelable {
    @Composable
    override fun Content() {
        val context = LocalActivity.current as AuthenticationActivity
        val viewModel =
            viewModel<BiometricRegistrationScreenViewModel>(
                factory = BiometricRegistrationScreenViewModel.factory(context.savedStateHandle),
            )
        LaunchedEffect(Unit) {
            viewModel.eventFlow.collectLatest {
                when (it) {
                    is EventState.Authenticated -> context.returnAuthenticationResult(it.result)
                    else -> {}
                }
            }
        }
        BiometricRegistrationScreenComposable(
            registerBiometrics = viewModel::registerBiometrics,
            skipRegistration = { context.returnAuthenticationResult(previousAuthenticationResult, false) },
        )
    }
}

@Composable
private fun BiometricRegistrationScreenComposable(
    registerBiometrics: (FragmentActivity) -> Unit,
    skipRegistration: () -> Unit,
) {
    val context = LocalActivity.current as FragmentActivity
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        PageTitle(
            text = stringResource(R.string.stytch_b2c_biometric_registration_title),
            textAlign = TextAlign.Start,
        )
        BodyText(text = stringResource(R.string.stytch_b2c_biometric_registration_body))
        StytchButton(
            enabled = true,
            onClick = { registerBiometrics(context) },
            text = stringResource(R.string.stytch_b2c_button_enroll_biometrics),
        )
        StytchTextButton(text = stringResource(R.string.stytch_b2c_button_skip_for_now), onClick = skipRegistration)
    }
}

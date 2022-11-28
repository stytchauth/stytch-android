package com.stytch.exampleapp.ui

import android.content.Intent
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.stytch.exampleapp.BiometricsViewModel
import com.stytch.exampleapp.R
import com.stytch.sdk.StytchClient
import timber.log.Timber

@Composable
fun BiometricsScreen(navController: NavController) {
    val viewModel: BiometricsViewModel = viewModel()
    val loading = viewModel.loadingState.collectAsState()
    val responseState = viewModel.currentResponse.collectAsState()
    val context = LocalContext.current as FragmentActivity
    val executor = ContextCompat.getMainExecutor(context)
    val biometricManager = BiometricManager.from(context)
    when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
        BiometricManager.BIOMETRIC_SUCCESS ->
            Timber.d("App can authenticate using biometrics.")
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
            Timber.e("No biometric features available on this device.")
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
            Timber.e("Biometric features are currently unavailable.")
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            // Prompts the user to create credentials that your app accepts.
            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
            }
            context.startActivityForResult(enrollIntent, 12345)
        }
        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
            TODO()
        }
        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
            TODO()
        }
        BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
            TODO()
        }
    }
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric login")
        .setSubtitle("Log in using your biometric credential")
        .setNegativeButtonText("Use account password")
        .build()
    val biometricsRegistrationPrompt = BiometricPrompt(
        context,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                viewModel.showBiometricsError("Authentication error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                viewModel.registerBiometrics(context)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                viewModel.showBiometricsError("Authentication failed")
            }
        }
    )
    val biometricsAuthenticatePrompt = BiometricPrompt(
        context,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                viewModel.showBiometricsError("Authentication error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                viewModel.authenticateBiometrics(context)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                viewModel.showBiometricsError("Authentication failed")
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                R.string.biometrics_registration_exists,
                StytchClient.biometrics.registrationAvailable,
            )
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.biometrics_register),
            onClick = { biometricsRegistrationPrompt.authenticate(promptInfo) }
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.biometrics_authenticate),
            onClick = { biometricsAuthenticatePrompt.authenticate(promptInfo) }
        )
        if (loading.value) {
            CircularProgressIndicator()
        } else {
            Text(text = responseState.value, modifier = Modifier.padding(8.dp))
        }
    }
}

package com.stytch.sdk.ui.b2b.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.screens.DeepLinkParserScreenViewModel
import com.stytch.sdk.ui.b2b.screens.DiscoveryScreenViewModel
import com.stytch.sdk.ui.b2b.screens.EmailConfirmationScreenViewModel
import com.stytch.sdk.ui.b2b.screens.MFAEnrollmentSelectionScreenViewModel
import com.stytch.sdk.ui.b2b.screens.MainScreenViewModel
import com.stytch.sdk.ui.b2b.screens.PasswordAuthenticateScreenViewModel
import com.stytch.sdk.ui.b2b.screens.PasswordForgotScreenViewModel
import com.stytch.sdk.ui.b2b.screens.PasswordResetScreenViewModel
import com.stytch.sdk.ui.b2b.screens.PasswordSetNewScreenViewModel
import com.stytch.sdk.ui.b2b.screens.RecoveryCodesEntryScreenViewModel
import com.stytch.sdk.ui.b2b.screens.RecoveryCodesSaveScreenViewModel
import com.stytch.sdk.ui.b2b.screens.TOTPEnrollmentScreenViewModel
import com.stytch.sdk.ui.b2b.screens.TOTPEntryScreenViewModel
import kotlinx.coroutines.flow.StateFlow

internal class B2BUIViewModelFactory(
    private val state: StateFlow<B2BUIState>,
    private val dispatchAction: suspend (B2BUIAction) -> Unit,
    private val productConfig: StytchB2BProductConfig,
) : ViewModelProvider.AndroidViewModelFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when (modelClass) {
            MainScreenViewModel::class.java -> MainScreenViewModel(state, dispatchAction, productConfig) as T
            DeepLinkParserScreenViewModel::class.java ->
                DeepLinkParserScreenViewModel(
                    state,
                    dispatchAction,
                    productConfig,
                ) as T
            DiscoveryScreenViewModel::class.java -> DiscoveryScreenViewModel(state, dispatchAction) as T
            EmailConfirmationScreenViewModel::class.java ->
                EmailConfirmationScreenViewModel(
                    state,
                    dispatchAction,
                    productConfig,
                ) as T
            PasswordSetNewScreenViewModel::class.java ->
                PasswordSetNewScreenViewModel(
                    state,
                    dispatchAction,
                    productConfig,
                ) as T
            PasswordResetScreenViewModel::class.java ->
                PasswordResetScreenViewModel(
                    state,
                    dispatchAction,
                ) as T
            PasswordAuthenticateScreenViewModel::class.java ->
                PasswordAuthenticateScreenViewModel(
                    state,
                    dispatchAction,
                    productConfig,
                ) as T
            PasswordForgotScreenViewModel::class.java ->
                PasswordForgotScreenViewModel(
                    state,
                    dispatchAction,
                    productConfig,
                ) as T
            MFAEnrollmentSelectionScreenViewModel::class.java ->
                MFAEnrollmentSelectionScreenViewModel(
                    state,
                    dispatchAction,
                    productConfig,
                ) as T
            TOTPEnrollmentScreenViewModel::class.java ->
                TOTPEnrollmentScreenViewModel(state, dispatchAction) as T
            TOTPEntryScreenViewModel::class.java ->
                TOTPEntryScreenViewModel(state, dispatchAction, productConfig) as T
            RecoveryCodesSaveScreenViewModel::class.java ->
                RecoveryCodesSaveScreenViewModel(state, dispatchAction) as T
            RecoveryCodesEntryScreenViewModel::class.java ->
                RecoveryCodesEntryScreenViewModel(state, dispatchAction, productConfig) as T
            else -> super.create(modelClass)
        }
}

package com.stytch.sdk.ui.b2b.screens.totpEntry

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.MFAPrimaryInfoState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseTOTPAuthenticate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class TOTPEntryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val _totpEntryState =
        MutableStateFlow(
            TOTPEntryScreenState(
                isEnrolling = state.value.mfaTOTPState?.isEnrolling == true,
                isSmsOtpAvailable = isSmsOtpAvailable(state.value.mfaPrimaryInfoState),
            ),
        )
    val totpEntryState = _totpEntryState.asStateFlow()

    private val useTOTPAuthenticate = UseTOTPAuthenticate(state, productConfig, ::request)

    private fun isSmsOtpAvailable(primaryInfoState: MFAPrimaryInfoState?): Boolean {
        if (primaryInfoState == null) return false
        return state.value.mfaTOTPState?.isEnrolling == false &&
            primaryInfoState.enrolledMfaMethods.contains(MfaMethod.SMS) &&
            primaryInfoState.organizationMfaOptionsSupported.contains(MfaMethod.SMS)
    }

    private fun validateCode(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            useTOTPAuthenticate(code = code)
                .onFailure {
                    _totpEntryState.value =
                        _totpEntryState.value.copy(
                            errorMessage = "Failed to validate TOTP code",
                        )
                }
        }
    }

    fun handle(action: TOTPEntryScreenAction) {
        when (action) {
            TOTPEntryScreenAction.GoToTOTPEnrollment -> dispatch(SetNextRoute(Routes.TOTPEnrollment))
            TOTPEntryScreenAction.TextMeACode -> dispatch(SetNextRoute(Routes.SMSOTPEntry))
            TOTPEntryScreenAction.UseRecoveryCode -> dispatch(SetNextRoute(Routes.RecoveryCodeEntry))
            is TOTPEntryScreenAction.ValidateCode -> validateCode(action.code)
        }
    }
}

@JacocoExcludeGenerated
internal data class TOTPEntryScreenState(
    val isEnrolling: Boolean,
    val isSmsOtpAvailable: Boolean,
    val errorMessage: String? = null,
)

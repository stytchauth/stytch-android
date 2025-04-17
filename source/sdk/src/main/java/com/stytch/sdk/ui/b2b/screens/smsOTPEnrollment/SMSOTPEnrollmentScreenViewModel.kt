package com.stytch.sdk.ui.b2b.screens.smsOTPEnrollment

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseOTPSMSSend
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPhoneNumber
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class SMSOTPEnrollmentScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val useSmsOtpSend = UseOTPSMSSend(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val useUpdateMemberPhoneNumber = UseUpdateMemberPhoneNumber(state, ::dispatch)
    val phoneNumberState =
        state
            .map {
                state.value.phoneNumberState
            }.stateIn(viewModelScope, SharingStarted.Lazily, state.value.phoneNumberState)

    fun handle(action: SMSOTPEnrollmentAction) {
        when (action) {
            SMSOTPEnrollmentAction.GoToMFAEnrollment -> dispatch(SetNextRoute(Routes.MFAEnrollmentSelection))
            is SMSOTPEnrollmentAction.Send -> useSmsOtpSend(action.isEnrolling)
            is SMSOTPEnrollmentAction.UpdateMemberCountryCode ->
                useUpdateMemberPhoneNumber(
                    countryCode = action.countryCode,
                    phoneNumber = null,
                )
            is SMSOTPEnrollmentAction.UpdateMemberPhoneNumber ->
                useUpdateMemberPhoneNumber(
                    countryCode = null,
                    phoneNumber = action.phoneNumber,
                )
        }
    }
}

internal sealed class SMSOTPEnrollmentAction {
    data object GoToMFAEnrollment : SMSOTPEnrollmentAction()

    @JacocoExcludeGenerated
    data class UpdateMemberCountryCode(
        val countryCode: String,
    ) : SMSOTPEnrollmentAction()

    @JacocoExcludeGenerated
    data class UpdateMemberPhoneNumber(
        val phoneNumber: String,
    ) : SMSOTPEnrollmentAction()

    @JacocoExcludeGenerated
    data class Send(
        val isEnrolling: Boolean,
    ) : SMSOTPEnrollmentAction()
}

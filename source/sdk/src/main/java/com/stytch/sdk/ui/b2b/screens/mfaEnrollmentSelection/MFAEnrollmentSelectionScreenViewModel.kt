package com.stytch.sdk.ui.b2b.screens.mfaEnrollmentSelection

import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.domain.ALL_MFA_METHODS
import com.stytch.sdk.ui.b2b.domain.getEnabledMethods
import com.stytch.sdk.ui.b2b.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class MFAEnrollmentSelectionScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val _sortedMfaMethods = MutableStateFlow<List<MfaMethod>>(emptyList())
    val sortedMfaMethods = _sortedMfaMethods.asStateFlow()

    init {
        val mfaProductInclude = productConfig.mfaProductInclude
        val mfaProductOrder =
            productConfig.mfaProductOrder.toSet().ifEmpty { ALL_MFA_METHODS }
        val organizationMfaOptionsSupported =
            state.value.mfaPrimaryInfoState?.organizationMfaOptionsSupported ?: emptyList()
        val optionsToDisplay =
            getEnabledMethods(
                orgSupportedMethods = organizationMfaOptionsSupported,
                uiIncludedMfaMethods = mfaProductInclude,
            )
        _sortedMfaMethods.value =
            sortMfaMethodsIntoCorrectOrder(
                allOptions = optionsToDisplay,
                expectedOrdering = mfaProductOrder,
            )
    }

    fun selectMfaMethod(mfaMethod: MfaMethod) {
        when (mfaMethod) {
            MfaMethod.SMS -> dispatch(SetNextRoute(Routes.SMSOTPEnrollment))
            MfaMethod.TOTP -> dispatch(SetNextRoute(Routes.TOTPEnrollment))
            MfaMethod.NONE -> { /* noop */ }
        }
    }

    private fun sortMfaMethodsIntoCorrectOrder(
        allOptions: Set<MfaMethod>,
        expectedOrdering: Set<MfaMethod>,
    ): List<MfaMethod> {
        val sortedOptions = mutableSetOf<MfaMethod>()
        expectedOrdering.forEach { mfaMethod ->
            if (allOptions.contains(mfaMethod)) {
                sortedOptions.add(mfaMethod)
            }
        }
        // append remaining options
        allOptions.forEach { mfaMethod -> sortedOptions.add(mfaMethod) }
        return sortedOptions.toList()
    }
}

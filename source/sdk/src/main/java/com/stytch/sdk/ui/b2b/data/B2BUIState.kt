package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import com.stytch.sdk.b2b.network.models.DiscoveredOrganization
import com.stytch.sdk.b2b.network.models.InternalOrganizationData
import com.stytch.sdk.common.errors.StytchError
import com.stytch.sdk.ui.b2b.navigation.Route
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.PasswordState
import com.stytch.sdk.ui.shared.data.PhoneNumberState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class B2BUIState(
    val currentRoute: Route = Routes.Main,
    val isLoading: Boolean = false,
    val stytchError: StytchError? = null,
    val errorToastText: String? = null,
    val emailState: EmailState = EmailState(),
    val phoneNumberState: PhoneNumberState = PhoneNumberState(),
    val passwordState: PasswordState = PasswordState(),
    val authFlowType: AuthFlowType = AuthFlowType.ORGANIZATION,
    val activeOrganization: InternalOrganizationData? = null,
    val discoveredOrganizations: List<DiscoveredOrganization>? = null,
    val mfaPrimaryInfoState: MFAPrimaryInfoState? = null,
    val mfaSMSState: MFASMSState? = null,
    val mfaTOTPState: MFATOTPState? = null,
    val postAuthScreen: Route = Routes.Success,
) : Parcelable {
    internal companion object {
        const val SAVED_STATE_KEY = "StytchB2BUIState"
    }
}

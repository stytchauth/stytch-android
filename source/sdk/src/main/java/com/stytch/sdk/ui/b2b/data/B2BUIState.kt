package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.DiscoveredOrganization
import com.stytch.sdk.b2b.network.models.InternalOrganizationData
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.common.DeeplinkTokenPair
import com.stytch.sdk.ui.b2b.navigation.Route
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.PasswordState
import com.stytch.sdk.ui.shared.data.PhoneNumberState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class B2BUIState(
    val currentRoute: Route? = null,
    val isLoading: Boolean = false,
    val errorToastText: String? = null,
    val emailState: EmailState = EmailState(shouldValidateEmail = false),
    val phoneNumberState: PhoneNumberState = PhoneNumberState(),
    val passwordState: PasswordState = PasswordState(),
    val authFlowType: AuthFlowType = AuthFlowType.ORGANIZATION,
    val activeOrganization: InternalOrganizationData? = null,
    val discoveredOrganizations: List<DiscoveredOrganization>? = null,
    val mfaPrimaryInfoState: MFAPrimaryInfoState? = null,
    val mfaSMSState: MFASMSState? = null,
    val mfaTOTPState: MFATOTPState? = null,
    val postAuthScreen: Route = Routes.Success,
    val deeplinkTokenPair: DeeplinkTokenPair? = null,
    val b2BErrorType: B2BErrorType? = null,
    val uiIncludedMfaMethods: List<MfaMethod> = emptyList(),
    val primaryAuthMethods: List<AllowedAuthMethods> = emptyList(),
    val isSearchingForOrganizationBySlug: Boolean = false,
) : Parcelable {
    internal companion object {
        const val SAVED_STATE_KEY = "StytchB2BUIState"
    }
}

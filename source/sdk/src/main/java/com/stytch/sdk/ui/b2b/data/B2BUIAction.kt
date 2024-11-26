package com.stytch.sdk.ui.b2b.data

import com.stytch.sdk.b2b.network.models.DiscoveredOrganization
import com.stytch.sdk.b2b.network.models.IB2BAuthDataWithMFA
import com.stytch.sdk.b2b.network.models.InternalOrganizationData
import com.stytch.sdk.common.errors.StytchError
import com.stytch.sdk.ui.b2b.navigation.Route
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.PasswordState
import com.stytch.sdk.ui.shared.data.PhoneNumberState

internal sealed interface B2BUIAction

internal data class UpdateEmailState(
    val emailState: EmailState,
) : B2BUIAction

internal data class UpdatePasswordState(
    val passwordState: PasswordState,
) : B2BUIAction

internal data class UpdatePhoneNumberState(
    val phoneNumberState: PhoneNumberState,
) : B2BUIAction

internal data class SetAuthFlowType(
    val authFlowType: AuthFlowType,
) : B2BUIAction

internal data class SetActiveOrganization(
    val organization: InternalOrganizationData,
) : B2BUIAction

internal data class SetDiscoveredOrganizations(
    val discoveredOrganizations: List<DiscoveredOrganization>,
) : B2BUIAction

internal data class UpdateMfaPrimaryInfoState(
    val mfaPrimaryInfoState: MFAPrimaryInfoState,
) : B2BUIAction

internal data class UpdateMfaSmsState(
    val mfaSmsState: MFASMSState,
) : B2BUIAction

internal data class UpdateMfaTotpState(
    val mfaTotpState: MFATOTPState,
) : B2BUIAction

internal data class SetPostAuthScreen(
    val route: Route,
) : B2BUIAction

internal data class SetLoading(
    val value: Boolean,
) : B2BUIAction

internal data class SetStytchError(
    val stytchError: StytchError,
) : B2BUIAction

internal data class SetB2BError(
    val b2bError: B2BErrorType,
) : B2BUIAction

internal data class NavigateTo(
    val route: Route,
) : B2BUIAction

internal data class HandleStepUpAuthentication(
    val response: IB2BAuthDataWithMFA,
) : B2BUIAction

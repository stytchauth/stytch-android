package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.network.models.B2BSearchMemberResponseData
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetLoading
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseSendCorrectPasswordReset(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    productConfig: StytchB2BProductConfig,
    memberSearchRequest: PerformRequest<B2BSearchMemberResponseData>,
    emailSendRequest: PerformRequest<BasicData>,
) {
    val useSearchMember = UseSearchMember(memberSearchRequest)
    val usePasswordResetByEmailStart =
        UsePasswordResetByEmailStart(scope, state, dispatch, productConfig, emailSendRequest)
    val useNonMemberPasswordReset =
        UseNonMemberPasswordReset(scope, state, dispatch, productConfig, emailSendRequest)
    val usePasswordDiscoveryResetByEmailStart =
        UsePasswordDiscoveryResetByEmailStart(scope, state, productConfig, dispatch, emailSendRequest)

    operator fun invoke() {
        dispatch(SetLoading(true))
        val organizationId = state.value.activeOrganization?.organizationId
        if (organizationId == null) {
            usePasswordDiscoveryResetByEmailStart()
            return
        }
        scope.launch {
            useSearchMember(
                emailAddress = state.value.emailState.emailAddress,
                organizationId = organizationId,
            ).onSuccess {
                dispatch(SetLoading(false))
                if (it.member != null) {
                    // member exists, so send them a reset
                    usePasswordResetByEmailStart()
                } else {
                    // no member, so drop them in the nonMemberReset flow
                    return@onSuccess useNonMemberPasswordReset()
                }
            }.onFailure {
                dispatch(SetLoading(false))
            }
        }
    }
}

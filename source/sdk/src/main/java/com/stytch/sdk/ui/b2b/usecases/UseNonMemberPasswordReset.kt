package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.R
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinks
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetGenericError
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.extensions.emailEligibleForJITProvisioning
import com.stytch.sdk.ui.b2b.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseNonMemberPasswordReset(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val productConfig: StytchB2BProductConfig,
    private val request: PerformRequest<BasicData>,
) {
    operator fun invoke() {
        val organization = state.value.activeOrganization ?: return
        if (!organization.emailEligibleForJITProvisioning(state.value.emailState.emailAddress)) {
            return dispatch(
                SetGenericError(
                    errorMessageId = R.string.stytch_b2b_jit_ineligible,
                    arguments = listOf(state.value.emailState.emailAddress, organization.organizationName),
                ),
            )
        }
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.magicLinks.email.loginOrSignup(
                    B2BMagicLinks.EmailMagicLinks.Parameters(
                        email = state.value.emailState.emailAddress,
                        organizationId = organization.organizationId,
                        loginRedirectUrl = getRedirectUrl(),
                        signupRedirectUrl = getRedirectUrl(),
                        loginTemplateId = productConfig.emailMagicLinksOptions.loginTemplateId,
                        signupTemplateId = productConfig.emailMagicLinksOptions.signupTemplateId,
                        locale = productConfig.locale,
                    ),
                )
            }.onSuccess {
                dispatch(SetNextRoute(Routes.PasswordResetVerifyConfirmation))
            }.onFailure {
                dispatch(SetGenericError(errorMessageId = R.string.stytch_b2b_unable_to_verify_email))
            }
        }
    }
}

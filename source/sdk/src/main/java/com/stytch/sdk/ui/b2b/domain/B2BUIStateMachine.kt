package com.stytch.sdk.ui.b2b.domain

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.HandleStepUpAuthentication
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.SetActiveOrganization
import com.stytch.sdk.ui.b2b.data.SetAuthFlowType
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetDeeplinkTokenPair
import com.stytch.sdk.ui.b2b.data.SetDiscoveredOrganizations
import com.stytch.sdk.ui.b2b.data.SetGenericError
import com.stytch.sdk.ui.b2b.data.SetIsSearchingForOrganizationBySlug
import com.stytch.sdk.ui.b2b.data.SetLoading
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.SetPostAuthScreen
import com.stytch.sdk.ui.b2b.data.UpdateEmailState
import com.stytch.sdk.ui.b2b.data.UpdateMfaPrimaryInfoState
import com.stytch.sdk.ui.b2b.data.UpdateMfaSmsState
import com.stytch.sdk.ui.b2b.data.UpdateMfaTotpState
import com.stytch.sdk.ui.b2b.data.UpdatePasswordState
import com.stytch.sdk.ui.b2b.data.UpdatePhoneNumberState
import com.stytch.sdk.ui.b2b.navigation.Routes
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BUIStateMachine(
    initialState: B2BUIState,
) : FlowReduxStateMachine<B2BUIState, B2BUIAction>(initialState) {
    init {
        spec {
            inState<B2BUIState> {
                on<UpdateEmailState> { action, state ->
                    state.mutate {
                        copy(emailState = action.emailState)
                    }
                }
                on<UpdatePasswordState> { action, state ->
                    state.mutate {
                        copy(passwordState = action.passwordState)
                    }
                }
                on<UpdatePhoneNumberState> { action, state ->
                    state.mutate {
                        copy(phoneNumberState = action.phoneNumberState)
                    }
                }
                on<SetAuthFlowType> { action, state ->
                    state.mutate {
                        copy(authFlowType = action.authFlowType)
                    }
                }
                on<SetActiveOrganization> { action, state ->
                    state.mutate {
                        copy(activeOrganization = action.organization)
                    }
                }
                on<SetDiscoveredOrganizations> { action, state ->
                    state.mutate {
                        copy(discoveredOrganizations = action.discoveredOrganizations)
                    }
                }
                on<UpdateMfaPrimaryInfoState> { action, state ->
                    state.mutate {
                        copy(mfaPrimaryInfoState = action.mfaPrimaryInfoState)
                    }
                }
                on<UpdateMfaSmsState> { action, state ->
                    state.mutate {
                        copy(mfaSMSState = action.mfaSmsState)
                    }
                }
                on<UpdateMfaTotpState> { action, state ->
                    state.mutate {
                        copy(mfaTOTPState = action.mfaTotpState)
                    }
                }
                on<SetPostAuthScreen> { action, state ->
                    state.mutate {
                        copy(postAuthScreen = action.route)
                    }
                }
                on<SetLoading> { action, state ->
                    state.mutate {
                        copy(isLoading = action.value)
                    }
                }
                on<SetGenericError> { action, state ->
                    state.mutate {
                        copy(errorToastText = action.errorText)
                    }
                }
                on<SetB2BError> { action, state ->
                    state.mutate {
                        copy(
                            currentRoute = Routes.Error,
                            b2BErrorType = action.b2bError,
                        )
                    }
                }
                on<SetDeeplinkTokenPair> { action, state ->
                    state.mutate {
                        copy(deeplinkTokenPair = action.deeplinkTokenPair)
                    }
                }
                on<SetNextRoute> { action, state ->
                    state.mutate {
                        copy(
                            currentRoute = action.route,
                            isLoading = false,
                            b2BErrorType = null,
                        )
                    }
                }
                on<HandleStepUpAuthentication> { action, state ->
                    handleResponseThatNeedsAdditionalAuthentication(state, action.response)
                }
                on<SetIsSearchingForOrganizationBySlug> { action, state ->
                    state.mutate {
                        copy(
                            isSearchingForOrganizationBySlug = action.isSearchingForOrganizationBySlug,
                        )
                    }
                }
                on<ResetEverything> { _, state ->
                    // These are configured post-launch, so we can't just do _all_ defaults
                    val uiIncludedMfaMethods = state.snapshot.uiIncludedMfaMethods
                    val authFlowType = state.snapshot.authFlowType
                    state.mutate {
                        B2BUIState().copy(
                            authFlowType = authFlowType,
                            currentRoute = if (authFlowType == AuthFlowType.DISCOVERY) Routes.Main else null,
                            uiIncludedMfaMethods = uiIncludedMfaMethods,
                        )
                    }
                }
            }
        }
    }
}

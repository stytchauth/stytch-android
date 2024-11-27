package com.stytch.sdk.ui.b2b.domain

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.HandleStepUpAuthentication
import com.stytch.sdk.ui.b2b.data.NavigateTo
import com.stytch.sdk.ui.b2b.data.SetActiveOrganization
import com.stytch.sdk.ui.b2b.data.SetAuthFlowType
import com.stytch.sdk.ui.b2b.data.SetDeeplinkToken
import com.stytch.sdk.ui.b2b.data.SetDiscoveredOrganizations
import com.stytch.sdk.ui.b2b.data.SetGenericError
import com.stytch.sdk.ui.b2b.data.SetLoading
import com.stytch.sdk.ui.b2b.data.SetPostAuthScreen
import com.stytch.sdk.ui.b2b.data.SetStytchError
import com.stytch.sdk.ui.b2b.data.UpdateEmailState
import com.stytch.sdk.ui.b2b.data.UpdateMfaPrimaryInfoState
import com.stytch.sdk.ui.b2b.data.UpdateMfaSmsState
import com.stytch.sdk.ui.b2b.data.UpdateMfaTotpState
import com.stytch.sdk.ui.b2b.data.UpdatePasswordState
import com.stytch.sdk.ui.b2b.data.UpdatePhoneNumberState
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
                on<SetStytchError> { action, state ->
                    state.mutate {
                        copy(stytchError = action.stytchError)
                    }
                }
                on<SetGenericError> { action, state ->
                    state.mutate {
                        copy(errorToastText = action.errorText)
                    }
                }
                on<SetDeeplinkToken> { action, state ->
                    state.mutate {
                        copy(token = action.token)
                    }
                }
                on<NavigateTo> { action, state ->
                    state.mutate {
                        copy(currentRoute = action.route)
                    }
                }
                on<HandleStepUpAuthentication> { action, state ->
                    handleResponseThatNeedsAdditionalAuthentication(state, action.response)
                }
            }
        }
    }
}

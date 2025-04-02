package com.stytch.sdk.ui.b2b.screens.passwordAuthenticate

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UsePasswordAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UsePasswordDiscoveryAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPassword
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.PasswordState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class PasswordAuthenticateScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val useUpdateMemberPassword = UseUpdateMemberPassword(state, ::dispatch)
    private val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    private val usePasswordAuthenticate =
        UsePasswordAuthenticate(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val usePasswordDiscoveryAuthenticate =
        UsePasswordDiscoveryAuthenticate(viewModelScope, state, ::dispatch, ::request)
    val passwordAuthenticateScreenState =
        state
            .map {
                PasswordAuthenticateScreenState(
                    emailState = state.value.emailState,
                    passwordState = state.value.passwordState,
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                PasswordAuthenticateScreenState(
                    emailState = state.value.emailState,
                    passwordState = state.value.passwordState,
                ),
            )

    private fun authenticatePassword() {
        if (state.value.activeOrganization == null) {
            usePasswordDiscoveryAuthenticate()
        } else {
            usePasswordAuthenticate()
        }
    }

    fun handle(action: PasswordAuthenticateAction) {
        when (action) {
            PasswordAuthenticateAction.Authenticate -> authenticatePassword()
            is PasswordAuthenticateAction.UpdateMemberEmailAddress -> useUpdateMemberEmailAddress(action.emailAddress)
            is PasswordAuthenticateAction.UpdateMemberPassword -> useUpdateMemberPassword(action.password)
            PasswordAuthenticateAction.GoToPasswordForgot -> dispatch(SetNextRoute(Routes.PasswordForgot))
            PasswordAuthenticateAction.ResetEverything -> dispatch(ResetEverything)
        }
    }
}

@JacocoExcludeGenerated
internal data class PasswordAuthenticateScreenState(
    val emailState: EmailState = EmailState(),
    val passwordState: PasswordState = PasswordState(),
)

internal sealed class PasswordAuthenticateAction {
    @JacocoExcludeGenerated
    data class UpdateMemberEmailAddress(
        val emailAddress: String,
    ) : PasswordAuthenticateAction()

    @JacocoExcludeGenerated
    data class UpdateMemberPassword(
        val password: String,
    ) : PasswordAuthenticateAction()

    data object Authenticate : PasswordAuthenticateAction()

    data object ResetEverything : PasswordAuthenticateAction()

    data object GoToPasswordForgot : PasswordAuthenticateAction()
}

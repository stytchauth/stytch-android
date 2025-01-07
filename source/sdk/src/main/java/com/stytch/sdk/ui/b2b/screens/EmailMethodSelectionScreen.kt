package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPDiscoverySend
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPLoginOrSignup
import com.stytch.sdk.ui.b2b.usecases.UseMagicLinksDiscoverySend
import com.stytch.sdk.ui.b2b.usecases.UseMagicLinksEmailLoginOrSignup
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import kotlinx.coroutines.flow.StateFlow

internal class EmailMethodSelectionScreeniewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    val useMagicLinksEmailLoginOrSignup =
        UseMagicLinksEmailLoginOrSignup(viewModelScope, state, ::dispatch, productConfig, ::request)
    val useMagicLinksDiscoverySend =
        UseMagicLinksDiscoverySend(viewModelScope, productConfig, state, ::dispatch, ::request)
    val useEmailOTPLoginOrSignup = UseEmailOTPLoginOrSignup(viewModelScope, state, ::dispatch, productConfig, ::request)
    val useEmailOTPDiscoverySend = UseEmailOTPDiscoverySend(viewModelScope, state, ::dispatch, productConfig, ::request)

    fun selectMethod(method: AllowedAuthMethods) {
        if (method == AllowedAuthMethods.MAGIC_LINK) {
            if (state.value.authFlowType == AuthFlowType.ORGANIZATION) {
                useMagicLinksEmailLoginOrSignup()
            } else {
                useMagicLinksDiscoverySend()
            }
        } else if (method == AllowedAuthMethods.EMAIL_OTP) {
            if (state.value.authFlowType == AuthFlowType.ORGANIZATION) {
                useEmailOTPLoginOrSignup()
            } else {
                useEmailOTPDiscoverySend()
            }
        }
    }
}

private val EMAIL_METHODS = listOf(AllowedAuthMethods.EMAIL_OTP, AllowedAuthMethods.MAGIC_LINK)

@Composable
internal fun EmailMethodSelectionScreen(
    createViewModel: CreateViewModel<EmailMethodSelectionScreeniewModel>,
    viewModel: EmailMethodSelectionScreeniewModel =
        createViewModel(EmailMethodSelectionScreeniewModel::class.java),
) {
    val theme = LocalStytchTheme.current
    Column {
        PageTitle(textAlign = TextAlign.Left, text = "Select how you’d like to continue.")
        Column {
            EMAIL_METHODS.forEach { method ->
                when (method) {
                    AllowedAuthMethods.EMAIL_OTP -> "Email me a log in code"
                    AllowedAuthMethods.MAGIC_LINK -> "Email me a log in link"
                    else -> null
                }?.let { title ->
                    MenuItemWithRightArrow(title = title, onClick = { viewModel.selectMethod(method) })
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(theme.inputBorderColor),
                    )
                }
            }
        }
    }
}

package com.stytch.sdk.ui.b2b.screens.deepLinkParser

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.B2BRedirectType
import com.stytch.sdk.b2b.B2BTokenType
import com.stytch.sdk.common.DeeplinkTokenPair
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseMagicLinksAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseMagicLinksDiscoveryAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseOAuthAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseOAuthDiscoveryAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseSSOAuthenticate
import kotlinx.coroutines.flow.StateFlow

internal class DeepLinkParserScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val useMagicLinksAuthenticate =
        UseMagicLinksAuthenticate(viewModelScope, productConfig, ::dispatch, ::request)
    private val useMagicLinksDiscoveryAuthenticate =
        UseMagicLinksDiscoveryAuthenticate(viewModelScope, state, ::dispatch, ::request)
    private val useOAuthDiscoveryAuthenticate =
        UseOAuthDiscoveryAuthenticate(viewModelScope, state, ::dispatch, ::request)
    private val useOAuthAuthenticate = UseOAuthAuthenticate(viewModelScope, productConfig, ::dispatch, ::request)
    private val useSSOAuthenticate = UseSSOAuthenticate(viewModelScope, ::dispatch, productConfig, ::request)

    internal fun handleDeepLink(pair: DeeplinkTokenPair) {
        if (pair.token.isNullOrEmpty()) return
        when (pair.tokenType as B2BTokenType) {
            B2BTokenType.MULTI_TENANT_MAGIC_LINKS -> useMagicLinksAuthenticate(pair.token)
            B2BTokenType.MULTI_TENANT_PASSWORDS -> dispatch(SetNextRoute(Routes.PasswordReset))
            B2BTokenType.DISCOVERY -> {
                if (pair.redirectType == B2BRedirectType.RESET_PASSWORD) {
                    dispatch(SetNextRoute(Routes.PasswordReset))
                } else {
                    useMagicLinksDiscoveryAuthenticate(pair.token)
                }
            }
            B2BTokenType.DISCOVERY_OAUTH -> useOAuthDiscoveryAuthenticate(pair.token)
            B2BTokenType.OAUTH -> useOAuthAuthenticate(pair.token)
            B2BTokenType.SSO -> useSSOAuthenticate(pair.token)
            B2BTokenType.UNKNOWN -> dispatch(SetB2BError(B2BErrorType.Default))
        }
    }
}

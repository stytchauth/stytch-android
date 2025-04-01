package com.stytch.sdk.ui.b2b.screens.main

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.InternalOrganizationData
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BOAuthProviderConfig
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetLoading
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProduct
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseEffectiveAuthConfig
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPDiscoverySend
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPLoginOrSignup
import com.stytch.sdk.ui.b2b.usecases.UseMagicLinksDiscoverySend
import com.stytch.sdk.ui.b2b.usecases.UseMagicLinksEmailLoginOrSignup
import com.stytch.sdk.ui.b2b.usecases.UseNonMemberPasswordReset
import com.stytch.sdk.ui.b2b.usecases.UseOAuthStart
import com.stytch.sdk.ui.b2b.usecases.UsePasswordAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UsePasswordDiscoveryAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseSSOStart
import com.stytch.sdk.ui.b2b.usecases.UseSearchMember
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailShouldBeValidated
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPassword
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.PasswordState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class MainScreenViewModel(
    private val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val useGetEffectiveAuthConfig = UseEffectiveAuthConfig(state, productConfig)
    private val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    private val useUpdateMemberPassword = UseUpdateMemberPassword(state, ::dispatch)
    private val useMagicLinksEmailLoginOrSignup =
        UseMagicLinksEmailLoginOrSignup(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val useMagicLinksDiscoverySend =
        UseMagicLinksDiscoverySend(viewModelScope, productConfig, state, ::dispatch, ::request)
    private val useSSOStart = UseSSOStart()
    private val useOAuthStart = UseOAuthStart(state)
    private val useSearchMember = UseSearchMember(::request)
    private val usePasswordsAuthenticate =
        UsePasswordAuthenticate(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val usePasswordDiscoveryAuthenticate =
        UsePasswordDiscoveryAuthenticate(viewModelScope, state, ::dispatch, ::request)
    private val useNonMemberPasswordReset =
        UseNonMemberPasswordReset(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val useUpdateMemberEmailShouldBeValidated = UseUpdateMemberEmailShouldBeValidated(state, ::dispatch)
    private val useEmailOTPLoginOrSignup =
        UseEmailOTPLoginOrSignup(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val useEmailOTPDiscoverySend =
        UseEmailOTPDiscoverySend(viewModelScope, state, ::dispatch, productConfig, ::request)

    private val enableEml = productConfig.products.contains(StytchB2BProduct.EMAIL_MAGIC_LINKS)
    private val enableOtp = productConfig.products.contains(StytchB2BProduct.EMAIL_OTP)

    val mainScreenState =
        state.map { it.toMainScreenState() }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            state.value.toMainScreenState(),
        )

    private fun handleEmailPasswordSubmit() {
        val emailAddress = state.value.emailState.emailAddress
        val organization = state.value.activeOrganization
        if (emailAddress.isBlank()) return
        viewModelScope.launch {
            dispatch(SetLoading(true))
            if (organization != null) {
                useSearchMember(
                    emailAddress = emailAddress,
                    organizationId = organization.organizationId,
                ).onSuccess { response ->
                    dispatch(SetLoading(false))
                    if (!response.member?.memberPasswordId.isNullOrEmpty()) {
                        usePasswordsAuthenticate()
                    } else {
                        useNonMemberPasswordReset()
                    }
                }.onFailure {
                    dispatch(SetLoading(false))
                }
            } else {
                usePasswordDiscoveryAuthenticate()
            }
        }
    }

    private fun handleEmailSubmit() {
        if (state.value.activeOrganization != null || state.value.mfaPrimaryInfoState != null) {
            if (enableEml && enableOtp) {
                dispatch(SetNextRoute(Routes.EmailMethodSelection))
            } else if (enableEml) {
                useMagicLinksEmailLoginOrSignup()
            } else if (enableOtp) {
                useEmailOTPLoginOrSignup()
            }
        } else {
            if (enableEml && enableOtp) {
                dispatch(SetNextRoute(Routes.EmailMethodSelection))
            } else if (enableEml) {
                useMagicLinksDiscoverySend()
            } else if (enableOtp) {
                useEmailOTPDiscoverySend()
            }
        }
    }

    private fun handleSSODiscovery() = dispatch(SetNextRoute(Routes.SSODiscoveryEmail))

    fun handleAction(action: MainScreenAction) {
        when (action) {
            is MainScreenAction.DispatchGlobalAction -> dispatch(action.action)
            is MainScreenAction.HandleEmailSubmit -> handleEmailSubmit()
            is MainScreenAction.HandlePasswordSubmit -> handleEmailPasswordSubmit()
            is MainScreenAction.StartSSO -> useSSOStart(action.context, action.connectionId)
            is MainScreenAction.StartSSODiscovery -> handleSSODiscovery()
            is MainScreenAction.StartOAuth -> useOAuthStart(action.context, action.providerConfig)
            is MainScreenAction.SetEmailShouldBeValidated -> useUpdateMemberEmailShouldBeValidated(true)
            is MainScreenAction.UpdateMemberEmailAddress -> useUpdateMemberEmailAddress(action.emailAddress)
            is MainScreenAction.UpdateMemberPassword -> useUpdateMemberPassword(action.password)
        }
    }

    private fun B2BUIState.toMainScreenState() =
        MainScreenState(
            primaryAuthMethods = primaryAuthMethods,
            emailState = emailState,
            passwordState = passwordState,
            authFlowType = authFlowType,
            organizationData = activeOrganization,
            products = useGetEffectiveAuthConfig.products.toList(),
            oauthProviderSettings = useGetEffectiveAuthConfig.oauthProviderSettings.toList(),
        )
}

internal sealed class MainScreenAction {
    @JacocoExcludeGenerated
    data class DispatchGlobalAction(
        val action: B2BUIAction,
    ) : MainScreenAction()

    @JacocoExcludeGenerated
    data class UpdateMemberEmailAddress(
        val emailAddress: String,
    ) : MainScreenAction()

    data object HandleEmailSubmit : MainScreenAction()

    data object SetEmailShouldBeValidated : MainScreenAction()

    @JacocoExcludeGenerated
    data class UpdateMemberPassword(
        val password: String,
    ) : MainScreenAction()

    data object HandlePasswordSubmit : MainScreenAction()

    @JacocoExcludeGenerated
    data class StartOAuth(
        val context: Activity,
        val providerConfig: B2BOAuthProviderConfig,
    ) : MainScreenAction()

    @JacocoExcludeGenerated
    data class StartSSO(
        val context: Activity,
        val connectionId: String,
    ) : MainScreenAction()

    data object StartSSODiscovery : MainScreenAction()
}

@JacocoExcludeGenerated
internal data class MainScreenState(
    val primaryAuthMethods: List<AllowedAuthMethods> = emptyList(),
    val emailState: EmailState = EmailState(),
    val passwordState: PasswordState = PasswordState(),
    val authFlowType: AuthFlowType = AuthFlowType.ORGANIZATION,
    val organizationData: InternalOrganizationData? = null,
    val products: List<StytchB2BProduct> = emptyList(),
    val oauthProviderSettings: List<B2BOAuthProviderConfig> = emptyList(),
)

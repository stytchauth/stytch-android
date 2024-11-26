package com.stytch.sdk.ui.b2b.usecases
import android.app.Activity
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.oauth.OAuth
import com.stytch.sdk.ui.b2b.data.B2BOAuthProviders
import com.stytch.sdk.ui.b2b.data.B2BUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal const val OAUTH_START_IDENTIFIER = 5551

internal class UseOAuthStart(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
) {
    operator fun invoke(
        context: Activity,
        provider: B2BOAuthProviders,
        customScopes: List<String>?,
        providerParams: Map<String, String>,
    ) {
        scope.launch(Dispatchers.IO) {
            val stytchProvider =
                when (provider) {
                    B2BOAuthProviders.GOOGLE -> StytchB2BClient.oauth.google
                    B2BOAuthProviders.MICROSOFT -> StytchB2BClient.oauth.microsoft
                    B2BOAuthProviders.HUBSPOT -> StytchB2BClient.oauth.hubspot
                    B2BOAuthProviders.GITHUB -> StytchB2BClient.oauth.github
                    B2BOAuthProviders.SLACK -> StytchB2BClient.oauth.slack
                }
            state.value.activeOrganization?.organizationId?.let { organizationId ->
                stytchProvider.start(
                    OAuth.Provider.StartParameters(
                        context = context,
                        oAuthRequestIdentifier = OAUTH_START_IDENTIFIER,
                        customScopes = customScopes,
                        providerParams = providerParams,
                        loginRedirectUrl = getRedirectUrl(),
                        signupRedirectUrl = getRedirectUrl(),
                        organizationId = organizationId,
                    ),
                )
            } ?: run {
                stytchProvider.discovery.start(
                    OAuth.ProviderDiscovery.DiscoveryStartParameters(
                        context = context,
                        oAuthRequestIdentifier = OAUTH_START_IDENTIFIER,
                        discoveryRedirectUrl = getRedirectUrl(),
                        customScopes = customScopes,
                        providerParams = providerParams,
                    ),
                )
            }
        }
    }
}

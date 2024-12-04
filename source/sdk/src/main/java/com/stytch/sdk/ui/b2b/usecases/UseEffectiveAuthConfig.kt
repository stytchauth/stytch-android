package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.AuthMethods
import com.stytch.sdk.ui.b2b.data.B2BOAuthProviderConfig
import com.stytch.sdk.ui.b2b.data.B2BOAuthProviders
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProduct
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import kotlinx.coroutines.flow.StateFlow

private val productsToAuthMethods: Map<StytchB2BProduct, AllowedAuthMethods?> =
    mapOf(
        StytchB2BProduct.OAUTH to null,
        StytchB2BProduct.SSO to AllowedAuthMethods.SSO,
        StytchB2BProduct.EMAIL_MAGIC_LINKS to AllowedAuthMethods.MAGIC_LINK,
        StytchB2BProduct.PASSWORDS to AllowedAuthMethods.PASSWORD,
    )

private val oauthProvidersToAuthMethods: Map<B2BOAuthProviders, AllowedAuthMethods> =
    mapOf(
        B2BOAuthProviders.GOOGLE to AllowedAuthMethods.GOOGLE_OAUTH,
        B2BOAuthProviders.MICROSOFT to AllowedAuthMethods.MICROSOFT_OAUTH,
        B2BOAuthProviders.HUBSPOT to AllowedAuthMethods.HUBSPOT_OAUTH,
        B2BOAuthProviders.SLACK to AllowedAuthMethods.SLACK_OAUTH,
        B2BOAuthProviders.GITHUB to AllowedAuthMethods.GITHUB_OAUTH,
    )

private fun <T> Map<T, AllowedAuthMethods?>.getKeyByValue(value: AllowedAuthMethods?): T? =
    filter { it.value == value }.keys.firstOrNull()

private fun flattenConfigToAuthMethods(
    products: List<StytchB2BProduct>,
    oauthProviders: List<B2BOAuthProviders>,
): List<AllowedAuthMethods> {
    val output = mutableListOf<AllowedAuthMethods>()
    products.forEach { product ->
        if (product == StytchB2BProduct.OAUTH) {
            oauthProviders.forEach { provider ->
                oauthProvidersToAuthMethods[provider]?.let {
                    output.add(it)
                }
            }
        } else {
            productsToAuthMethods[product]?.let {
                output.add(it)
            }
        }
    }
    return output
}

internal class UseEffectiveAuthConfig(
    private val state: StateFlow<B2BUIState>,
    private val productConfig: StytchB2BProductConfig,
) {
    private val restrictedAuthMethods: Set<AllowedAuthMethods>
        get() {
            if (state.value.primaryAuthMethods.isNotEmpty()) {
                return state.value.primaryAuthMethods.toSet()
            }
            if (state.value.activeOrganization?.authMethods == AuthMethods.RESTRICTED) {
                return state.value.activeOrganization
                    ?.allowedAuthMethods
                    ?.toSet()
                    ?: emptySet()
            }
            return emptySet()
        }

    private val hasPrimaryAuthMethods: Boolean
        get() = state.value.primaryAuthMethods.isNotEmpty()

    private val oauthProviderConfig: List<B2BOAuthProviderConfig>
        get() = productConfig.oauthOptions.providers

    private val flattenedConfiguredAuthMethods: List<AllowedAuthMethods>
        get() = flattenConfigToAuthMethods(productConfig.products, oauthProviderConfig.map { it.type })

    private val authMethodsToShow: List<AllowedAuthMethods>
        get() {
            // If there are no restrictions, use the auth methods from the UI config as-is
            if (restrictedAuthMethods.isNotEmpty()) {
                return flattenedConfiguredAuthMethods
            }

            // If there are restrictions, filter the auth methods from the UI config
            val restrictedAuthMethodsInUiConfig =
                flattenedConfiguredAuthMethods.filter {
                    restrictedAuthMethods.isEmpty() || restrictedAuthMethods.contains(it)
                }

            // Use the filtered methods unless there are none _and_ the restrictions
            // come from `primary_required` (not just org-level restrictions)
            if (restrictedAuthMethodsInUiConfig.isNotEmpty() || !hasPrimaryAuthMethods) {
                return restrictedAuthMethodsInUiConfig
            }

            // If the org restricts allowed auth methods, show all of the methods
            // included in `primary_required`
            if (state.value.activeOrganization?.authMethods == AuthMethods.RESTRICTED) {
                return flattenConfigToAuthMethods(
                    productsToAuthMethods.keys.toList(),
                    oauthProvidersToAuthMethods.keys.toList(),
                ).filter {
                    restrictedAuthMethods.isEmpty() || restrictedAuthMethods.contains(it)
                }
            }

            // If the org doesn't have any auth method restrictions, default to email
            // magic links
            return listOf(AllowedAuthMethods.MAGIC_LINK)
        }

    val products: Set<StytchB2BProduct>
        get() =
            authMethodsToShow
                .mapNotNull { authMethod ->
                    if (authMethod in oauthProvidersToAuthMethods.values) {
                        StytchB2BProduct.OAUTH
                    } else {
                        productsToAuthMethods.getKeyByValue(authMethod)
                    }
                }.toSet()

    val oauthProviderSettings: Set<B2BOAuthProviderConfig>
        get() =
            authMethodsToShow
                .mapNotNull { authMethod ->
                    oauthProvidersToAuthMethods.getKeyByValue(authMethod)?.let { provider ->
                        oauthProviderConfig.find { config ->
                            config.type == provider
                        }
                    }
                }.toSet()
}

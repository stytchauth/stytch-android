package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.b2b.network.models.InternalOrganizationData
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.ui.shared.data.SessionOptions
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
public data class StytchB2BProductConfig
    @JvmOverloads
    constructor(
        val products: List<StytchB2BProduct> =
            listOf(
                StytchB2BProduct.EMAIL_MAGIC_LINKS,
                StytchB2BProduct.PASSWORDS,
                StytchB2BProduct.OAUTH,
            ),
        val authFlowType: AuthFlowType = AuthFlowType.DISCOVERY,
        val organizationSlug: String? = null,
        val sessionOptions: SessionOptions = SessionOptions(),
        val emailMagicLinksOptions: B2BEmailMagicLinkOptions = B2BEmailMagicLinkOptions(),
        val passwordOptions: B2BPasswordOptions = B2BPasswordOptions(),
        val oauthOptions: B2BOAuthOptions = B2BOAuthOptions(),
        val directLoginForSingleMembership: DirectLoginForSingleMembershipOptions? = null,
        val allowCreateOrganization: Boolean = true,
        val directCreateOrganizationForNoMembership: Boolean = false,
        val mfaProductOrder: List<MfaMethod> = emptyList(),
        val mfaProductInclude: List<MfaMethod> =
            listOf(
                MfaMethod.SMS,
                MfaMethod.TOTP,
            ),
        val locale: Locale? = Locale.EN,
    ) : Parcelable

/*
  Here we are generating the ordering and which components will be displayed.
  We use the Component enum this because there is some complex logic here, and
  we want to be able to unit test.

  Rules we want to follow when generating the components:
  1. If we're displaying both an email-based method (email magic links and/or email OTPs) and passwords,
  we need to display them together as a single wrapped component. The index of this wrapped component is
  equivalent to the first index of either email-based method or passwords in the config products list.
  2. If we have both buttons and input, we want to display a divider between the last 2 elements.
  3. Some components have both a discovery and a non-discovery version. We want to display the correct version
  based on the flow type (found in state).
  4. We want to display the components in the order that they are listed in the config.

  This function should be considered the source of truth for which components to render
  and what order to render them in as of 6/21/23.
*/
internal fun List<StytchB2BProduct>.generateProductComponentsOrdering(
    authFlowType: AuthFlowType,
    organization: InternalOrganizationData?,
): List<ProductComponent> {
    val containsEmail = contains(StytchB2BProduct.EMAIL_MAGIC_LINKS) || contains(StytchB2BProduct.EMAIL_OTP)
    val displayEmlAndPasswordsTogether = containsEmail && contains(StytchB2BProduct.PASSWORDS)
    val components =
        mapNotNull { product ->
            when (product) {
                StytchB2BProduct.EMAIL_MAGIC_LINKS,
                StytchB2BProduct.EMAIL_OTP,
                -> {
                    if (displayEmlAndPasswordsTogether) {
                        ProductComponent.PasswordEMLCombined
                    } else {
                        if (authFlowType == AuthFlowType.ORGANIZATION) {
                            ProductComponent.EmailForm
                        } else {
                            ProductComponent.EmailDiscoveryForm
                        }
                    }
                }

                StytchB2BProduct.OAUTH -> {
                    ProductComponent.OAuthButtons
                }

                StytchB2BProduct.SSO -> {
                    // We only need to render a component if we have a valid SSO connection
                    organization?.let { org ->
                        val isSSOValid = !org.ssoActiveConnections.isNullOrEmpty()
                        if (authFlowType == AuthFlowType.ORGANIZATION && isSSOValid) {
                            ProductComponent.SSOButtons
                        } else {
                            null
                        }
                    }
                }

                StytchB2BProduct.PASSWORDS -> {
                    if (displayEmlAndPasswordsTogether) {
                        ProductComponent.PasswordEMLCombined
                    } else {
                        ProductComponent.PasswordsEmailForm
                    }
                }
            }
        }
            // ensure we have only one of each component
            .toSet()
            // but we want a list we can modify
            .toMutableList()

    val hasButtons = components.any { it in setOf(ProductComponent.OAuthButtons, ProductComponent.SSOButtons) }
    val hasInput = any { it in setOf(StytchB2BProduct.EMAIL_MAGIC_LINKS, StytchB2BProduct.PASSWORDS) }
    val showDivider = hasButtons && hasInput
    if (components.isNotEmpty() && showDivider) {
        components.add(components.size - 1, ProductComponent.Divider)
    }
    return components.toList()
}

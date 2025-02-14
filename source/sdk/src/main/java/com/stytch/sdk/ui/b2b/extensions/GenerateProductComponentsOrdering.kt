package com.stytch.sdk.ui.b2b.extensions

import com.stytch.sdk.b2b.network.models.InternalOrganizationData
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.ProductComponent
import com.stytch.sdk.ui.b2b.data.StytchB2BProduct

/*
    Here we are generating the ordering and which components will be displayed.
    We use the ProductComponent enum because there is some complex logic here, and
    we want to be able to unit test.

    Rules we want to follow when generating the components:
    1. If we're displaying both email magic links or email otp and passwords, we need to display them together
    as a single wrapped component. The index of this wrapped component is equivalent to the first index of
    either email magic links, email otp, or passwords in the config products list.
    2. If both buttons and input are present, display a divider above and below the input component.
    Do not add a divider above if the input is the first component or below if it is the last.
    3. We want to display the components in the order that they are listed in the config.
*/
internal fun List<StytchB2BProduct>.generateProductComponentsOrdering(
    authFlowType: AuthFlowType,
    organization: InternalOrganizationData?,
): List<ProductComponent> {
    val finalComponentOrdering = mutableListOf<ProductComponent>()
    val containsEmail = contains(StytchB2BProduct.EMAIL_MAGIC_LINKS) || contains(StytchB2BProduct.EMAIL_OTP)
    val displayEmlAndPasswordsTogether = containsEmail && contains(StytchB2BProduct.PASSWORDS)
    // map StytchB2BProduct to appropriate ProductComponent
    val productComponentList =
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
        }.toSet()
    // add dividers as necessary
    productComponentList
        .forEachIndexed { index, component ->
            val componentAndDividers = mutableListOf(component)
            if (component.isInputComponent()) {
                if (index > 0) {
                    // add a divider before the component
                    componentAndDividers.add(0, ProductComponent.Divider)
                }
                if (index < productComponentList.size - 1) {
                    // add a divider after the component
                    componentAndDividers.add(ProductComponent.Divider)
                }
            }
            finalComponentOrdering.addAll(componentAndDividers)
        }
    return finalComponentOrdering
}

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
    2. If both buttons and inputs are present, display a divider above and below the input component.
       Do not add a divider above if the input is the first component or below if it is the last.
    3. Overall, we want to display the components in the order that they are listed in the config, aside from the
       operations described above
*/
internal fun List<StytchB2BProduct>.generateProductComponentsOrdering(
    authFlowType: AuthFlowType,
    organization: InternalOrganizationData?,
): List<ProductComponent> = mapProductsToComponents(authFlowType, organization).addDividers()

private fun List<StytchB2BProduct>.mapProductsToComponents(
    authFlowType: AuthFlowType,
    organization: InternalOrganizationData?,
): MutableList<ProductComponent> {
    val containsEmail = contains(StytchB2BProduct.EMAIL_MAGIC_LINKS) || contains(StytchB2BProduct.EMAIL_OTP)
    val displayEmlAndPasswordsTogether = containsEmail && contains(StytchB2BProduct.PASSWORDS)
    return mapNotNull { product ->
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
                val organizationHasActiveSSOConnections =
                    authFlowType == AuthFlowType.ORGANIZATION &&
                        (organization?.ssoActiveConnections?.isNotEmpty() ?: false)
                if (authFlowType == AuthFlowType.DISCOVERY || organizationHasActiveSSOConnections) {
                    ProductComponent.SSOButtons
                } else {
                    null
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
        // ensure we only have one of each product component type
        .toSet()
        // make it a mutable list, for the next round of processing
        .toMutableList()
}

private fun MutableList<ProductComponent>.addDividers(): MutableList<ProductComponent> {
    val output: MutableList<ProductComponent> = mutableListOf()
    forEachIndexed { index, component ->
        val componentAndDividers = mutableListOf(component)
        if (component.isInputComponent()) {
            if (index > 0) {
                // add a divider before the component
                componentAndDividers.add(0, ProductComponent.Divider)
            }
            if (index < this.size - 1) {
                // add a divider after the component
                componentAndDividers.add(ProductComponent.Divider)
            }
        }
        output.addAll(componentAndDividers)
    }
    return output
}

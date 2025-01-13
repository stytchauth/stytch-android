package com.stytch.sdk.ui.b2b.extensions

import com.stytch.sdk.b2b.network.models.EmailJitProvisioning
import com.stytch.sdk.b2b.network.models.InternalOrganizationData

internal fun InternalOrganizationData.emailEligibleForJITProvisioning(emailAddress: String): Boolean {
    val domain = emailAddress.split("@").last()
    val jitProvisioningNotAllowed = emailJitProvisioning == EmailJitProvisioning.NOT_ALLOWED
    val jitProvisioningRestricted = emailJitProvisioning == EmailJitProvisioning.RESTRICTED
    val domainInAllowedDomains = (emailAllowedDomains ?: emptyList()).contains(domain)
    return (jitProvisioningNotAllowed || (jitProvisioningRestricted && domainInAllowedDomains))
}

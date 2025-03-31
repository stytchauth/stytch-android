package com.stytch.sdk.ui.b2b.extensions

import com.stytch.sdk.b2b.network.models.InternalOrganizationData
import com.stytch.sdk.b2b.network.models.OrganizationData

internal fun OrganizationData.toInternalOrganizationData(): InternalOrganizationData =
    InternalOrganizationData(
        organizationId = organizationId,
        organizationName = organizationName,
        organizationLogoUrl = organizationLogoUrl,
        organizationSlug = organizationSlug,
        ssoActiveConnections = ssoActiveConnections,
        ssoDefaultConnectionId = ssoDefaultConnectionId,
        emailAllowedDomains = emailAllowedDomains,
        emailJitProvisioning = emailJitProvisioning,
        emailInvites = emailInvites,
        authMethods = authMethods,
        allowedAuthMethods = allowedAuthMethods,
    )

package com.stytch.sdk.ui.b2b.extensions

import com.stytch.sdk.b2b.network.models.InternalOrganizationData
import com.stytch.sdk.b2b.network.models.OrganizationData

internal fun OrganizationData.toInternalOrganizationData(): InternalOrganizationData =
    InternalOrganizationData(
        organizationId,
        organizationName,
        organizationLogoUrl,
        ssoActiveConnections,
        ssoDefaultConnectionId,
        emailAllowedDomains,
        emailJitProvisioning,
        emailInvites,
        authMethods,
        allowedAuthMethods,
    )

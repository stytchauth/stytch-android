package com.stytch.sdk.b2b.organization

import com.stytch.sdk.b2b.network.models.OrganizationData
import java.util.Date

public sealed interface StytchOrganization {
    public data object Unavailable : StytchOrganization

    public data class Available(
        public val lastValidatedAt: Date,
        public val organizationData: OrganizationData,
    ) : StytchOrganization
}

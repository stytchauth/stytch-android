package com.stytch.sdk.ui.b2b.extensions

import com.stytch.sdk.b2b.network.models.DiscoveredOrganization
import com.stytch.sdk.ui.b2b.data.DirectLoginForSingleMembershipOptions

internal val invitedTypes = listOf("pending_member", "invited_member")
internal val jitEligible = listOf("eligible_to_join_by_email_domain", "eligible_to_join_by_oauth_tenant")

internal fun List<DiscoveredOrganization>.shouldAllowDirectLoginToOrganization(
    config: DirectLoginForSingleMembershipOptions? = null,
): DiscoveredOrganization? {
    // If direct login is not enabled, return false
    if (config == null) return null
    // Count active memberships
    val activeOrganizations = filter { org -> org.membership.type == "active_member" }
    // Check for pending invites or JIT provisioning, depending on config
    val hasBlockingConditions =
        any { org ->
            (invitedTypes.contains(org.membership.type) && !config.ignoreInvites) ||
                (jitEligible.contains(org.membership.type) && !config.ignoreJitProvisioning)
        }
    // Allow direct login if there is exactly one active membership and no blocking conditions
    if (activeOrganizations.size == 1 && !hasBlockingConditions) return activeOrganizations[0]
    return null
}

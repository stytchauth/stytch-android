package com.stytch.sdk.b2b.organization

import com.stytch.sdk.b2b.OrganizationResponse

/**
 * The Organization interface provides methods for retrieving the current authenticated user's organization.
 */
public interface Organization {
    /**
     * Wraps Stytch’s organization/me endpoint.
     * @return [OrganizationResponse]
     */
    public suspend fun get(): OrganizationResponse

    /**
     * Wraps Stytch’s organization/me endpoint.
     * @param callback a callback that receives an [OrganizationResponse]
     */
    public fun get(callback: (OrganizationResponse) -> Unit)
}

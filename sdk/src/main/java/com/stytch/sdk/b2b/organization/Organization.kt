package com.stytch.sdk.b2b.organization

import com.stytch.sdk.b2b.OrganizationResponse

/**
 * Organization interface that encompasses retrieving an Organization
 */
public interface Organization {
    /**
     * Wraps Stytch’s organization/me endpoint.
     * @return OrganizationResponse response from backend
     */
    public suspend fun get(): OrganizationResponse

    /**
     * Wraps Stytch’s organization/me endpoint.
     * @param callback calls callback with OrganizationResponse response from backend
     */
    public fun get(callback: (OrganizationResponse) -> Unit)
}

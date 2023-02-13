package com.stytch.sdk.b2b.organizations

import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.OrganizationResponse

/**
 * Organizations interface that encompasses retrieving an Organization
 */
public interface Organizations {
    /**
     * Wraps Stytch’s organization/:organizationId endpoint.
     * @return OrganizationResponse response from backend
     */
    public suspend fun getOrganization(): OrganizationResponse

    /**
     * Wraps Stytch’s organization/:organizationId endpoint.
     * @param callback calls callback with OrganizationResponse response from backend
     */
    public fun getOrganization(callback: (OrganizationResponse) -> Unit)

    /**
     * Wraps Stytch’s organization/:organizationId/members/me endpoint.
     * @return MemberResponse response from backend
     */
    public suspend fun getMember(): MemberResponse

    /**
     * Wraps Stytch’s organization/:organizationId/members/me endpoint.
     * @param parameters required to retrieve the member
     * @param callback calls callback with MemberResponse response from backend
     */
    public fun getMember(callback: (MemberResponse) -> Unit)
}

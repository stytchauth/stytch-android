package com.stytch.sdk.b2b.organizations

import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.OrganizationResponse

/**
 * Organizations interface that encompasses retrieving an Organization
 */
public interface Organizations {
    /**
     * Data class used for wrapping parameters used with getting an Organization
     * @param organizationId is the member's organization ID
     */
    public data class GetParameters(
        val organizationId: String
    )

    /**
     * Wraps Stytch’s organization/:organizationId endpoint.
     * @param parameters required to retrieve the organization
     * @return OrganizationResponse response from backend
     */
    public suspend fun getOrganization(parameters: GetParameters): OrganizationResponse

    /**
     * Wraps Stytch’s organization/:organizationId endpoint.
     * @param parameters required to retrieve the organization
     * @param callback calls callback with OrganizationResponse response from backend
     */
    public fun getOrganization(parameters: GetParameters, callback: (OrganizationResponse) -> Unit)

    /**
     * Wraps Stytch’s organization/:organizationId/members/me endpoint.
     * @param parameters required to retrieve the member
     * @return MemberResponse response from backend
     */
    public suspend fun getMember(parameters: GetParameters): MemberResponse

    /**
     * Wraps Stytch’s organization/:organizationId/members/me endpoint.
     * @param parameters required to retrieve the member
     * @param callback calls callback with MemberResponse response from backend
     */
    public fun getMember(parameters: GetParameters, callback: (MemberResponse) -> Unit)
}

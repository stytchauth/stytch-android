package com.stytch.sdk.b2b.organizations

import com.stytch.sdk.b2b.OrganizationResponse

/**
 * Organizations interface that encompasses retrieving an Organization
 */
public interface Organizations {
    /**
     * Data class used for wrapping parameters used with getting an Organization
     * @param organizationId is the member's organization ID
     */
    public data class GetOrganizationParameters(
        val organizationId: String
    )

    /**
     * Wraps Stytch’s organization/:organizationId endpoint.
     * @param parameters required to receive magic link
     * @return OrganizationResponse response from backend
     */
    public suspend fun getOrganization(parameters: GetOrganizationParameters): OrganizationResponse

    /**
     * Wraps Stytch’s organization/:organizationId endpoint.
     * @param parameters required to receive magic link
     * @param callback calls callback with OrganizationResponse response from backend
     */
    public fun getOrganization(parameters: GetOrganizationParameters, callback: (OrganizationResponse) -> Unit)
}

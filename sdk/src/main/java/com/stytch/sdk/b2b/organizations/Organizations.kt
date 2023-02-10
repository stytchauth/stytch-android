package com.stytch.sdk.b2b.organizations

import com.stytch.sdk.b2b.OrganizationResponse

public interface Organizations {
    public data class GetOrganizationParameters(
        val organizationId: String
    )

    public suspend fun getOrganization(parameters: GetOrganizationParameters): OrganizationResponse

    public fun getOrganization(parameters: GetOrganizationParameters, callback: (OrganizationResponse) -> Unit)
}

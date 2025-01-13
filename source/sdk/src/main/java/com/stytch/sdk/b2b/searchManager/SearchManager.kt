package com.stytch.sdk.b2b.searchManager

import com.stytch.sdk.b2b.B2BSearchMemberResponse
import com.stytch.sdk.b2b.B2BSearchOrganizationResponse
import java.util.concurrent.CompletableFuture

/**
 * The SearchManager interface provides methods for searching for organizations and members
 */
public interface SearchManager {
    /**
     * A data class wrapping the parameters used for a Search Organizations call
     * @property organizationSlug the slug of the organization to search for
     */
    public data class SearchOrganizationParameters(
        val organizationSlug: String,
    )

    /**
     * Search for an organization
     * @param parameters the parameters needed to make the search request
     * @return [B2BSearchOrganizationResponse]
     */
    public suspend fun searchOrganization(parameters: SearchOrganizationParameters): B2BSearchOrganizationResponse

    /**
     * Search for an organization
     * @param parameters the parameters needed to make the search request
     * @param callback a callback that receives a [B2BSearchOrganizationResponse]
     */
    public fun searchOrganization(
        parameters: SearchOrganizationParameters,
        callback: (B2BSearchOrganizationResponse) -> Unit,
    )

    /**
     * Search for an organization
     * @param parameters the parameters needed to make the search request
     * @return [B2BSearchOrganizationResponse]
     */
    public fun searchOrganizationCompletable(
        parameters: SearchOrganizationParameters,
    ): CompletableFuture<B2BSearchOrganizationResponse>

    /**
     * A data class wrapping the parameters used for a Search Members call
     * @property emailAddress the email address of the member to search for
     * @property organizationId the id of the organization the member belongs to
     */
    public data class SearchMemberParameters(
        val emailAddress: String,
        val organizationId: String,
    )

    /**
     * Search for an organization member
     * @param parameters the parameters needed to make the search request
     * @return [B2BSearchMemberResponse]
     */
    public suspend fun searchMember(parameters: SearchMemberParameters): B2BSearchMemberResponse

    /**
     * Search for an organization member
     * @param parameters the parameters needed to make the search request
     * @param callback a callback that receives a [B2BSearchMemberResponse]
     */
    public fun searchMember(
        parameters: SearchMemberParameters,
        callback: (B2BSearchMemberResponse) -> Unit,
    )

    /**
     * Search for an organization member
     * @param parameters the parameters needed to make the search request
     * @return [B2BSearchMemberResponse]
     */
    public fun searchMemberCompletable(parameters: SearchMemberParameters): CompletableFuture<B2BSearchMemberResponse>
}

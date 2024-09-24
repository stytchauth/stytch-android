package com.stytch.sdk.b2b.organization

import com.squareup.moshi.Json
import com.stytch.sdk.b2b.CreateMemberResponse
import com.stytch.sdk.b2b.DeleteMemberResponse
import com.stytch.sdk.b2b.DeleteOrganizationMemberAuthenticationFactorResponse
import com.stytch.sdk.b2b.DeleteOrganizationResponse
import com.stytch.sdk.b2b.MemberSearchResponse
import com.stytch.sdk.b2b.OrganizationResponse
import com.stytch.sdk.b2b.ReactivateMemberResponse
import com.stytch.sdk.b2b.UpdateOrganizationMemberResponse
import com.stytch.sdk.b2b.UpdateOrganizationResponse
import com.stytch.sdk.b2b.member.MemberAuthenticationFactor
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.AuthMethods
import com.stytch.sdk.b2b.network.models.EmailInvites
import com.stytch.sdk.b2b.network.models.EmailJitProvisioning
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.b2b.network.models.MfaMethods
import com.stytch.sdk.b2b.network.models.MfaPolicy
import com.stytch.sdk.b2b.network.models.OrganizationData
import com.stytch.sdk.b2b.network.models.SearchOperator
import com.stytch.sdk.b2b.network.models.SsoJitProvisioning
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.CompletableFuture

/**
 * The Organization interface provides methods for retrieving, updating, and deleting the current authenticated user's
 * organization and creating, updating, deleting, reactivating, and searching an organizations members
 */
public interface Organization {
    /**
     * Exposes a flow of organization data
     */
    public val onChange: StateFlow<OrganizationData?>

    /**
     * Assign a callback that will be called when the organization data changes
     */

    public fun onChange(callback: (OrganizationData?) -> Unit)

    /**
     * Wraps Stytch’s organization/me endpoint.
     * @return [OrganizationResponse]
     */
    public suspend fun get(): OrganizationResponse

    /**
     * Wraps Stytch’s organization/me endpoint.
     * @return [OrganizationResponse]
     */
    public fun getCompletable(): CompletableFuture<OrganizationResponse>

    /**
     * Wraps Stytch’s organization/me endpoint.
     * @param callback a callback that receives an [OrganizationResponse]
     */
    public fun get(callback: (OrganizationResponse) -> Unit)

    /**
     * Get member from memory without network call
     * @return locally stored [Organization]
     */
    public fun getSync(): OrganizationData?

    /**
     * Data class used for wrapping parameters used for making an Organization update call
     * @property organizationName The name of the organization
     * @property organizationSlug The unique URL slug of the Organization. A minimum of two characters is required. The
     * slug only accepts alphanumeric characters and the following reserved characters: - . _ ~.
     * @property organizationLogoUrl The image URL of the Organization logo.
     * @property ssoDefaultConnectionId The default connection used for SSO when there are multiple active connections.
     * @property ssoJitProvisioning The authentication setting that controls the JIT provisioning of Members when
     * authenticating via SSO.
     * @property ssoJitProvisioningAllowedConnections An array of connection_ids that reference SAML Connection objects.
     * Only these connections will be allowed to JIT provision Members via SSO when sso_jit_provisioning is set to
     * RESTRICTED.
     * @property emailAllowedDomains An array of email domains that allow invites or JIT provisioning for new Members.
     * This list is enforced when either email_invites or email_jit_provisioning is set to RESTRICTED. Common domains
     * such as gmail.com are not allowed.
     * @property emailJitProvisioning The authentication setting that controls how a new Member can be provisioned by
     * authenticating via Email Magic Link.
     * @property emailInvites The authentication setting that controls how a new Member can be invited to an
     * organization by email.
     * @property authMethods The setting that controls which authentication methods can be used by Members of an
     * Organization.
     * @property allowedAuthMethods An array of allowed authentication methods.
     * @property mfaMethods The setting that controls which mfa methods can be used by Members of an Organization.
     * @property allowedMfaMethods An array of allowed MFA methods.
     * @property mfaPolicy The setting that controls the MFA policy for all Members in the Organization.
     * @property rbacEmailImplicitRoleAssignments An array of implicit role assignments granted to members in this
     * organization whose emails match the domain.
     */
    public data class UpdateOrganizationParameters
        @JvmOverloads
        constructor(
            val organizationName: String? = null,
            val organizationSlug: String? = null,
            val organizationLogoUrl: String? = null,
            val ssoDefaultConnectionId: String? = null,
            val ssoJitProvisioning: SsoJitProvisioning? = null,
            val ssoJitProvisioningAllowedConnections: List<String>? = null,
            val emailAllowedDomains: List<String>? = null,
            val emailJitProvisioning: EmailJitProvisioning? = null,
            val emailInvites: EmailInvites? = null,
            val authMethods: AuthMethods? = null,
            val allowedAuthMethods: List<AllowedAuthMethods>? = null,
            val mfaMethods: MfaMethods? = null,
            val allowedMfaMethods: List<MfaMethod>? = null,
            val mfaPolicy: MfaPolicy? = null,
            val rbacEmailImplicitRoleAssignments: List<String>? = null,
        )

    /**
     * Updates the Organization of the logged-in member.
     * The member must have permission to call this endpoint via the project's RBAC policy & their role assignments.
     * An Organization must always have at least one auth setting set to either RESTRICTED or ALL_ALLOWED in order to
     * provision new Members.
     * @param parameters the parameters required for updating an organization
     * @return [UpdateOrganizationResponse]
     */
    public suspend fun update(parameters: UpdateOrganizationParameters): UpdateOrganizationResponse

    /**
     * Updates the Organization of the logged-in member.
     * The member must have permission to call this endpoint via the project's RBAC policy & their role assignments.
     * An Organization must always have at least one auth setting set to either RESTRICTED or ALL_ALLOWED in order to
     * provision new Members.
     * @param parameters the parameters required for updating an organization
     * @param callback a callback that receives an [UpdateOrganizationResponse]
     */
    public fun update(
        parameters: UpdateOrganizationParameters,
        callback: (UpdateOrganizationResponse) -> Unit,
    )

    /**
     * Updates the Organization of the logged-in member.
     * The member must have permission to call this endpoint via the project's RBAC policy & their role assignments.
     * An Organization must always have at least one auth setting set to either RESTRICTED or ALL_ALLOWED in order to
     * provision new Members.
     * @param parameters the parameters required for updating an organization
     * @return [UpdateOrganizationResponse]
     */
    public fun updateCompletable(
        parameters: UpdateOrganizationParameters,
    ): CompletableFuture<UpdateOrganizationResponse>

    /**
     * Deletes the Organization of the logged-in member. All Members of the Organization will also be deleted.
     * The member must have permission to call this endpoint via the project's RBAC policy & their role assignments.
     * Note: This endpoint will log out the current member, as they will also be deleted
     * @return [DeleteOrganizationResponse]
     */
    public suspend fun delete(): DeleteOrganizationResponse

    /**
     * Deletes the Organization of the logged-in member. All Members of the Organization will also be deleted.
     * The member must have permission to call this endpoint via the project's RBAC policy & their role assignments.
     * Note: This endpoint will log out the current member, as they will also be deleted
     * @param callback a callback that receives a [DeleteOrganizationResponse]
     */
    public fun delete(callback: (DeleteOrganizationResponse) -> Unit)

    /**
     * Deletes the Organization of the logged-in member. All Members of the Organization will also be deleted.
     * The member must have permission to call this endpoint via the project's RBAC policy & their role assignments.
     * Note: This endpoint will log out the current member, as they will also be deleted
     * @return [DeleteOrganizationResponse]
     */
    public fun deleteCompletable(): CompletableFuture<DeleteOrganizationResponse>

    /**
     * Public variable that exposes an instance of Organization.Members
     */
    public val members: OrganizationMembers

    /**
     * An interface that provides methods for performing create, update, delete, reactivate, and search operations on an
     * Organization's members
     */
    public interface OrganizationMembers {
        /**
         * Deletes a Member.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param memberId The ID of the member to be deleted
         * @return [DeleteMemberResponse]
         */
        public suspend fun delete(memberId: String): DeleteMemberResponse

        /**
         * Deletes a Member.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param memberId The ID of the member to be deleted
         * @param callback a callback that receives a [DeleteMemberResponse]
         */
        public fun delete(
            memberId: String,
            callback: (DeleteMemberResponse) -> Unit,
        )

        /**
         * Deletes a Member.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param memberId The ID of the member to be deleted
         * @return [DeleteMemberResponse]
         */
        public fun deleteCompletable(memberId: String): CompletableFuture<DeleteMemberResponse>

        /**
         * Reactivates a deleted Member's status and its associated email status (if applicable) to active.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param memberId The ID of the member to be reactivated
         * @return [ReactivateMemberResponse]
         */
        public suspend fun reactivate(memberId: String): ReactivateMemberResponse

        /**
         * Reactivates a deleted Member's status and its associated email status (if applicable) to active.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param memberId The ID of the member to be reactivated
         * @param callback a callback that receives a [ReactivateMemberResponse]
         */
        public fun reactivate(
            memberId: String,
            callback: (ReactivateMemberResponse) -> Unit,
        )

        /**
         * Reactivates a deleted Member's status and its associated email status (if applicable) to active.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param memberId The ID of the member to be reactivated
         * @return [ReactivateMemberResponse]
         */
        public fun reactivateCompletable(memberId: String): CompletableFuture<ReactivateMemberResponse>

        /**
         * Deletes a [MemberAuthenticationFactor] from the currently authenticated member
         * @param authenticationFactor the authentication factor to delete
         * @return [DeleteOrganizationMemberAuthenticationFactorResponse]
         */
        public suspend fun deleteMemberAuthenticationFactor(
            memberId: String,
            authenticationFactor: MemberAuthenticationFactor,
        ): DeleteOrganizationMemberAuthenticationFactorResponse

        /**
         * Deletes a [MemberAuthenticationFactor] from the currently authenticated member
         * @param authenticationFactor the authentication factor to delete
         * @param callback a callback that receives a [DeleteOrganizationMemberAuthenticationFactorResponse]
         */
        public fun deleteMemberAuthenticationFactor(
            memberId: String,
            authenticationFactor: MemberAuthenticationFactor,
            callback: (DeleteOrganizationMemberAuthenticationFactorResponse) -> Unit,
        )

        /**
         * Deletes a [MemberAuthenticationFactor] from the currently authenticated member
         * @param authenticationFactor the authentication factor to delete
         * @return [DeleteOrganizationMemberAuthenticationFactorResponse]
         */
        public fun deleteMemberAuthenticationFactorCompletable(
            memberId: String,
            authenticationFactor: MemberAuthenticationFactor,
        ): CompletableFuture<DeleteOrganizationMemberAuthenticationFactorResponse>

        /**
         * Data class used for wrapping the parameters necessary for creating a user
         * @property emailAddress  the Member's `email_address`
         * @property name The name of the Member.
         * @property isBreakGlass Identifies the Member as a break glass user - someone who has permissions to
         * authenticate into an Organization by bypassing the Organization's settings. A break glass account is
         * typically used for emergency purposes to gain access outside of normal authentication procedures.
         * @property mfaEnrolled Sets whether the Member is enrolled in MFA. If true, the Member must complete an MFA
         * step whenever they wish to log in to their Organization. If false, the Member only needs to complete an MFA
         * step if the Organization's MFA policy is set to REQUIRED_FOR_ALL.
         * @property mfaPhoneNumber The Member's phone number. A Member may only have one phone number.
         * @property untrustedMetadata An arbitrary JSON object of application-specific data. These fields can be edited
         * directly by the frontend SDK, and should not be used to store critical information.
         * @property createMemberAsPending Flag for whether or not to save a Member as pending or active in Stytch. It
         * defaults to false. If true, new Members will be created with status pending in Stytch's backend. Their status
         * will remain pending and they will continue to receive signup email templates for every Email Magic Link until
         * that Member authenticates and becomes active. If false, new Members will be created with status active.
         * @property roles Roles to explicitly assign to this Member.
         */
        public data class CreateMemberParameters
            @JvmOverloads
            constructor(
                val emailAddress: String,
                val name: String? = null,
                val isBreakGlass: Boolean? = null,
                val mfaEnrolled: Boolean? = null,
                val mfaPhoneNumber: String? = null,
                val untrustedMetadata: Map<String, Any?>? = null,
                val createMemberAsPending: Boolean? = null,
                val roles: List<String>? = null,
            )

        /**
         * Creates a Member.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param parameters the parameters required for creating a user
         * @return [CreateMemberResponse]
         */
        public suspend fun create(parameters: CreateMemberParameters): CreateMemberResponse

        /**
         * Creates a Member.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param parameters the parameters required for creating a user
         * @param callback a callback that receives a [CreateMemberResponse]
         */
        public fun create(
            parameters: CreateMemberParameters,
            callback: (CreateMemberResponse) -> Unit,
        )

        /**
         * Creates a Member.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param parameters the parameters required for creating a user
         * @return [CreateMemberResponse]
         */
        public fun createCompletable(parameters: CreateMemberParameters): CompletableFuture<CreateMemberResponse>

        /**
         * Data class used for wrapping the parameters necessary for creating a user
         * @property memberId Globally unique UUID that identifies a specific Member.
         * @property emailAddress  the Member's `email_address`
         * @property name The name of the Member.
         * @property isBreakGlass Identifies the Member as a break glass user - someone who has permissions to
         * authenticate into an Organization by bypassing the Organization's settings. A break glass account is
         * typically used for emergency purposes to gain access outside of normal authentication procedures.
         * @property mfaEnrolled Sets whether the Member is enrolled in MFA. If true, the Member must complete an MFA
         * step whenever they wish to log in to their Organization. If false, the Member only needs to complete an MFA
         * step if the Organization's MFA policy is set to REQUIRED_FOR_ALL.
         * @property mfaPhoneNumber The Member's phone number. A Member may only have one phone number.
         * @property untrustedMetadata An arbitrary JSON object of application-specific data. These fields can be edited
         * directly by the frontend SDK, and should not be used to store critical information.
         * @property roles Roles to explicitly assign to this Member.
         * @property preserveExistingSessions Whether to preserve existing sessions when explicit Roles that are revoked
         * are also implicitly assigned by SSO connection or SSO group. Defaults to false - that is, existing Member
         * Sessions that contain SSO authentication factors with the affected SSO connection IDs will be revoked.
         * @property defaultMfaMethod Sets the Member's default MFA method. Valid values are 'sms_otp' and 'totp'.
         * This value will determine
         * 1. Which MFA method the Member is prompted to use when logging in
         * 2. Whether An SMS will be sent automatically after completing the first leg of authentication
         */
        public data class UpdateMemberParameters
            @JvmOverloads
            constructor(
                val memberId: String,
                val emailAddress: String? = null,
                val name: String? = null,
                val isBreakGlass: Boolean? = null,
                val mfaEnrolled: Boolean? = null,
                val mfaPhoneNumber: String? = null,
                val untrustedMetadata: Map<String, Any?>? = null,
                val roles: List<String>? = null,
                val preserveExistingSessions: Boolean? = null,
                val defaultMfaMethod: MfaMethod? = null,
            )

        /**
         * Updates a Member.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param parameters the parameters required for updating a user
         * @return [UpdateOrganizationMemberResponse]
         */
        public suspend fun update(parameters: UpdateMemberParameters): UpdateOrganizationMemberResponse

        /**
         * Updates a Member.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param parameters the parameters required for updating a user
         * @param callback a callback that receives a [UpdateOrganizationMemberResponse]
         */
        public fun update(
            parameters: UpdateMemberParameters,
            callback: (UpdateOrganizationMemberResponse) -> Unit,
        )

        /**
         * Updates a Member.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param parameters the parameters required for updating a user
         * @return [UpdateOrganizationMemberResponse]
         */
        public fun updateCompletable(
            parameters: UpdateMemberParameters,
        ): CompletableFuture<UpdateOrganizationMemberResponse>

        /**
         * Data class used for wrapping the parameters necessary to search members
         * @property cursor The cursor field allows you to paginate through your results.
         * Each result array is limited to 1000 results.
         * If your query returns more than 1000 results, you will need to paginate the responses using the cursor.
         * If you receive a response that includes a non-null next_cursor in the results_metadata object, repeat the
         * search call with the next_cursor value set to the cursor field to retrieve the next page of results.
         * Continue to make search calls until the next_cursor in the response is null.
         * @property limit The number of search results to return per page.
         * The default limit is 100. A maximum of 1000 results can be returned by a single search request.
         * If the total size of your result set is greater than one page size, you must paginate the response.
         * See the cursor field.
         * @property query The optional query object contains the operator, i.e. AND or OR, and the operands that will
         * filter your results.
         * Only an operator is required. If you include no operands, no filtering will be applied.
         * If you include no query object, it will return all Members with no filtering applied.
         */
        public data class SearchParameters
            @JvmOverloads
            constructor(
                val cursor: String? = null,
                val limit: Int? = null,
                val query: SearchQuery? = null,
            )

        /**
         * A data class representing conditions for a query
         * @property operator The action to perform on the operands. The accepted value are:
         * `AND` – all the operand values provided must match.
         * `OR` – the operator will return any matches to at least one of the operand values you supply.
         * @property operands An array of operand objects that contains all of the filters and values to apply to your
         * search query.
         */
        public data class SearchQuery(
            val operator: SearchOperator,
            val operands: List<SearchQueryOperand>,
        )

        /**
         * A data class representing a Search Query Operand, which contains all of the filters and values to apply to
         * your search query.
         * @property filterName the field on which to filter
         * @property filterValue the value of the field to filter by
         */
        public sealed class SearchQueryOperand(
            @Json(name = "filter_name")
            public val filterName: String,
            @Json(name = "filter_value")
            public val filterValue: Any,
        ) {
            /**
             * An operand for searching based on member_ids
             */
            public data class MemberIds(
                val value: List<String>,
            ) : SearchQueryOperand(
                    filterName = "member_ids",
                    filterValue = value,
                )

            /**
             * An operand for searching based on member_emails
             */
            public data class MemberEmails(
                val value: List<String>,
            ) : SearchQueryOperand(
                    filterName = "member_emails",
                    filterValue = value,
                )

            /**
             * An operand for searching based on member_email_fuzzy
             */
            public data class MemberEmailFuzzy(
                val value: String,
            ) : SearchQueryOperand(
                    filterName = "member_email_fuzzy",
                    filterValue = value,
                )

            /**
             * An operand for searching based on member_is_breakglass
             */
            public data class MemberIsBreakingGlass(
                val value: Boolean,
            ) : SearchQueryOperand(
                    filterName = "member_is_breakglass",
                    filterValue = value,
                )

            /**
             * An operand for searching based on statuses
             */
            public data class Statuses(
                val value: List<String>,
            ) : SearchQueryOperand(
                    filterName = "statuses",
                    filterValue = value,
                )

            /**
             * An operand for searching based on member_mfa_phone_numbers
             */
            public data class MemberMFAPhoneNumbers(
                val value: List<String>,
            ) : SearchQueryOperand(
                    filterName = "member_mfa_phone_numbers",
                    filterValue = value,
                )

            /**
             * An operand for searching based on member_mfa_phone_number_fuzzy
             */
            public data class MemberMFAPhoneNumberFuzzy(
                val value: String,
            ) : SearchQueryOperand(
                    filterName = "member_mfa_phone_number_fuzzy",
                    filterValue = value,
                )

            /**
             * An operand for searching based on member_password_exists
             */
            public data class MemberPasswordExists(
                val value: Boolean,
            ) : SearchQueryOperand(
                    filterName = "member_password_exists",
                    filterValue = value,
                )

            /**
             * An operand for searching based on member_roles
             */
            public data class MemberRoles(
                val value: List<String>,
            ) : SearchQueryOperand(
                    filterName = "member_roles",
                    filterValue = value,
                )

            /**
             * An operand for searching based on custom filters
             */
            public data class Custom(
                val name: String,
                val value: Any,
            ) : SearchQueryOperand(
                    filterName = name,
                    filterValue = value,
                )
        }

        /**
         * Search for Members from the caller's organization. Submitting an empty query returns all non-deleted Members.
         * All fuzzy search filters require a minimum of three characters.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param parameters the parameters for searching
         * @return [MemberSearchResponse]
         */
        public suspend fun search(parameters: SearchParameters): MemberSearchResponse

        /**
         * Search for Members from the caller's organization. Submitting an empty query returns all non-deleted Members.
         * All fuzzy search filters require a minimum of three characters.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param parameters the parameters for searching
         * @param callback a callback that receives a [MemberSearchResponse]
         */
        public fun search(
            parameters: SearchParameters,
            callback: (MemberSearchResponse) -> Unit,
        )

        /**
         * Search for Members from the caller's organization. Submitting an empty query returns all non-deleted Members.
         * All fuzzy search filters require a minimum of three characters.
         * The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
         * @param parameters the parameters for searching
         * @return [MemberSearchResponse]
         */
        public fun searchCompletable(parameters: SearchParameters): CompletableFuture<MemberSearchResponse>
    }
}

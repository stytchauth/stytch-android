package com.stytch.sdk.b2b.organization

import com.stytch.sdk.b2b.DeleteMemberResponse
import com.stytch.sdk.b2b.DeleteOrganizationMemberAuthenticationFactorResponse
import com.stytch.sdk.b2b.DeleteOrganizationResponse
import com.stytch.sdk.b2b.OrganizationResponse
import com.stytch.sdk.b2b.ReactivateMemberResponse
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
import com.stytch.sdk.b2b.network.models.SsoJitProvisioning

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
    public data class UpdateOrganizationParameters(
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
     * Public variable that exposes an instance of Organization.Members
     */
    public val members: OrganizationMembers

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
    }
}

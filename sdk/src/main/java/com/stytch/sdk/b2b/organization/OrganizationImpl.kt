package com.stytch.sdk.b2b.organization

import com.stytch.sdk.b2b.DeleteMemberResponse
import com.stytch.sdk.b2b.DeleteOrganizationResponse
import com.stytch.sdk.b2b.OrganizationResponse
import com.stytch.sdk.b2b.UpdateOrganizationResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.OrganizationData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OrganizationImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.Organization,
) : Organization {
    override suspend fun get(): OrganizationResponse =
        withContext(dispatchers.io) {
            api.getOrganization().apply {
                if (this is StytchResult.Success) {
                    sessionStorage.organization = this.value.organization
                }
            }
        }

    override fun get(callback: (OrganizationResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = get()
            callback(result)
        }
    }

    override fun getSync(): OrganizationData? = sessionStorage.organization

    override suspend fun update(parameters: Organization.UpdateOrganizationParameters): UpdateOrganizationResponse =
        withContext(dispatchers.io) {
            api.updateOrganization(
                organizationName = parameters.organizationName,
                organizationSlug = parameters.organizationSlug,
                organizationLogoUrl = parameters.organizationLogoUrl,
                ssoDefaultConnectionId = parameters.ssoDefaultConnectionId,
                ssoJitProvisioning = parameters.ssoJitProvisioning,
                ssoJitProvisioningAllowedConnections = parameters.ssoJitProvisioningAllowedConnections,
                emailAllowedDomains = parameters.emailAllowedDomains,
                emailJitProvisioning = parameters.emailJitProvisioning,
                emailInvites = parameters.emailInvites,
                authMethods = parameters.authMethods,
                allowedAuthMethods = parameters.allowedAuthMethods,
                mfaMethods = parameters.mfaMethods,
                allowedMfaMethods = parameters.allowedMfaMethods,
                mfaPolicy = parameters.mfaPolicy,
                rbacEmailImplicitRoleAssignments = parameters.rbacEmailImplicitRoleAssignments,
            ).apply {
                if (this is StytchResult.Success) {
                    sessionStorage.organization = this.value.organization
                }
            }
        }

    override fun update(
        parameters: Organization.UpdateOrganizationParameters,
        callback: (UpdateOrganizationResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = update(parameters)
            callback(result)
        }
    }

    override suspend fun delete(): DeleteOrganizationResponse =
        withContext(dispatchers.io) {
            api.deleteOrganization().apply {
                if (this is StytchResult.Success) {
                    sessionStorage.organization = null
                    sessionStorage.member = null
                    sessionStorage.updateSession(null, null, null)
                }
            }
        }

    override fun delete(callback: (DeleteOrganizationResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = delete()
            callback(result)
        }
    }

    override val members: Organization.OrganizationMembers = OrganizationMembersImpl()

    private inner class OrganizationMembersImpl : Organization.OrganizationMembers {
        override suspend fun delete(memberId: String): DeleteMemberResponse =
            withContext(dispatchers.io) {
                api.deleteOrganizationMember(memberId)
            }

        override fun delete(
            memberId: String,
            callback: (DeleteMemberResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = members.delete(memberId)
                callback(result)
            }
        }
    }
}

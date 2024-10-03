package com.stytch.sdk.b2b.organization

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
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.B2BRequests
import com.stytch.sdk.b2b.network.models.OrganizationData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchObject
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.stytchObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class OrganizationImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.Organization,
) : Organization {
    private val callbacks = mutableListOf<(StytchObject<OrganizationData>) -> Unit>()

    override suspend fun onChange(): StateFlow<StytchObject<OrganizationData>> =
        combine(sessionStorage.organizationFlow, sessionStorage.lastValidatedAtFlow, ::stytchObjectMapper)
            .stateIn(
                externalScope,
                SharingStarted.WhileSubscribed(),
                stytchObjectMapper<OrganizationData>(sessionStorage.organization, sessionStorage.lastValidatedAt),
            )

    init {
        externalScope.launch {
            onChange().collect {
                callbacks.forEach { callback ->
                    callback(it)
                }
            }
        }
    }

    override fun onChange(callback: (StytchObject<OrganizationData>) -> Unit) {
        callbacks.add(callback)
    }

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

    override fun getCompletable(): CompletableFuture<OrganizationResponse> =
        externalScope
            .async {
                get()
            }.asCompletableFuture()

    override fun getSync(): OrganizationData? = sessionStorage.organization

    override suspend fun update(parameters: Organization.UpdateOrganizationParameters): UpdateOrganizationResponse =
        withContext(dispatchers.io) {
            api
                .updateOrganization(
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

    override fun updateCompletable(
        parameters: Organization.UpdateOrganizationParameters,
    ): CompletableFuture<UpdateOrganizationResponse> =
        externalScope
            .async {
                update(parameters)
            }.asCompletableFuture()

    override suspend fun delete(): DeleteOrganizationResponse =
        withContext(dispatchers.io) {
            api.deleteOrganization().apply {
                if (this is StytchResult.Success) {
                    sessionStorage.organization = null
                    sessionStorage.member = null
                    sessionStorage.updateSession(null, null, null, null)
                }
            }
        }

    override fun delete(callback: (DeleteOrganizationResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = delete()
            callback(result)
        }
    }

    override fun deleteCompletable(): CompletableFuture<DeleteOrganizationResponse> =
        externalScope
            .async {
                delete()
            }.asCompletableFuture()

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

        override fun deleteCompletable(memberId: String): CompletableFuture<DeleteMemberResponse> =
            externalScope
                .async {
                    delete(memberId)
                }.asCompletableFuture()

        override suspend fun reactivate(memberId: String): ReactivateMemberResponse =
            withContext(dispatchers.io) {
                api.reactivateOrganizationMember(memberId = memberId)
            }

        override fun reactivate(
            memberId: String,
            callback: (ReactivateMemberResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(members.reactivate(memberId))
            }
        }

        override fun reactivateCompletable(memberId: String): CompletableFuture<ReactivateMemberResponse> =
            externalScope
                .async {
                    reactivate(memberId)
                }.asCompletableFuture()

        override suspend fun deleteMemberAuthenticationFactor(
            memberId: String,
            authenticationFactor: MemberAuthenticationFactor,
        ): DeleteOrganizationMemberAuthenticationFactorResponse =
            withContext(dispatchers.io) {
                when (authenticationFactor) {
                    is MemberAuthenticationFactor.MfaPhoneNumber -> api.deleteOrganizationMemberMFAPhoneNumber(memberId)
                    is MemberAuthenticationFactor.MfaTOTP -> api.deleteOrganizationMemberMFATOTP(memberId)
                    is MemberAuthenticationFactor.Password ->
                        api.deleteOrganizationMemberPassword(
                            authenticationFactor.id,
                        )
                }
            }

        override fun deleteMemberAuthenticationFactor(
            memberId: String,
            authenticationFactor: MemberAuthenticationFactor,
            callback: (DeleteOrganizationMemberAuthenticationFactorResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(members.deleteMemberAuthenticationFactor(memberId, authenticationFactor))
            }
        }

        override fun deleteMemberAuthenticationFactorCompletable(
            memberId: String,
            authenticationFactor: MemberAuthenticationFactor,
        ): CompletableFuture<DeleteOrganizationMemberAuthenticationFactorResponse> =
            externalScope
                .async {
                    deleteMemberAuthenticationFactor(memberId, authenticationFactor)
                }.asCompletableFuture()

        override suspend fun create(
            parameters: Organization.OrganizationMembers.CreateMemberParameters,
        ): CreateMemberResponse =
            withContext(dispatchers.io) {
                api.createOrganizationMember(
                    emailAddress = parameters.emailAddress,
                    name = parameters.name,
                    isBreakGlass = parameters.isBreakGlass,
                    mfaEnrolled = parameters.mfaEnrolled,
                    mfaPhoneNumber = parameters.mfaPhoneNumber,
                    untrustedMetadata = parameters.untrustedMetadata,
                    createMemberAsPending = parameters.createMemberAsPending,
                    roles = parameters.roles,
                )
            }

        override fun create(
            parameters: Organization.OrganizationMembers.CreateMemberParameters,
            callback: (CreateMemberResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = members.create(parameters)
                callback(result)
            }
        }

        override fun createCompletable(
            parameters: Organization.OrganizationMembers.CreateMemberParameters,
        ): CompletableFuture<CreateMemberResponse> =
            externalScope
                .async {
                    create(parameters)
                }.asCompletableFuture()

        override suspend fun update(
            parameters: Organization.OrganizationMembers.UpdateMemberParameters,
        ): UpdateOrganizationMemberResponse =
            withContext(dispatchers.io) {
                api.updateOrganizationMember(
                    memberId = parameters.memberId,
                    emailAddress = parameters.emailAddress,
                    name = parameters.name,
                    isBreakGlass = parameters.isBreakGlass,
                    mfaEnrolled = parameters.mfaEnrolled,
                    mfaPhoneNumber = parameters.mfaPhoneNumber,
                    untrustedMetadata = parameters.untrustedMetadata,
                    roles = parameters.roles,
                    preserveExistingSessions = parameters.preserveExistingSessions,
                    defaultMfaMethod = parameters.defaultMfaMethod,
                )
            }

        override fun update(
            parameters: Organization.OrganizationMembers.UpdateMemberParameters,
            callback: (UpdateOrganizationMemberResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(update(parameters))
            }
        }

        override fun updateCompletable(
            parameters: Organization.OrganizationMembers.UpdateMemberParameters,
        ): CompletableFuture<UpdateOrganizationMemberResponse> =
            externalScope
                .async {
                    update(parameters)
                }.asCompletableFuture()

        override suspend fun search(
            parameters: Organization.OrganizationMembers.SearchParameters,
        ): MemberSearchResponse =
            withContext(dispatchers.io) {
                api.search(
                    cursor = parameters.cursor,
                    limit = parameters.limit,
                    query =
                        parameters.query?.let {
                            B2BRequests.SearchQuery(
                                operator = it.operator,
                                operands =
                                    it.operands.map { operand ->
                                        B2BRequests.SearchQueryOperand(
                                            filterName = operand.filterName,
                                            filterValue = operand.filterValue,
                                        )
                                    },
                            )
                        },
                )
            }

        override fun search(
            parameters: Organization.OrganizationMembers.SearchParameters,
            callback: (MemberSearchResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(members.search(parameters))
            }
        }

        override fun searchCompletable(
            parameters: Organization.OrganizationMembers.SearchParameters,
        ): CompletableFuture<MemberSearchResponse> =
            externalScope
                .async {
                    search(parameters)
                }.asCompletableFuture()
    }
}

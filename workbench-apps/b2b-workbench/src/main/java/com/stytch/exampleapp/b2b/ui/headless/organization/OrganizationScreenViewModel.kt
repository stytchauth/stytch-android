package com.stytch.exampleapp.b2b.ui.headless.organization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.member.MemberAuthenticationFactor
import com.stytch.sdk.b2b.network.models.SearchOperator
import com.stytch.sdk.b2b.organization.Organization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrganizationScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: OrganizationAction) =
        when (action) {
            is OrganizationAction.CreateOrganizationMember -> createOrganizationMember(action.emailAddress)
            is OrganizationAction.DeleteMemberMFANumber -> deleteMemberMFANumber(action.memberId)
            OrganizationAction.DeleteOrganization -> deleteOrganization()
            is OrganizationAction.DeleteOrganizationMember -> deleteOrganizationMember(action.memberId)
            is OrganizationAction.ReactivateOrganizationMember -> reactivateOrganizationMember(action.memberId)
            is OrganizationAction.SearchOrganizationMembersByEmail ->
                searchOrganizationMembersByEmail(
                    action.emailAddress,
                )
            is OrganizationAction.UpdateMemberPhoneNumber ->
                updateMemberPhoneNumber(
                    action.memberId,
                    action.phoneNumber,
                )
            is OrganizationAction.UpdateOrganizationName -> updateOrganizationName(action.name)
        }

    private fun createOrganizationMember(emailAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.organization.members.create(
                    Organization.OrganizationMembers.CreateMemberParameters(
                        emailAddress = emailAddress,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun deleteMemberMFANumber(memberId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.organization.members.deleteMemberAuthenticationFactor(
                    memberId = memberId,
                    authenticationFactor = MemberAuthenticationFactor.MfaPhoneNumber,
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun deleteOrganization() {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response = StytchB2BClient.organization.delete()
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun deleteOrganizationMember(memberId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response = StytchB2BClient.organization.members.delete(memberId)
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun reactivateOrganizationMember(memberId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response = StytchB2BClient.organization.members.reactivate(memberId)
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun searchOrganizationMembersByEmail(emailAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.organization.members
                    .search(
                        Organization.OrganizationMembers.SearchParameters(
                            query =
                                Organization.OrganizationMembers.SearchQuery(
                                    operator = SearchOperator.AND,
                                    operands =
                                        listOf(
                                            Organization.OrganizationMembers.SearchQueryOperand.MemberEmails(
                                                value = listOf(emailAddress),
                                            ),
                                        ),
                                ),
                        ),
                    )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun updateMemberPhoneNumber(
        memberId: String,
        phoneNumber: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.organization.members.update(
                    Organization.OrganizationMembers.UpdateMemberParameters(
                        memberId = memberId,
                        mfaPhoneNumber = phoneNumber,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun updateOrganizationName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.organization.update(
                    Organization.UpdateOrganizationParameters(
                        organizationName = name,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }
}

sealed interface OrganizationAction {
    data object DeleteOrganization : OrganizationAction

    data class UpdateOrganizationName(
        val name: String,
    ) : OrganizationAction

    data class DeleteOrganizationMember(
        val memberId: String,
    ) : OrganizationAction

    data class ReactivateOrganizationMember(
        val memberId: String,
    ) : OrganizationAction

    data class DeleteMemberMFANumber(
        val memberId: String,
    ) : OrganizationAction

    data class CreateOrganizationMember(
        val emailAddress: String,
    ) : OrganizationAction

    data class UpdateMemberPhoneNumber(
        val memberId: String,
        val phoneNumber: String,
    ) : OrganizationAction

    data class SearchOrganizationMembersByEmail(
        val emailAddress: String,
    ) : OrganizationAction
}

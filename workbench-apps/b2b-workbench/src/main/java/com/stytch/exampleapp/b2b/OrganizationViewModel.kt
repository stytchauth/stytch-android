package com.stytch.exampleapp.b2b

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.member.MemberAuthenticationFactor
import com.stytch.sdk.b2b.network.models.SearchOperator
import com.stytch.sdk.b2b.organization.Organization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrganizationViewModel : ViewModel() {
    var memberIdState by mutableStateOf(TextFieldValue(""))
    var memberPhoneState by mutableStateOf(TextFieldValue(""))
    var memberEmailState by mutableStateOf(TextFieldValue(""))
    var memberNameState by mutableStateOf(TextFieldValue(""))
    var newOrgNameState by mutableStateOf(TextFieldValue(""))
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    private fun CoroutineScope.launchAndToggleLoadingState(block: suspend () -> Unit): DisposableHandle =
        launch {
            _loadingState.value = true
            block()
        }.invokeOnCompletion {
            _loadingState.value = false
        }

    fun deleteOrganization() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value = StytchB2BClient.organization.delete().toFriendlyDisplay()
        }
    }

    fun updateOrganizationName() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.organization
                    .update(
                        Organization.UpdateOrganizationParameters(organizationName = newOrgNameState.text),
                    ).toFriendlyDisplay()
        }
    }

    fun deleteOrganizationMember() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.organization.members
                    .delete(
                        memberId = memberIdState.text,
                    ).toFriendlyDisplay()
        }
    }

    fun reactivateOrganizationMember() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.organization.members
                    .reactivate(
                        memberId = memberIdState.text,
                    ).toFriendlyDisplay()
        }
    }

    fun deleteMemberMfaPhoneNumber() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.organization.members
                    .deleteMemberAuthenticationFactor(
                        memberId = memberIdState.text,
                        authenticationFactor = MemberAuthenticationFactor.MfaPhoneNumber,
                    ).toFriendlyDisplay()
        }
    }

    fun createOrganizationMember() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.organization.members
                    .create(
                        Organization.OrganizationMembers.CreateMemberParameters(
                            emailAddress = memberEmailState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun updateOrganizationMemberPhone() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.organization.members
                    .update(
                        Organization.OrganizationMembers.UpdateMemberParameters(
                            memberId = memberIdState.text,
                            mfaPhoneNumber = memberPhoneState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun searchOrganizationMembersByEmail() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.organization.members
                    .search(
                        Organization.OrganizationMembers.SearchParameters(
                            query =
                                Organization.OrganizationMembers.SearchQuery(
                                    operator = SearchOperator.AND,
                                    operands =
                                        listOf(
                                            Organization.OrganizationMembers.SearchQueryOperand.MemberEmails(
                                                value = listOf(memberEmailState.text),
                                            ),
                                        ),
                                ),
                        ),
                    ).toFriendlyDisplay()
        }
    }
}

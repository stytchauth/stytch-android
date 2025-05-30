package com.stytch.exampleapp.b2b.ui.headless.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.member.Member
import com.stytch.sdk.b2b.member.MemberAuthenticationFactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemberScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: MemberAction) =
        when (action) {
            is MemberAction.UpdateName -> updateName(action.name)
            MemberAction.DeleteMFAPhoneNumber -> deleteFactor(MemberAuthenticationFactor.MfaPhoneNumber)
            MemberAction.DeleteMFATOTP -> deleteFactor(MemberAuthenticationFactor.MfaTOTP)
        }

    private fun updateName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response = StytchB2BClient.member.update(Member.UpdateParams(name = name))
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun deleteFactor(factor: MemberAuthenticationFactor) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchB2BClient.member.deleteFactor(factor)))
        }
    }
}

sealed interface MemberAction {
    data class UpdateName(
        val name: String,
    ) : MemberAction

    data object DeleteMFAPhoneNumber : MemberAction

    data object DeleteMFATOTP : MemberAction
}

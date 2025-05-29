package com.stytch.exampleapp.b2b.ui.headless.totp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.totp.TOTP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TOTPScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: TOTPAction) =
        when (action) {
            is TOTPAction.Create -> create(action.organizationId, action.memberId)
            is TOTPAction.Authenticate -> authenticate(action.organizationId, action.memberId, action.code)
        }

    private fun create(
        organizationId: String,
        memberId: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.totp.create(
                    TOTP.CreateParameters(
                        organizationId = organizationId,
                        memberId = memberId,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun authenticate(
        organizationId: String,
        memberId: String,
        code: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.totp.authenticate(
                    TOTP.AuthenticateParameters(
                        organizationId = organizationId,
                        memberId = memberId,
                        code = code,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }
}

sealed interface TOTPAction {
    data class Create(
        val organizationId: String,
        val memberId: String,
    ) : TOTPAction

    data class Authenticate(
        val organizationId: String,
        val memberId: String,
        val code: String,
    ) : TOTPAction
}

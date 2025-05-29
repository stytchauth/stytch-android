package com.stytch.exampleapp.b2b.ui.headless.scim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.scim.SCIM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SCIMScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: SCIMAction) =
        when (action) {
            is SCIMAction.CreateConnection -> createConnection(action.displayName, action.identityProvider)
            is SCIMAction.DeleteConnection -> deleteConnection(action.connectionId)
            SCIMAction.GetConnection -> getConnection()
            SCIMAction.GetConnectionGroups -> getConnectionGroups()
            is SCIMAction.RotateCancel -> rotateCancel(action.connectionId)
            is SCIMAction.RotateComplete -> rotateComplete(action.connectionId)
            is SCIMAction.RotateStart -> rotateStart(action.connectionId)
            is SCIMAction.UpdateConnection ->
                updateConnection(
                    action.connectionId,
                    action.displayName,
                    action.identityProvider,
                )
        }

    private fun createConnection(
        displayName: String,
        identityProvider: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.scim.createConnection(
                    SCIM.CreateConnectionParameters(
                        displayName = displayName,
                        identityProvider = identityProvider,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun deleteConnection(connectionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchB2BClient.scim.deleteConnection(connectionId)))
        }
    }

    private fun getConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchB2BClient.scim.getConnection()))
        }
    }

    private fun getConnectionGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(
                HeadlessMethodResponseState.Response(
                    StytchB2BClient.scim.getConnectionGroups(SCIM.GetConnectionGroupsParameters()),
                ),
            )
        }
    }

    private fun rotateStart(connectionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchB2BClient.scim.rotateStart(connectionId)))
        }
    }

    private fun rotateComplete(connectionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchB2BClient.scim.rotateComplete(connectionId)))
        }
    }

    private fun rotateCancel(connectionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchB2BClient.scim.rotateCancel(connectionId)))
        }
    }

    private fun updateConnection(
        connectionId: String,
        displayName: String,
        identityProvider: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.scim.updateConnection(
                    SCIM.UpdateConnectionParameters(
                        connectionId = connectionId,
                        displayName = displayName,
                        identityProvider = identityProvider,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }
}

sealed interface SCIMAction {
    data class CreateConnection(
        val displayName: String,
        val identityProvider: String,
    ) : SCIMAction

    data class UpdateConnection(
        val connectionId: String,
        val displayName: String,
        val identityProvider: String,
    ) : SCIMAction

    data class DeleteConnection(
        val connectionId: String,
    ) : SCIMAction

    data object GetConnection : SCIMAction

    data object GetConnectionGroups : SCIMAction

    data class RotateStart(
        val connectionId: String,
    ) : SCIMAction

    data class RotateCancel(
        val connectionId: String,
    ) : SCIMAction

    data class RotateComplete(
        val connectionId: String,
    ) : SCIMAction
}

package com.stytch.sdk.b2b.scim

import com.stytch.sdk.b2b.SCIMCreateConnectionResponse
import com.stytch.sdk.b2b.SCIMDeleteConnectionResponse
import com.stytch.sdk.b2b.SCIMGetConnectionGroupsResponse
import com.stytch.sdk.b2b.SCIMGetConnectionResponse
import com.stytch.sdk.b2b.SCIMRotateCancelResponse
import com.stytch.sdk.b2b.SCIMRotateCompleteResponse
import com.stytch.sdk.b2b.SCIMRotateStartResponse
import com.stytch.sdk.b2b.SCIMUpdateConnectionResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SCIMImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val api: StytchB2BApi.SCIM,
) : SCIM {
    override suspend fun createConnection(parameters: SCIM.CreateConnectionParameters): SCIMCreateConnectionResponse =
        withContext(dispatchers.io) {
            api.createConnection(
                displayName = parameters.displayName,
                identityProvider = parameters.identityProvider,
            )
        }

    override fun createConnection(
        parameters: SCIM.CreateConnectionParameters,
        callback: (SCIMCreateConnectionResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(createConnection(parameters))
        }
    }

    override suspend fun updateConnection(parameters: SCIM.UpdateConnectionParameters): SCIMUpdateConnectionResponse =
        withContext(dispatchers.io) {
            api.updateConnection(
                connectionId = parameters.connectionId,
                displayName = parameters.displayName,
                identityProvider = parameters.identityProvider,
                scimGroupImplicitRoleAssignments = parameters.scimGroupImplicitRoleAssignments,
            )
        }

    override fun updateConnection(
        parameters: SCIM.UpdateConnectionParameters,
        callback: (SCIMUpdateConnectionResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(updateConnection(parameters))
        }
    }

    override suspend fun deleteConnection(connectionId: String): SCIMDeleteConnectionResponse =
        withContext(dispatchers.io) {
            api.deleteConection(connectionId = connectionId)
        }

    override fun deleteConnection(
        connectionId: String,
        callback: (SCIMDeleteConnectionResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(deleteConnection(connectionId))
        }
    }

    override suspend fun getConnection(): SCIMGetConnectionResponse =
        withContext(dispatchers.io) {
            api.getConnection()
        }

    override fun getConnection(callback: (SCIMGetConnectionResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(getConnection())
        }
    }

    override suspend fun getConnectionGroups(
        parameters: SCIM.GetConnectionGroupsParameters,
    ): SCIMGetConnectionGroupsResponse =
        withContext(dispatchers.io) {
            api.getConnectionGroups(
                limit = parameters.limit,
                cursor = parameters.cursor,
            )
        }

    override fun getConnectionGroups(
        parameters: SCIM.GetConnectionGroupsParameters,
        callback: (SCIMGetConnectionGroupsResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(getConnectionGroups(parameters))
        }
    }

    override suspend fun rotateStart(connectionId: String): SCIMRotateStartResponse =
        withContext(dispatchers.io) {
            api.rotateStart(connectionId = connectionId)
        }

    override fun rotateStart(
        connectionId: String,
        callback: (SCIMRotateStartResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(rotateStart(connectionId))
        }
    }

    override suspend fun rotateComplete(connectionId: String): SCIMRotateCompleteResponse =
        withContext(dispatchers.io) {
            api.rotateComplete(connectionId = connectionId)
        }

    override fun rotateComplete(
        connectionId: String,
        callback: (SCIMRotateCompleteResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(rotateComplete(connectionId))
        }
    }

    override suspend fun rotateCancel(connectionId: String): SCIMRotateCancelResponse =
        withContext(dispatchers.io) {
            api.rotateCancel(connectionId = connectionId)
        }

    override fun rotateCancel(
        connectionId: String,
        callback: (SCIMRotateCancelResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(rotateCancel(connectionId))
        }
    }
}

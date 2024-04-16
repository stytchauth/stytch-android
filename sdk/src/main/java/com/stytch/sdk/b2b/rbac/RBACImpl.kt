package com.stytch.sdk.b2b.rbac

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class RBACImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
) : RBAC {
    private fun getRoleIds(): List<String> {
        return sessionStorage.memberSession?.roles ?: emptyList()
    }

    override fun isAuthorizedSync(
        resourceId: String,
        action: String,
    ): Boolean {
        return StytchB2BClient.bootstrapData.rbacPolicy?.callerIsAuthorized(
            memberRoles = getRoleIds(),
            resourceId = resourceId,
            action = action,
        ) ?: false
    }

    override suspend fun isAuthorized(
        resourceId: String,
        action: String,
    ): Boolean {
        StytchB2BClient.refreshBootstrapData()
        return isAuthorizedSync(resourceId = resourceId, action = action)
    }

    override fun isAuthorized(
        resourceId: String,
        action: String,
        callback: (Boolean) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(isAuthorized(resourceId, action))
        }
    }

    override suspend fun allPermissions(): Map<String, Map<String, Boolean>> {
        StytchB2BClient.refreshBootstrapData()
        return StytchB2BClient.bootstrapData.rbacPolicy?.allPermissionsForCaller(getRoleIds()) ?: emptyMap()
    }

    override fun allPermissions(callback: (Map<String, Map<String, Boolean>>) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(allPermissions())
        }
    }
}

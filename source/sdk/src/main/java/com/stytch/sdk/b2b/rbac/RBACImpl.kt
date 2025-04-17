package com.stytch.sdk.b2b.rbac

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

internal class RBACImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
) : RBAC {
    private fun getRoleIds(): List<String> = sessionStorage.memberSession?.roles ?: emptyList()

    override fun isAuthorizedSync(
        resourceId: String,
        action: String,
    ): Boolean =
        StytchB2BClient.configurationManager.bootstrapData.rbacPolicy?.callerIsAuthorized(
            memberRoles = getRoleIds(),
            resourceId = resourceId,
            action = action,
        ) ?: false

    override suspend fun isAuthorized(
        resourceId: String,
        action: String,
    ): Boolean {
        StytchB2BClient.configurationManager.refreshBootstrapData()
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

    override fun isAuthorizedCompletable(
        resourceId: String,
        action: String,
    ): CompletableFuture<Boolean> =
        externalScope
            .async {
                isAuthorized(resourceId, action)
            }.asCompletableFuture()

    override suspend fun allPermissions(): Map<String, Map<String, Boolean>> {
        StytchB2BClient.configurationManager.refreshBootstrapData()
        return StytchB2BClient.configurationManager.bootstrapData.rbacPolicy
            ?.allPermissionsForCaller(getRoleIds())
            ?: emptyMap()
    }

    override fun allPermissions(callback: (Map<String, Map<String, Boolean>>) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(allPermissions())
        }
    }

    override fun allPermissionsCompletable(): CompletableFuture<Map<String, Map<String, Boolean>>> =
        externalScope
            .async {
                allPermissions()
            }.asCompletableFuture()
}

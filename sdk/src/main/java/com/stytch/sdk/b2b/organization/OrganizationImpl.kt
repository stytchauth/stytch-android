package com.stytch.sdk.b2b.organization

import com.stytch.sdk.b2b.OrganizationResponse
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
}

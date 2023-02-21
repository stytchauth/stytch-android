package com.stytch.sdk.b2b.organization

import com.stytch.sdk.b2b.OrganizationResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OrganizationImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val api: StytchB2BApi.Organization
) : Organization {
    override suspend fun get(): OrganizationResponse =
        withContext(dispatchers.io) {
            api.getOrganization()
        }

    override fun get(callback: (OrganizationResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = get()
            callback(result)
        }
    }
}

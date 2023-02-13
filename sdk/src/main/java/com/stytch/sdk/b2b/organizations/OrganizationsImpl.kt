package com.stytch.sdk.b2b.organizations

import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.OrganizationResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OrganizationsImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val api: StytchB2BApi.Organizations
) : Organizations {
    override suspend fun getOrganization(): OrganizationResponse =
        withContext(dispatchers.io) {
            api.getOrganization()
        }

    override fun getOrganization(callback: (OrganizationResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = getOrganization()
            callback(result)
        }
    }

    override suspend fun getMember(): MemberResponse =
        withContext(dispatchers.io) {
            api.getMember()
        }

    override fun getMember(callback: (MemberResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = getMember()
            callback(result)
        }
    }
}

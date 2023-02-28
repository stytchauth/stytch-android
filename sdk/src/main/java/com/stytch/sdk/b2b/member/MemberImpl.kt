package com.stytch.sdk.b2b.member

import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.MemberData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MemberImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.Member,
) : Member {
    override suspend fun get(): MemberResponse =
        withContext(dispatchers.io) {
            api.getMember()
        }

    override fun get(callback: (MemberResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = get()
            callback(result)
        }
    }

    override fun getSync(): MemberData? = sessionStorage.member
}

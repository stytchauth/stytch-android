package com.stytch.sdk.b2b.member

import com.stytch.sdk.b2b.DeleteMemberAuthenticationFactorResponse
import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.UpdateMemberResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.MemberData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MemberImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.Member,
) : Member {
    private val callbacks = mutableListOf<(MemberData?) -> Unit>()

    override val onChange: StateFlow<MemberData?> = sessionStorage.memberFlow

    init {
        externalScope.launch {
            onChange.collect {
                callbacks.forEach { callback ->
                    callback(it)
                }
            }
        }
    }

    override fun onChange(callback: (MemberData?) -> Unit) {
        callbacks.add(callback)
    }

    override suspend fun get(): MemberResponse =
        withContext(dispatchers.io) {
            api.getMember().apply {
                if (this is StytchResult.Success) {
                    sessionStorage.member = this.value.member
                }
            }
        }

    override fun get(callback: (MemberResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = get()
            callback(result)
        }
    }

    override fun getSync(): MemberData? = sessionStorage.member

    override suspend fun update(params: Member.UpdateParams): UpdateMemberResponse =
        withContext(dispatchers.io) {
            api.updateMember(
                name = params.name,
                untrustedMetadata = params.untrustedMetadata,
                mfaEnrolled = params.mfaEnrolled,
                mfaPhoneNumber = params.mfaPhoneNumber,
                defaultMfaMethod = params.defaultMfaMethod,
            )
        }

    override fun update(
        params: Member.UpdateParams,
        callback: (UpdateMemberResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = update(params)
            callback(result)
        }
    }

    override suspend fun deleteFactor(factor: MemberAuthenticationFactor): DeleteMemberAuthenticationFactorResponse =
        withContext(dispatchers.io) {
            when (factor) {
                is MemberAuthenticationFactor.MfaPhoneNumber -> api.deleteMFAPhoneNumber()
                is MemberAuthenticationFactor.MfaTOTP -> api.deleteMFATOTP()
                is MemberAuthenticationFactor.Password -> api.deletePassword(factor.id)
            }.apply {
                if (this is StytchResult.Success) {
                    sessionStorage.member = this.value.member
                }
            }
        }

    override fun deleteFactor(
        factor: MemberAuthenticationFactor,
        callback: (DeleteMemberAuthenticationFactorResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = deleteFactor(factor)
            callback(result)
        }
    }
}

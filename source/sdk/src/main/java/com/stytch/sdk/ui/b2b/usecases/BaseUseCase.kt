package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import com.stytch.sdk.ui.b2b.domain.B2BUIStateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal open class BaseUseCase(
    private val scope: CoroutineScope,
    internal open val stateMachine: B2BUIStateMachine,
) {
    internal fun performAuthenticationRequest(request: suspend () -> StytchResult<CommonAuthenticationData>) {
        stateMachine.setLoading(true)
        scope
            .launch(Dispatchers.IO) {
                stateMachine.handleAuthenticationResponse(request())
            }.invokeOnCompletion {
                stateMachine.setLoading(false)
            }
    }

    internal suspend inline fun <T> performGenericRequest(
        crossinline request: suspend () -> StytchResult<T>,
    ): Result<T> {
        stateMachine.setLoading(true)
        val stytchResult =
            withContext(Dispatchers.IO) {
                val response = request()
                stateMachine.setLoading(false)
                response
            }
        return when (stytchResult) {
            is StytchResult.Success -> Result.success(stytchResult.value)
            is StytchResult.Error -> Result.failure(stytchResult.exception)
        }
    }
}

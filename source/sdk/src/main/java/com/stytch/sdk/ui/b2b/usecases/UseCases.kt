package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.ui.b2b.domain.B2BUIStateMachine
import kotlinx.coroutines.CoroutineScope

internal class UseCases private constructor(
    scope: CoroutineScope,
    stateMachine: B2BUIStateMachine,
) {
    val usePasswordsStrengthCheck = UsePasswordsStrengthCheck(scope, stateMachine)
    val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(scope, stateMachine)
    val useUpdateMemberPassword = UseUpdateMemberPassword(scope, stateMachine)

    companion object {
        @Volatile
        private var instance: UseCases? = null

        fun getInstance(
            scope: CoroutineScope,
            stateMachine: B2BUIStateMachine,
        ): UseCases {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = UseCases(scope, stateMachine)
                    }
                }
            }
            return instance!!
        }
    }
}

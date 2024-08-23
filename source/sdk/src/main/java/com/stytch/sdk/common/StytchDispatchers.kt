package com.stytch.sdk.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal data class StytchDispatchers(
    val io: CoroutineDispatcher = Dispatchers.IO,
    val ui: CoroutineDispatcher = Dispatchers.Main,
)

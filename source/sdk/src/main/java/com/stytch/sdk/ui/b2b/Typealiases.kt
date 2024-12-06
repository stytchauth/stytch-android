package com.stytch.sdk.ui.b2b

import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.b2b.data.B2BUIAction

internal typealias PerformRequest<T> = suspend (suspend () -> StytchResult<T>) -> Result<T>

internal typealias CustomizeLoadingIndicatorRequest<T> = suspend (Boolean, suspend () -> StytchResult<T>) -> Result<T>

internal typealias Dispatch = (B2BUIAction) -> Unit

internal typealias CreateViewModel<T> = (Class<T>) -> T

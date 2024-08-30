package com.stytch.sdk.ui.data

import com.stytch.sdk.common.StytchResult

internal sealed class EventState {
    data class NavigationRequested(
        val navigationRoute: NavigationRoute,
    ) : EventState()

    data class Authenticated(
        val result: StytchResult<Any>,
    ) : EventState()

    data object Exit : EventState()
}

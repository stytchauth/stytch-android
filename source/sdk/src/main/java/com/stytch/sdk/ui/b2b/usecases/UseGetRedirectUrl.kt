package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient

internal fun getRedirectUrl(): String = "stytchui-${StytchB2BClient.publicToken}://deeplink"

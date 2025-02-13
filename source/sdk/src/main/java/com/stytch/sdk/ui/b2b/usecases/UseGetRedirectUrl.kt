package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient

internal fun getRedirectUrl(): String = "${StytchB2BClient.publicToken}://b2b-ui"

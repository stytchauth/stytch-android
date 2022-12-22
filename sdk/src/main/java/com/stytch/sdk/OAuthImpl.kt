package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sessions.SessionStorage
import kotlinx.coroutines.CoroutineScope

internal class OAuthImpl(
    externalScope: CoroutineScope,
    dispatchers: StytchDispatchers,
    sessionStorage: SessionStorage,
    api: StytchApi.OAuth,
) : OAuth {
    override val googleOneTap: OAuth.GoogleOneTap = GoogleOneTapImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        api,
        GoogleOneTapProviderImpl()
    )
}

package com.stytch.sdk

import com.stytch.sdk.network.responseData.StytchErrorResponse

public sealed class StytchExceptions(public open val reason: Any?) : Exception() {
    public class Critical(override val reason: Throwable) : StytchExceptions(reason)
    public class Connection(override val reason: Throwable) : StytchExceptions(reason)
    public class Input(override val reason: String) : StytchExceptions(reason)
    public class Response(override val reason: StytchErrorResponse?) : StytchExceptions(reason)
}
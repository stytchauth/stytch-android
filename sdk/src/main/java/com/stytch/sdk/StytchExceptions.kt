package com.stytch.sdk

import com.stytch.sdk.network.responseData.StytchErrorResponse

/**
 * Exception wrapper meant for use with Stytch-specific exceptions and provides a straightforward way to encompass any possible errors that come from the SDK.
 * @param reason provides a reasoning as to what exactly happened for the flow to go wrong.
 */
public sealed class StytchExceptions(public open val reason: Any?) : Exception() {

    /**
     * Critical exception wrapper
     * @param reason provides a Throwable with information on what went wrong.
     */
    public class Critical(override val reason: Throwable) : StytchExceptions(reason)

    /**
     * Connection exception wrapper
     * @param reason provides a Throwable with information on what went wrong.
     */
    public class Connection(override val reason: Throwable) : StytchExceptions(reason)

    /**
     * Input exception wrapper
     * @param reason provides a simple reasoning as a String as what went wrong
     */
    public class Input(override val reason: String) : StytchExceptions(reason)

    /**
     * Response exception wrapper
     * @param reason provides a StytchErrorResponse object with a reason as to what went wrong.
     */
    public class Response(override val reason: StytchErrorResponse?) : StytchExceptions(reason)
}
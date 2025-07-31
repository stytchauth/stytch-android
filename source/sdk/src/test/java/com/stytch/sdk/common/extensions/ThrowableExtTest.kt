package com.stytch.sdk.common.extensions

import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchAPISchemaError
import com.stytch.sdk.common.errors.StytchAPIUnreachableError
import com.stytch.sdk.common.errors.StytchDeeplinkUnkownTokenTypeError
import com.stytch.sdk.utils.API_ERROR_RESPONSE_STRING
import com.stytch.sdk.utils.EMPTY_ERROR_RESPONSE_STRING
import com.stytch.sdk.utils.SCHEMA_ERROR_RESPONSE_STRING
import com.stytch.sdk.utils.createHttpExceptionReturningString
import org.junit.Assert.fail
import org.junit.Test

internal class ThrowableExtTest {
    @Test
    fun `A StytchError returns the same StytchError`() {
        val originalException = StytchDeeplinkUnkownTokenTypeError()
        val returnedException = originalException.toStytchError()
        assert(originalException == returnedException)
    }

    @Test
    fun `An HttpException returns the appropriate StytchError`() {
        // cast the HttpExceptions as Throwables to ensure we're testing the correct extension method
        val originalApiError: Throwable = createHttpExceptionReturningString(API_ERROR_RESPONSE_STRING)
        val originalSchemaError: Throwable = createHttpExceptionReturningString(SCHEMA_ERROR_RESPONSE_STRING)
        val originalUnknownError: Throwable = createHttpExceptionReturningString(EMPTY_ERROR_RESPONSE_STRING)
        assert(originalApiError.toStytchError() is StytchAPIError)
        assert(originalSchemaError.toStytchError() is StytchAPISchemaError)
        assert(originalUnknownError.toStytchError() is StytchAPIUnreachableError)
    }

    @Test
    fun `Anything else returns StytchAPIUnreachableError`() {
        try {
            val originalException = RuntimeException("Something BAAAAAAD happened")
            assert(originalException.toStytchError() is StytchAPIUnreachableError)
        } catch (_: Exception) {
            fail("Should not have thrown an exception")
        }
    }
}

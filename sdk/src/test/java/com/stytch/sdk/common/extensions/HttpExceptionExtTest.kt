package com.stytch.sdk.common.extensions

import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchAPISchemaError
import com.stytch.sdk.common.errors.StytchAPIUnreachableError
import com.stytch.sdk.utils.API_ERROR_RESPONSE_STRING
import com.stytch.sdk.utils.EMPTY_ERROR_RESPONSE_STRING
import com.stytch.sdk.utils.SCHEMA_ERROR_RESPONSE_STRING
import com.stytch.sdk.utils.createHttpExceptionReturningString
import org.junit.Test
import retrofit2.HttpException

internal class HttpExceptionExtTest {
    @Test
    fun `StytchErrorResponse returns a StytchAPIError`() {
        val exception: HttpException = createHttpExceptionReturningString(API_ERROR_RESPONSE_STRING)
        val error = exception.toStytchError()
        assert(error is StytchAPIError)
    }

    @Test
    fun `StytchSchemaError returns a StytchAPISchemaError`() {
        val exception: HttpException = createHttpExceptionReturningString(SCHEMA_ERROR_RESPONSE_STRING)
        val error = exception.toStytchError()
        assert(error is StytchAPISchemaError)
    }

    @Test
    fun `Empty returns a StytchAPIUnreachableError`() {
        val exception: HttpException = createHttpExceptionReturningString(EMPTY_ERROR_RESPONSE_STRING)
        val error = exception.toStytchError()
        assert(error is StytchAPIUnreachableError)
    }
}

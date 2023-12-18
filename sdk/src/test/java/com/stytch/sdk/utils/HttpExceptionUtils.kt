package com.stytch.sdk.utils

import io.mockk.every
import io.mockk.mockk
import retrofit2.HttpException

internal const val API_ERROR_RESPONSE_STRING = """{
    "status_code": 400,
    "error_type": "some_api_error",
    "error_message": "This is a test error message",
    "error_url": "https://stytch.com/docs/something"
}"""

internal const val SCHEMA_ERROR_RESPONSE_STRING = """{
    "body": ""
}"""

internal const val EMPTY_ERROR_RESPONSE_STRING = ""

internal fun createHttpExceptionReturningString(string: String): HttpException = mockk {
    every { code() } returns 400
    every { message } returns "Something went wrong"
    every { response() } returns mockk {
        every { errorBody() } returns mockk {
            every { string() } returns string
        }
    }
}

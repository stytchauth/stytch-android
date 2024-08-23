package com.stytch.sdk.common.network

import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.network.models.CommonResponses
import com.stytch.sdk.utils.API_ERROR_RESPONSE_STRING
import com.stytch.sdk.utils.createHttpExceptionReturningString
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class SafeApiCallTest {
    @Test
    fun `A successful request returns StytchResult_Success`() =
        runTest {
            val result =
                safeApiCall({}) {
                    mockk<CommonResponses.Bootstrap.BootstrapResponse>(relaxed = true)
                }
            assert(result is StytchResult.Success)
        }

    @Test
    fun `An unsuccessful request returns StytchResult_Error`() =
        runTest {
            val result =
                safeApiCall({}) {
                    mockk<CommonResponses.Bootstrap.BootstrapResponse> {
                        every { data } throws createHttpExceptionReturningString(API_ERROR_RESPONSE_STRING)
                    }
                }
            require(result is StytchResult.Error)
            assert(result.exception is StytchAPIError)
        }
}

package com.stytch.sdk.b2b.scim

import com.stytch.sdk.b2b.SCIMCreateConnectionResponse
import com.stytch.sdk.b2b.SCIMDeleteConnectionResponse
import com.stytch.sdk.b2b.SCIMGetConnectionGroupsResponse
import com.stytch.sdk.b2b.SCIMGetConnectionResponse
import com.stytch.sdk.b2b.SCIMRotateCancelResponse
import com.stytch.sdk.b2b.SCIMRotateCompleteResponse
import com.stytch.sdk.b2b.SCIMRotateStartResponse
import com.stytch.sdk.b2b.SCIMUpdateConnectionResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.B2BSCIMCreateConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMDeleteConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMGetConnectionGroupsResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMGetConnectionResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMRotateCancelResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMRotateCompleteResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMRotateStartResponseData
import com.stytch.sdk.b2b.network.models.B2BSCIMUpdateConnectionResponseData
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class SCIMImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.SCIM

    private lateinit var impl: SCIMImpl
    private val dispatcher = Dispatchers.Unconfined

    private val mockCreateResponse = StytchResult.Success<B2BSCIMCreateConnectionResponseData>(mockk(relaxed = true))
    private val mockUpdateResponse = StytchResult.Success<B2BSCIMUpdateConnectionResponseData>(mockk(relaxed = true))
    private val mockDeleteResponse = StytchResult.Success<B2BSCIMDeleteConnectionResponseData>(mockk(relaxed = true))
    private val mockGetResponse = StytchResult.Success<B2BSCIMGetConnectionResponseData>(mockk(relaxed = true))
    private val mockGetGroupResponse =
        StytchResult.Success<B2BSCIMGetConnectionGroupsResponseData>(
            mockk(relaxed = true),
        )
    private val mockRotateStartResponse = StytchResult.Success<B2BSCIMRotateStartResponseData>(mockk(relaxed = true))
    private val mockRotateCancelResponse = StytchResult.Success<B2BSCIMRotateCancelResponseData>(mockk(relaxed = true))
    private val mockRotateCompleteResponse =
        StytchResult.Success<B2BSCIMRotateCompleteResponseData>(
            mockk(relaxed = true),
        )

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)

        coEvery { mockApi.createConnection(any(), any()) } returns mockCreateResponse
        coEvery { mockApi.updateConnection(any(), any(), any(), any()) } returns mockUpdateResponse
        coEvery { mockApi.deleteConection(any()) } returns mockDeleteResponse
        coEvery { mockApi.getConnection() } returns mockGetResponse
        coEvery { mockApi.getConnectionGroups(any(), any()) } returns mockGetGroupResponse
        coEvery { mockApi.rotateStart(any()) } returns mockRotateStartResponse
        coEvery { mockApi.rotateCancel(any()) } returns mockRotateCancelResponse
        coEvery { mockApi.rotateComplete(any()) } returns mockRotateCompleteResponse

        impl =
            SCIMImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                api = mockApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `SCIMImpl createConnection delegates to api`() =
        runTest {
            val response = impl.createConnection(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.createConnection(any(), any()) }
        }

    @Test
    fun `SCIMImpl createConnection with callback calls callback method`() {
        val mockCallback = spyk<(SCIMCreateConnectionResponse) -> Unit>()
        impl.createConnection(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockCreateResponse) }
    }

    @Test
    fun `SCIMImpl updateConnection delegates to api`() =
        runTest {
            val response = impl.updateConnection(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.updateConnection(any(), any(), any(), any()) }
        }

    @Test
    fun `SCIMImpl updateConnection with callback calls callback method`() {
        val mockCallback = spyk<(SCIMUpdateConnectionResponse) -> Unit>()
        impl.updateConnection(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockUpdateResponse) }
    }

    @Test
    fun `SCIMImpl deleteConnection delegates to api`() =
        runTest {
            val response = impl.deleteConnection("connection-id")
            assert(response is StytchResult.Success)
            coVerify { mockApi.deleteConection(any()) }
        }

    @Test
    fun `SCIMImpl deleteConnection with callback calls callback method`() {
        val mockCallback = spyk<(SCIMDeleteConnectionResponse) -> Unit>()
        impl.deleteConnection("connection-id", mockCallback)
        verify { mockCallback.invoke(mockDeleteResponse) }
    }

    @Test
    fun `SCIMImpl getConnection delegates to api`() =
        runTest {
            val response = impl.getConnection()
            assert(response is StytchResult.Success)
            coVerify { mockApi.getConnection() }
        }

    @Test
    fun `SCIMImpl getConnection with callback calls callback method`() {
        val mockCallback = spyk<(SCIMGetConnectionResponse) -> Unit>()
        impl.getConnection(mockCallback)
        verify { mockCallback.invoke(mockGetResponse) }
    }

    @Test
    fun `SCIMImpl getConnectionGroups delegates to api`() =
        runTest {
            val response = impl.getConnectionGroups(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.getConnectionGroups(any(), any()) }
        }

    @Test
    fun `SCIMImpl getConnectionGroups with callback calls callback method`() {
        val mockCallback = spyk<(SCIMGetConnectionGroupsResponse) -> Unit>()
        impl.getConnectionGroups(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockGetGroupResponse) }
    }

    @Test
    fun `SCIMImpl rotateStart delegates to api`() =
        runTest {
            val response = impl.rotateStart("connection-id")
            assert(response is StytchResult.Success)
            coVerify { mockApi.rotateStart(any()) }
        }

    @Test
    fun `SCIMImpl rotateStart with callback calls callback method`() {
        val mockCallback = spyk<(SCIMRotateStartResponse) -> Unit>()
        impl.rotateStart("connection-id", mockCallback)
        verify { mockCallback.invoke(mockRotateStartResponse) }
    }

    @Test
    fun `SCIMImpl rotateCancel delegates to api`() =
        runTest {
            val response = impl.rotateCancel("connection-id")
            assert(response is StytchResult.Success)
            coVerify { mockApi.rotateCancel(any()) }
        }

    @Test
    fun `SCIMImpl rotateCancel with callback calls callback method`() {
        val mockCallback = spyk<(SCIMRotateCancelResponse) -> Unit>()
        impl.rotateCancel("connection-id", mockCallback)
        verify { mockCallback.invoke(mockRotateCancelResponse) }
    }

    @Test
    fun `SCIMImpl rotateComplete delegates to api`() =
        runTest {
            val response = impl.rotateComplete("connection-id")
            assert(response is StytchResult.Success)
            coVerify { mockApi.rotateComplete(any()) }
        }

    @Test
    fun `SCIMImpl rotateComplete with callback calls callback method`() {
        val mockCallback = spyk<(SCIMRotateCompleteResponse) -> Unit>()
        impl.rotateComplete("connection-id", mockCallback)
        verify { mockCallback.invoke(mockRotateCompleteResponse) }
    }
}

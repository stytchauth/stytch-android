package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.b2b.DiscoverOrganizationsResponse
import com.stytch.sdk.b2b.IntermediateSessionExchangeResponse
import com.stytch.sdk.b2b.OrganizationCreateResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DiscoveryImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.Discovery

    private lateinit var impl: DiscoveryImpl
    private val dispatcher = Dispatchers.Unconfined

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        impl = DiscoveryImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            api = mockApi
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `DiscoveryImpl organizations delegates to api`() = runTest {
        coEvery { mockApi.discoverOrganizations(any()) } returns StytchResult.Success(mockk(relaxed = true))
        val response = impl.listOrganizations(mockk(relaxed = true))
        assert(response is StytchResult.Success)
        coVerify { mockApi.discoverOrganizations(any()) }
    }

    @Test
    fun `DiscoveryImpl organizations with callback calls callback method`() {
        coEvery { mockApi.discoverOrganizations(any()) } returns StytchResult.Success(mockk(relaxed = true))
        val mockCallback = spyk<(DiscoverOrganizationsResponse) -> Unit>()
        impl.listOrganizations(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `DiscoveryImpl exchangeSession delegates to api`() = runTest {
        coEvery { mockApi.exchangeSession(any(), any(), any()) } returns StytchResult.Success(mockk(relaxed = true))
        val response = impl.exchangeIntermediateSession(mockk(relaxed = true))
        assert(response is StytchResult.Success)
        coVerify { mockApi.exchangeSession(any(), any(), any()) }
    }

    @Test
    fun `DiscoveryImpl exchangeSession with callback calls callback method`() {
        coEvery { mockApi.exchangeSession(any(), any(), any()) } returns StytchResult.Success(mockk(relaxed = true))
        val mockCallback = spyk<(IntermediateSessionExchangeResponse) -> Unit>()
        impl.exchangeIntermediateSession(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `DiscoveryImpl create delegates to api`() = runTest {
        coEvery {
            mockApi.createOrganization(any(), any(), any(), any(), any())
        } returns StytchResult.Success(mockk(relaxed = true))
        val response = impl.createOrganization(mockk(relaxed = true))
        assert(response is StytchResult.Success)
        coVerify { mockApi.createOrganization(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `DiscoveryImpl create with callback calls callback method`() {
        coEvery {
            mockApi.createOrganization(any(), any(), any(), any(), any())
        } returns StytchResult.Success(mockk(relaxed = true))
        val mockCallback = spyk<(OrganizationCreateResponse) -> Unit>()
        impl.createOrganization(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}

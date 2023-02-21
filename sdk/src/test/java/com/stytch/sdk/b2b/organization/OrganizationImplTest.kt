package com.stytch.sdk.b2b.organization

import com.stytch.sdk.b2b.OrganizationResponse
import com.stytch.sdk.b2b.network.Organization
import com.stytch.sdk.b2b.network.OrganizationResponseData
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
internal class OrganizationImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.Organization

    private lateinit var impl: OrganizationImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulOrgResponse = StytchResult.Success<OrganizationResponseData>(mockk(relaxed = true))

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        impl = OrganizationImpl(
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
    fun `Organizations getOrganization delegates to api`() = runTest {
        coEvery { mockApi.getOrganization() } returns successfulOrgResponse
        val response = impl.get()
        assert(response is StytchResult.Success)
        coVerify { mockApi.getOrganization() }
    }

    @Test
    fun `Organizations getOrganization with callback calls callback method`() {
        coEvery { mockApi.getOrganization() } returns successfulOrgResponse
        val mockCallback = spyk<(OrganizationResponse) -> Unit>()
        impl.get(mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}

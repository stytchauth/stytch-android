package com.stytch.sdk.b2b.organizations

import com.stytch.sdk.b2b.OrganizationResponse
import com.stytch.sdk.b2b.network.Organization
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
internal class OrganizationsImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.Organizations

    private lateinit var impl: OrganizationsImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulAuthResponse = StytchResult.Success<Organization>(mockk(relaxed = true))

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        impl = OrganizationsImpl(
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
        coEvery { mockApi.getOrganization(any()) } returns successfulAuthResponse
        val response = impl.getOrganization(Organizations.GetOrganizationParameters("organizationId"))
        assert(response is StytchResult.Success)
        coVerify { mockApi.getOrganization(any()) }
    }

    @Test
    fun `Organizations getOrganization with callback calls callback method`() {
        coEvery { mockApi.getOrganization(any()) } returns successfulAuthResponse
        val mockCallback = spyk<(OrganizationResponse) -> Unit>()
        impl.getOrganization(Organizations.GetOrganizationParameters("organizationId"), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}

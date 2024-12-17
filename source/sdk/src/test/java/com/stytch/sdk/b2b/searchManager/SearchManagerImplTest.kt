package com.stytch.sdk.b2b.searchManager

import com.stytch.sdk.b2b.B2BSearchMemberResponse
import com.stytch.sdk.b2b.B2BSearchOrganizationResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.StytchDispatchers
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class SearchManagerImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.SearchManager
    private lateinit var impl: SearchManager
    private val dispatcher = Dispatchers.Unconfined

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        impl =
            SearchManagerImpl(
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
    fun `SearchManager searchOrganizations delegates to the api`() =
        runBlocking {
            coEvery { mockApi.searchOrganizations(any()) } returns mockk(relaxed = true)
            impl.searchOrganization(mockk(relaxed = true))
            coVerify { mockApi.searchOrganizations(any()) }
        }

    @Test
    fun `SearchManager searchOrganizations with callback calls callback`() {
        coEvery { mockApi.searchOrganizations(any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(B2BSearchOrganizationResponse) -> Unit>()
        impl.searchOrganization(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `SearchManager searchMembers delegates to the api`() =
        runBlocking {
            coEvery { mockApi.searchMembers(any(), any()) } returns mockk(relaxed = true)
            impl.searchMember(mockk(relaxed = true))
            coVerify { mockApi.searchMembers(any(), any()) }
        }

    @Test
    fun `SearchManager searchMembers with callback calls callback`() {
        coEvery { mockApi.searchMembers(any(), any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(B2BSearchMemberResponse) -> Unit>()
        impl.searchMember(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}

package com.stytch.sdk.b2b.member

import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.MemberData
import com.stytch.sdk.b2b.network.models.MemberResponseData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
internal class MemberImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.Member

    @MockK
    private lateinit var mockSessionStorage: B2BSessionStorage

    private lateinit var impl: MemberImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulMemberResponse = StytchResult.Success<MemberResponseData>(mockk(relaxed = true))

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        impl = MemberImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            sessionStorage = mockSessionStorage,
            api = mockApi
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `Member get delegates to api`() = runTest {
        coEvery { mockApi.getMember() } returns successfulMemberResponse
        val response = impl.get()
        assert(response is StytchResult.Success)
        coVerify { mockApi.getMember() }
    }

    @Test
    fun `Member get with callback calls callback method`() {
        coEvery { mockApi.getMember() } returns successfulMemberResponse
        val mockCallback = spyk<(MemberResponse) -> Unit>()
        impl.get(mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `Member getSync delegates to sessionStorage`() {
        val mockMember: MemberData = mockk()
        every { mockSessionStorage.member } returns mockMember
        val member = impl.getSync()
        assert(member == mockMember)
        verify { mockSessionStorage.member }
    }
}

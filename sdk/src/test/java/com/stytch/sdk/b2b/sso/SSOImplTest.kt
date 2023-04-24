package com.stytch.sdk.b2b.sso

import com.stytch.sdk.b2b.SSOAuthenticateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.SSOAuthenticateResponseData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
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
import java.security.KeyStore

@OptIn(ExperimentalCoroutinesApi::class)
internal class SSOImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.SSO

    @MockK
    private lateinit var mockB2BSessionStorage: B2BSessionStorage

    @MockK
    private lateinit var mockStorageHelper: StorageHelper

    private lateinit var impl: SSOImpl
    private val dispatcher = Dispatchers.Unconfined

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.b2b.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        impl = SSOImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            sessionStorage = mockB2BSessionStorage,
            storageHelper = mockStorageHelper,
            api = mockApi,
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `SSO authenticate returns error if codeverifier fails`() = runTest {
        every { mockStorageHelper.loadValue(any()) } returns null
        val response = impl.authenticate(mockk(relaxed = true))
        assert(response is StytchResult.Error)
    }

    @Test
    fun `SSO authenticate delegates to api`() = runTest {
        every { mockStorageHelper.retrieveCodeVerifier() } returns ""
        val mockResponse = StytchResult.Success<SSOAuthenticateResponseData>(mockk(relaxed = true))
        coEvery { mockApi.authenticate(any(), any(), any()) } returns mockResponse
        val response = impl.authenticate(SSO.AuthenticateParams(""))
        assert(response is StytchResult.Success)
        coVerify { mockApi.authenticate(any(), any(), any()) }
        verify { mockResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `SSO authenticate with callback calls callback method`() {
        val mockCallback = spyk<(SSOAuthenticateResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}

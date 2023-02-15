package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.b2b.network.B2BSessionData
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.StorageHelper
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import java.security.KeyStore
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class B2BSessionStorageTest {
    @MockK
    private lateinit var mockStorageHelper: StorageHelper

    private lateinit var storage: B2BSessionStorage

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true)
        storage = B2BSessionStorage(mockStorageHelper)
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `SessionStorage sessionToken getter delegates to storageHelper`() {
        every { mockStorageHelper.loadValue(any()) } returns "mockedSessionToken"
        val sessionToken = storage.sessionToken
        verify { mockStorageHelper.loadValue(eq(Constants.PREFERENCES_NAME_SESSION_TOKEN)) }
        assert(sessionToken == "mockedSessionToken")
    }

    @Test
    fun `SessionStorage sessionJwt getter delegates to storageHelper`() {
        every { mockStorageHelper.loadValue(any()) } returns "mockedSessionJwt"
        val sessionJwt = storage.sessionJwt
        verify { mockStorageHelper.loadValue(eq(Constants.PREFERENCES_NAME_SESSION_JWT)) }
        assert(sessionJwt == "mockedSessionJwt")
    }

    @Test
    fun `SessionStorage setters delegate to storageHelper`() {
        every { mockStorageHelper.saveValue(any(), any()) } just runs
        storage.updateSession(
            sessionToken = "mySessionToken",
            sessionJwt = "mySessionJwt"
        )
        verify {
            mockStorageHelper.saveValue(eq(Constants.PREFERENCES_NAME_SESSION_TOKEN), eq("mySessionToken"))
            mockStorageHelper.saveValue(eq(Constants.PREFERENCES_NAME_SESSION_JWT), eq("mySessionJwt"))
        }
    }

    @Test
    fun `SessionStorage updateSession correctly updates sessionData`() {
        // we're not testing sessiontoken or sessionjwt here, because the getters/setters are already tested above
        every { mockStorageHelper.saveValue(any(), any()) } just runs
        val mockedSessionData = mockk<B2BSessionData>(relaxed = true)
        storage.updateSession(
            sessionToken = "mySessionToken",
            sessionJwt = "mySessionJwt",
            session = mockedSessionData,
        )
        assert(storage.session == mockedSessionData)
    }

    @Test
    fun `SessionStorage revoke properly nulls out session data`() {
        // we're not testing sessiontoken or sessionjwt here, because the getters/setters are already tested above
        every { mockStorageHelper.saveValue(any(), any()) } just runs
        storage.revoke()
        assert(storage.session == null)
        assert(storage.member == null)
    }
}

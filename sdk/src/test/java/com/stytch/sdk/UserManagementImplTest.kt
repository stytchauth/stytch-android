package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.responseData.UserData
import com.stytch.sessions.SessionAutoUpdater
import com.stytch.sessions.SessionStorage
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
import java.security.KeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class UserManagementImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.UserManagement

    @MockK
    private lateinit var mockSessionStorage: SessionStorage

    private lateinit var impl: UserManagementImpl
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
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any()) } just runs
        impl = UserManagementImpl(
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
    fun `UserManagementImpl getUser delegates to api`() = runTest {
        coEvery { mockApi.getUser() } returns StytchResult.Success(mockk(relaxed = true))
        val response = impl.getUser()
        assert(response is StytchResult.Success)
        coVerify { mockApi.getUser() }
    }

    @Test
    fun `UserManagementImpl getUser with callback calls callback method`() {
        coEvery { mockApi.getUser() } returns StytchResult.Success(mockk(relaxed = true))
        val mockCallback = spyk<(UserResponse) -> Unit>()
        impl.getUser(mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    // getSyncUser delegates to sessionStorage
    @Test
    fun `UserManagementImpl getSyncUser delegates to sessionStorage`() {
        val mockUser: UserData = mockk()
        every { mockSessionStorage.user } returns mockUser
        val user = impl.getSyncUser()
        assert(user == mockUser)
        verify { mockSessionStorage.user }
    }

    @Test
    fun `UserManagementImpl deleteEmailById delegates to api`() = runTest {
        coEvery { mockApi.deleteEmailById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        val response = impl.deleteEmailById("emailAddressId")
        assert(response is StytchResult.Success)
        coVerify { mockApi.deleteEmailById("emailAddressId") }
    }

    @Test
    fun `UserManagementImpl deleteEmailById with callback calls callback method`() {
        coEvery { mockApi.deleteEmailById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.deleteEmailById("emailAddressId", mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `UserManagementImpl deletePhoneNumberById delegates to api`() = runTest {
        coEvery { mockApi.deletePhoneNumberById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        val response = impl.deletePhoneNumberById("phoneNumberId")
        assert(response is StytchResult.Success)
        coVerify { mockApi.deletePhoneNumberById("phoneNumberId") }
    }

    @Test
    fun `UserManagementImpl deletePhoneNumberById with callback calls callback method`() {
        coEvery { mockApi.deletePhoneNumberById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.deletePhoneNumberById("phoneNumberId", mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `UserManagementImpl deleteBiometricRegistrationById delegates to api`() = runTest {
        coEvery { mockApi.deleteBiometricRegistrationById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        val response = impl.deleteBiometricRegistrationById("biometricsRegistrationId")
        assert(response is StytchResult.Success)
        coVerify { mockApi.deleteBiometricRegistrationById("biometricsRegistrationId") }
    }

    @Test
    fun `UserManagementImpl deleteBiometricRegistrationById with callback calls callback method`() {
        coEvery { mockApi.deleteBiometricRegistrationById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.deleteBiometricRegistrationById("biometricsRegistrationId", mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}

package com.stytch.sdk.userManagement

import com.stytch.sdk.DeleteFactorResponse
import com.stytch.sdk.EncryptionManager
import com.stytch.sdk.StorageHelper
import com.stytch.sdk.StytchDispatchers
import com.stytch.sdk.StytchResult
import com.stytch.sdk.UserResponse
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.responseData.UserData
import com.stytch.sdk.sessions.SessionAutoUpdater
import com.stytch.sdk.sessions.SessionStorage
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
        coEvery { mockApi.getUser() } returns StytchResult.Success(mockk(relaxed = true))
        every { mockSessionStorage.user = any() } just runs
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
    fun `UserManagementImpl deleteFactor with callback calls callback method`() {
        coEvery { mockApi.deleteEmailById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        val mockCallback = spyk<(DeleteFactorResponse) -> Unit>()
        impl.deleteFactor(AuthenticationFactor.Email("emailAddressId"), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `UserManagementImpl deleteFactor delegates to api for all supported factors`() = runTest {
        coEvery { mockApi.deleteEmailById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        coEvery { mockApi.deletePhoneNumberById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        coEvery { mockApi.deleteBiometricRegistrationById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        coEvery { mockApi.deleteCryptoWalletById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        coEvery { mockApi.deleteWebAuthnById(any()) } returns StytchResult.Success(mockk(relaxed = true))
        listOf(
            AuthenticationFactor.Email("emailAddressId"),
            AuthenticationFactor.PhoneNumber("phoneNumberId"),
            AuthenticationFactor.BiometricRegistration("biometricsRegistrationId"),
            AuthenticationFactor.CryptoWallet("cryptoWalletId"),
            AuthenticationFactor.WebAuthn("webAuthnId"),
        ).forEach { impl.deleteFactor(it) }
        coVerify { mockApi.deleteEmailById("emailAddressId") }
        coVerify { mockApi.deletePhoneNumberById("phoneNumberId") }
        coVerify { mockApi.deleteBiometricRegistrationById("biometricsRegistrationId") }
        coVerify { mockApi.deleteCryptoWalletById("cryptoWalletId") }
        coVerify { mockApi.deleteWebAuthnById("webAuthnId") }
    }
}
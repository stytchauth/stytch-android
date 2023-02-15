package com.stytch.sdk.b2b.network

import android.content.Context
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.StytchDataResponse
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.security.KeyStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

@OptIn(ExperimentalCoroutinesApi::class)
internal class StytchB2BApiTest {
    var mContextMock = mockk<Context>(relaxed = true)

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        mockkObject(StytchB2BApi)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `StytchB2BApi isInitialized returns correctly based on configuration state`() {
        StytchB2BApi.configure("publicToken", DeviceInfo())
        assert(StytchB2BApi.isInitialized)
    }

    @Test(expected = IllegalStateException::class)
    fun `StytchB2BApi apiService throws exception when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BApi.apiService
    }

    @Test
    fun `StytchB2BApi apiService is available when configured`() {
        StytchB2BClient.configure(mContextMock, "")
        StytchB2BApi.apiService
    }

    // TODO every method calls safeApi

    @Test
    fun `StytchB2BApi MagicLinks Email loginOrCreate calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.loginOrCreateUserByEmail(any()) } returns mockk(relaxed = true)
        StytchB2BApi.MagicLinks.Email.loginOrCreate("", "", "", "", "", "", "")
        coVerify { StytchB2BApi.apiService.loginOrCreateUserByEmail(any()) }
    }

    @Test
    fun `StytchB2BApi MagicLinks Email authenticate calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.authenticate(any()) } returns mockk(relaxed = true)
        StytchB2BApi.MagicLinks.Email.authenticate("", 30U, "")
        coVerify { StytchB2BApi.apiService.authenticate(any()) }
    }

    @Test
    fun `StytchB2BApi Sessions authenticate calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.authenticateSessions(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Sessions.authenticate(30U)
        coVerify { StytchB2BApi.apiService.authenticateSessions(any()) }
    }

    @Test
    fun `StytchB2BApi Sessions revoke calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.revokeSessions() } returns mockk(relaxed = true)
        StytchB2BApi.Sessions.revoke()
        coVerify { StytchB2BApi.apiService.revokeSessions() }
    }

    @Test
    fun `StytchB2BApi Organizations getOrganization calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.getOrganization() } returns mockk(relaxed = true)
        StytchB2BApi.Organizations.getOrganization()
        coVerify { StytchB2BApi.apiService.getOrganization() }
    }

    @Test
    fun `StytchB2BApi Member get calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.getMember() } returns mockk(relaxed = true)
        StytchB2BApi.Member.getMember()
        coVerify { StytchB2BApi.apiService.getMember() }
    }

    @Test(expected = IllegalStateException::class)
    fun `safeApiCall throws exception when StytchB2BClient is not initialized`() = runTest {
        every { StytchB2BApi.isInitialized } returns false
        val mockApiCall: suspend () -> StytchDataResponse<Boolean> = mockk()
        StytchB2BApi.safeB2BApiCall { mockApiCall() }
    }

    @Test
    fun `safeApiCall returns success when call succeeds`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            return StytchDataResponse(true)
        }
        val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `safeApiCall returns correct error for HttpException`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            throw HttpException(mockk(relaxed = true))
        }
        val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
        assert(result is StytchResult.Error)
    }

    @Test
    fun `safeApiCall returns correct error for StytchExceptions`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            throw StytchExceptions.Critical(RuntimeException("Test"))
        }
        val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
        assert(result is StytchResult.Error)
    }

    @Test
    fun `safeApiCall returns correct error for other exceptions`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            error("Test")
        }
        val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
        assert(result is StytchResult.Error)
    }
}

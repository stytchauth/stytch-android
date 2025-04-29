package com.stytch.sdk.b2b.member

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.stytch.sdk.b2b.DeleteMemberAuthenticationFactorResponse
import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.UpdateMemberResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.MemberData
import com.stytch.sdk.b2b.network.models.MemberResponseData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.PREFERENCES_NAME_LAST_VALIDATED_AT
import com.stytch.sdk.common.PREFERENCES_NAME_MEMBER_DATA
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class MemberImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.Member

    @MockK
    private lateinit var mockSharedPreferences: SharedPreferences

    @MockK
    private lateinit var mockSharedPreferencesEditor: Editor

    private lateinit var spiedSessionStorage: B2BSessionStorage

    private lateinit var impl: MemberImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulMemberResponse = StytchResult.Success<MemberResponseData>(mockk(relaxed = true))

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any()) } returns Unit
        every { EncryptionManager.encryptString(any()) } returns ""
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true)
        every { mockSharedPreferences.edit() } returns mockSharedPreferencesEditor
        every { mockSharedPreferencesEditor.putString(any(), any()) } returns mockSharedPreferencesEditor
        every { mockSharedPreferencesEditor.putLong(any(), any()) } returns mockSharedPreferencesEditor
        every { mockSharedPreferencesEditor.apply() } just runs
        every { mockSharedPreferences.getLong(any(), any()) } returns 0
        StorageHelper.sharedPreferences = mockSharedPreferences
        spiedSessionStorage = spyk(B2BSessionStorage(StorageHelper), recordPrivateCalls = true)
        impl =
            MemberImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = spiedSessionStorage,
                api = mockApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `Member get delegates to api and caches the result`() =
        runBlocking {
            coEvery { mockApi.getMember() } returns successfulMemberResponse
            every { mockSharedPreferencesEditor.putString(any(), any()) } returns mockSharedPreferencesEditor
            every { mockSharedPreferencesEditor.putLong(any(), any()) } returns mockSharedPreferencesEditor
            val response = impl.get()
            assert(response is StytchResult.Success)
            coVerify { mockApi.getMember() }
            verify { mockSharedPreferencesEditor.putString(PREFERENCES_NAME_MEMBER_DATA, any()) }
            verify { mockSharedPreferencesEditor.putLong(PREFERENCES_NAME_LAST_VALIDATED_AT, any()) }
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
        every { spiedSessionStorage.member } returns mockMember
        val member = impl.getSync()
        assert(member == mockMember)
        verify { spiedSessionStorage.member }
    }

    @Test
    fun `Member update delegates to api`() =
        runBlocking {
            coEvery { mockApi.updateMember(any(), any(), any(), any(), any()) } returns mockk(relaxed = true)
            impl.update(mockk(relaxed = true))
            coVerify { mockApi.updateMember(any(), any(), any(), any(), any()) }
        }

    @Test
    fun `Member deleteFactor with callback calls callback method`() {
        coEvery { mockApi.deleteMFAPhoneNumber() } returns StytchResult.Success(mockk(relaxed = true))
        val mockCallback = spyk<(DeleteMemberAuthenticationFactorResponse) -> Unit>()
        impl.deleteFactor(MemberAuthenticationFactor.MfaPhoneNumber, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `Member deleteFactor delegates to api for all supported factors`() =
        runBlocking {
            coEvery { mockApi.deleteMFAPhoneNumber() } returns StytchResult.Success(mockk(relaxed = true))
            coEvery { mockApi.deleteMFATOTP() } returns StytchResult.Success(mockk(relaxed = true))
            coEvery { mockApi.deletePassword(any()) } returns StytchResult.Success(mockk(relaxed = true))
            listOf(
                MemberAuthenticationFactor.MfaPhoneNumber,
                MemberAuthenticationFactor.MfaTOTP,
                MemberAuthenticationFactor.Password("passwordId"),
            ).forEach { impl.deleteFactor(it) }
            coVerify { mockApi.deleteMFAPhoneNumber() }
            coVerify { mockApi.deleteMFATOTP() }
            coVerify { mockApi.deletePassword("passwordId") }
        }

    @Test
    fun `Member update with callback calls callback method`() {
        coEvery { mockApi.updateMember(any(), any(), any(), any(), any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(UpdateMemberResponse) -> Unit>()
        impl.update(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}

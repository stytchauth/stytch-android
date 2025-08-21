package com.stytch.sdk.consumer.sessions

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.stytch.sdk.common.PREFERENCES_NAME_LAST_AUTHENTICATED_USER_ID
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_JWT
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_TOKEN
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.errors.StytchNoCurrentSessionError
import com.stytch.sdk.consumer.biometrics.LAST_USED_BIOMETRIC_REGISTRATION_ID
import com.stytch.sdk.consumer.extensions.keepLocalBiometricRegistrationsInSync
import com.stytch.sdk.consumer.network.models.UserData
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class ConsumerSessionStorageTest {
    private lateinit var impl: ConsumerSessionStorage

    @MockK
    private lateinit var mockSharedPreferences: SharedPreferences

    @MockK
    private lateinit var mockSharedPreferencesEditor: Editor

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic(KeyStore::class)
        mockkStatic("com.stytch.sdk.consumer.extensions.UserDataExtKt")
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        every { mockSharedPreferences.edit() } returns mockSharedPreferencesEditor
        every { mockSharedPreferencesEditor.putString(any(), any()) } returns mockSharedPreferencesEditor
        every { mockSharedPreferencesEditor.putLong(any(), any()) } returns mockSharedPreferencesEditor
        every { mockSharedPreferences.getLong(any(), any()) } returns 0L
        every { mockSharedPreferencesEditor.apply() } just runs
        StorageHelper.sharedPreferences = mockSharedPreferences
        every { StorageHelper.loadValue(any()) } returns null
        every { StorageHelper.saveValue(any(), any()) } just runs
        impl = ConsumerSessionStorage(StorageHelper)
    }

    @Test(expected = StytchNoCurrentSessionError::class)
    fun `ensureSessionIsValidOrThrow throws StytchNoCurrentSessionError if missing sessionToken and sessionJwt`() {
        impl.ensureSessionIsValidOrThrow()
    }

    @Test
    fun `ensureSessionIsValidOrThrow doesn't throw StytchNoCurrentSessionError if either exists`() {
        every { StorageHelper.loadValue(PREFERENCES_NAME_SESSION_TOKEN) } returns "sessionToken"
        every { StorageHelper.loadValue(PREFERENCES_NAME_SESSION_JWT) } returns null
        impl.ensureSessionIsValidOrThrow()
        every { StorageHelper.loadValue(PREFERENCES_NAME_SESSION_TOKEN) } returns null
        every { StorageHelper.loadValue(PREFERENCES_NAME_SESSION_JWT) } returns "sessionJwt"
        impl.ensureSessionIsValidOrThrow()
        every { StorageHelper.loadValue(PREFERENCES_NAME_SESSION_TOKEN) } returns "sessionToken"
        every { StorageHelper.loadValue(PREFERENCES_NAME_SESSION_JWT) } returns "sessionJwt"
        impl.ensureSessionIsValidOrThrow()
    }

    @Test
    fun `setting a user keeps biometric registrations in check when there is no previous user id`() =
        runBlocking {
            every { StorageHelper.loadValue(PREFERENCES_NAME_LAST_AUTHENTICATED_USER_ID) } returns null
            val mockUserData: UserData =
                mockk(relaxed = true) {
                    every { userId } returns "current-user-id"
                    every { keepLocalBiometricRegistrationsInSync(any()) } just runs
                }
            impl.user = mockUserData
            verify(exactly = 1) { mockUserData.keepLocalBiometricRegistrationsInSync(any()) }
        }

    @Test
    fun `setting a user keeps biometric registrations in check when the same user is logging in as before`() =
        runBlocking {
            every { StorageHelper.loadValue(PREFERENCES_NAME_LAST_AUTHENTICATED_USER_ID) } returns "current-user-id"
            val mockUserData: UserData =
                mockk(relaxed = true) {
                    every { userId } returns "current-user-id"
                    every { keepLocalBiometricRegistrationsInSync(any()) } just runs
                }
            // only local biometric registrations are cleaned up, and no pending deletes are processed
            impl.user = mockUserData
            verify(exactly = 1) { mockUserData.keepLocalBiometricRegistrationsInSync(any()) }
            verify(exactly = 0) { StorageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID) }
        }

    @Test
    fun `setting a user keeps biometric registrations in check when a new user is logging in with nothing to update`() =
        runBlocking {
            every { StorageHelper.loadValue(PREFERENCES_NAME_LAST_AUTHENTICATED_USER_ID) } returns "previous-user-id"
            val mockUserData: UserData =
                mockk(relaxed = true) {
                    every { userId } returns "current-user-id"
                    every { keepLocalBiometricRegistrationsInSync(any()) } just runs
                }

            // if no pending deletes and no previous registrations, nothing is processed
            every { StorageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID) } returns null
            impl.user = mockUserData
            verify(exactly = 0) { mockUserData.keepLocalBiometricRegistrationsInSync(any()) }
            verify(exactly = 1) { StorageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID) }
            verify(exactly = 0) { StorageHelper.deleteAllBiometricsKeys() }
        }

    @Test
    fun `setting a user keeps biometric registrations in check when a new user is logging in with stuff to update`() =
        runBlocking {
            every { StorageHelper.loadValue(PREFERENCES_NAME_LAST_AUTHENTICATED_USER_ID) } returns "previous-user-id"
            every { StorageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID) } returns "previous-registration-id"
            val mockUserData: UserData =
                mockk(relaxed = true) {
                    every { userId } returns "current-user-id"
                    every { keepLocalBiometricRegistrationsInSync(any()) } just runs
                }
            // if there is a pending delete and a previous registration, everything is processed
            impl.user = mockUserData
            verify(exactly = 1) { StorageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID) }
            verify(exactly = 1) { StorageHelper.deleteAllBiometricsKeys() }
        }
}

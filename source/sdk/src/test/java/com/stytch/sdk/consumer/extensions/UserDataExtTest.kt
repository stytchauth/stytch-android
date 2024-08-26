package com.stytch.sdk.consumer.extensions

import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.network.models.BiometricRegistrations
import com.stytch.sdk.consumer.biometrics.KEYS_REQUIRED_FOR_REGISTRATION
import com.stytch.sdk.consumer.biometrics.LAST_USED_BIOMETRIC_REGISTRATION_ID
import com.stytch.sdk.consumer.network.models.UserData
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

private const val MOCK_REGISTRATION_ID = "biometric-registration-uuid"

internal class UserDataExtTest {
    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
    }

    @Test
    fun `keepLocalBiometricRegistrationsInSync does what it's supposed to`() {
        // No local registration, does nothing
        every { StorageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID) } returns null
        val mockUser: UserData = mockk(relaxed = true)
        mockUser.keepLocalBiometricRegistrationsInSync(StorageHelper)
        verify(exactly = 0) { StorageHelper.deletePreference(any()) }
        verify(exactly = 0) { StorageHelper.keyStore.deleteEntry(any()) }

        // local registration that exists on the user, does nothing
        every { StorageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID) } returns MOCK_REGISTRATION_ID
        every { mockUser.biometricRegistrations } returns listOf(BiometricRegistrations(MOCK_REGISTRATION_ID, true))
        mockUser.keepLocalBiometricRegistrationsInSync(StorageHelper)
        verify(exactly = 0) { StorageHelper.deletePreference(any()) }
        verify(exactly = 0) { StorageHelper.keyStore.deleteEntry(any()) }

        // local registration that doesn't exist on the user, deletes local
        every { StorageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID) } returns MOCK_REGISTRATION_ID
        every { mockUser.biometricRegistrations } returns emptyList()
        val mockKeystore: KeyStore =
            mockk(relaxed = true) {
                every { deleteEntry(any()) } just runs
            }
        every { StorageHelper.keyStore } returns mockKeystore
        mockUser.keepLocalBiometricRegistrationsInSync(StorageHelper)
        verify(exactly = KEYS_REQUIRED_FOR_REGISTRATION.size) { StorageHelper.deletePreference(any()) }
        verify(exactly = 1) { mockKeystore.deleteEntry(any()) }
    }
}

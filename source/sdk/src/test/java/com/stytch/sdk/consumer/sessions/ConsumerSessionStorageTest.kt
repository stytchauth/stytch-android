package com.stytch.sdk.consumer.sessions

import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_JWT
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_TOKEN
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.errors.StytchNoCurrentSessionError
import com.stytch.sdk.consumer.extensions.keepLocalBiometricRegistrationsInSync
import com.stytch.sdk.consumer.network.models.UserData
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.TestScope
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class ConsumerSessionStorageTest {
    private lateinit var impl: ConsumerSessionStorage

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkStatic("com.stytch.sdk.consumer.extensions.UserDataExtKt")
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        every { StorageHelper.loadValue(any()) } returns null
        every { StorageHelper.saveValue(any(), any()) } just runs
        impl = ConsumerSessionStorage(StorageHelper, TestScope())
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
    fun `setting a user keeps local biometric registrations in check`() {
        val mockUserData: UserData =
            mockk(relaxed = true) {
                every { keepLocalBiometricRegistrationsInSync(any()) } just runs
            }
        impl.user = mockUserData
        verify { mockUserData.keepLocalBiometricRegistrationsInSync(any()) }
    }
}

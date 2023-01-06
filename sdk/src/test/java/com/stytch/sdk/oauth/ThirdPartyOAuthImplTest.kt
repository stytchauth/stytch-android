package com.stytch.sdk.oauth

import android.app.Activity
import com.stytch.sdk.StorageHelper
import com.stytch.sdk.network.StytchApi
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import java.security.KeyStore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Suppress("MaxLineLength")
internal class ThirdPartyOAuthImplTest {
    @MockK
    private lateinit var mockStorageHelper: StorageHelper

    private lateinit var impl: ThirdPartyOAuthImpl

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StytchApi)
        every { StytchApi.publicToken } returns "public-token-test-1234"
        MockKAnnotations.init(this, true, true)
        impl = ThirdPartyOAuthImpl(
            mockStorageHelper,
            providerName = "testing"
        )
    }

    @Test
    fun `buildUri builds the correct URI`() {
        val uri = impl.buildUri(
            host = "https://host/",
            parameters = mapOf(
                "login_redirect_url" to "https://host/login",
                "signup_redirect_url" to "https://host/signup",
            ),
            pkce = "pkce",
            token = "token"
        )
        val expected = "https://host/public/oauth/testing/start?code_challenge=pkce&public_token=token&login_redirect_url=https%3A%2F%2Fhost%2Flogin&signup_redirect_url=https%3A%2F%2Fhost%2Fsignup" // ktlint-disable maximum-line-length
        assert(uri.toString() == expected)
    }

    @Test
    fun `start starts activity for result`() {
        val mockActivity: Activity = mockk(relaxed = true) {
            every { startActivityForResult(any(), any()) } just runs
        }
        every { mockStorageHelper.generateHashedCodeChallenge() } returns Pair("S256", "hashedcodechallenge")
        impl.start(OAuth.ThirdParty.StartParameters(mockActivity, 1234))
        verify { mockActivity.startActivityForResult(any(), 1234) }
    }
}

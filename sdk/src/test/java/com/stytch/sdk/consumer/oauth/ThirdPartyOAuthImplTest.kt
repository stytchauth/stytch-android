@file:Suppress("MaxLineLength", "ktlint:standard:max-line-length")

package com.stytch.sdk.consumer.oauth

import android.app.Activity
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.consumer.network.StytchApi
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.security.KeyStore

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
        impl =
            ThirdPartyOAuthImpl(
                mockStorageHelper,
                providerName = "testing",
            )
    }

    @Ignore("Issues with Robolectric on GitHub")
    @Test
    fun `buildUri builds the correct URI`() {
        val uri =
            impl.buildUri(
                host = "https://host/",
                parameters =
                    mapOf(
                        "login_redirect_url" to "https://host/login",
                        "signup_redirect_url" to "https://host/signup",
                    ),
                pkce = "pkce",
                token = "token",
            )
        val expected = "https://host/public/oauth/testing/start?code_challenge=pkce&public_token=token&login_redirect_url=https%3A%2F%2Fhost%2Flogin&signup_redirect_url=https%3A%2F%2Fhost%2Fsignup"
        assert(uri.toString() == expected)
    }

    @Ignore("Issues with Robolectric on GitHub")
    @Test
    fun `start starts activity for result`() {
        val mockActivity: Activity =
            mockk(relaxed = true) {
                every { startActivityForResult(any(), any()) } just runs
            }
        every { mockStorageHelper.generateHashedCodeChallenge() } returns Pair("S256", "hashedcodechallenge")
        impl.start(OAuth.ThirdParty.StartParameters(mockActivity, 1234))
        verify { mockActivity.startActivityForResult(any(), 1234) }
    }
}

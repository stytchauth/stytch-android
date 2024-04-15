package com.stytch.sdk.b2b.sso

import com.stytch.sdk.b2b.B2BSSODeleteConnectionResponse
import com.stytch.sdk.b2b.B2BSSOGetConnectionsResponse
import com.stytch.sdk.b2b.B2BSSOOIDCCreateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOOIDCUpdateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOSAMLCreateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOSAMLDeleteVerificationCertificateResponse
import com.stytch.sdk.b2b.B2BSSOSAMLUpdateConnectionByURLResponse
import com.stytch.sdk.b2b.B2BSSOSAMLUpdateConnectionResponse
import com.stytch.sdk.b2b.SSOAuthenticateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.SSOAuthenticateResponseData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sessions.SessionAutoUpdater
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

@OptIn(ExperimentalCoroutinesApi::class)
internal class SSOImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.SSO

    @MockK
    private lateinit var mockB2BSessionStorage: B2BSessionStorage

    @MockK
    private lateinit var mockStorageHelper: StorageHelper

    private lateinit var impl: SSOImpl
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
        mockkStatic("com.stytch.sdk.b2b.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        impl =
            SSOImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockB2BSessionStorage,
                storageHelper = mockStorageHelper,
                api = mockApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `SSO authenticate returns error if codeverifier fails`() =
        runTest {
            every { mockStorageHelper.loadValue(any()) } returns null
            val response = impl.authenticate(mockk(relaxed = true))
            assert(response is StytchResult.Error)
        }

    @Test
    fun `SSO authenticate delegates to api`() =
        runTest {
            every { mockStorageHelper.retrieveCodeVerifier() } returns ""
            val mockResponse = StytchResult.Success<SSOAuthenticateResponseData>(mockk(relaxed = true))
            coEvery { mockApi.authenticate(any(), any(), any()) } returns mockResponse
            val response = impl.authenticate(SSO.AuthenticateParams(""))
            assert(response is StytchResult.Success)
            coVerify { mockApi.authenticate(any(), any(), any()) }
            verify { mockResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `SSO authenticate with callback calls callback method`() {
        val mockCallback = spyk<(SSOAuthenticateResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `SSO getConnections delegates to api`() =
        runTest {
            coEvery { mockApi.getConnections() } returns mockk(relaxed = true)
            impl.getConnections()
            coVerify { mockApi.getConnections() }
        }

    @Test
    fun `SSO getconnections with callback calls callback method`() {
        coEvery { mockApi.getConnections() } returns mockk(relaxed = true)
        val mockCallback = spyk<(B2BSSOGetConnectionsResponse) -> Unit>()
        impl.getConnections(mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `SSO deleteConnection delegates to api`() =
        runTest {
            coEvery { mockApi.deleteConnection(any()) } returns mockk(relaxed = true)
            val connectionId = "my-connection-id"
            impl.deleteConnection(connectionId)
            coVerify { mockApi.deleteConnection(connectionId) }
        }

    @Test
    fun `SSO deleteConnection with callback calls callback method`() {
        coEvery { mockApi.deleteConnection(any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(B2BSSODeleteConnectionResponse) -> Unit>()
        impl.deleteConnection("", mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `SSO saml create delegates to api`() =
        runTest {
            coEvery { mockApi.samlCreateConnection(any()) } returns mockk(relaxed = true)
            val parameters = SSO.SAML.CreateParameters(displayName = "my cool display name")
            impl.saml.createConnection(parameters)
            coVerify { mockApi.samlCreateConnection(displayName = parameters.displayName) }
        }

    @Test
    fun `SSO saml create with callback calls callback method`() {
        coEvery { mockApi.samlCreateConnection(any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(B2BSSOSAMLCreateConnectionResponse) -> Unit>()
        impl.saml.createConnection(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `SSO saml update delegates to api`() =
        runTest {
            coEvery { mockApi.samlUpdateConnection(any()) } returns mockk(relaxed = true)
            val parameters = SSO.SAML.UpdateParameters(connectionId = "connection-id")
            impl.saml.updateConnection(parameters)
            coVerify { mockApi.samlUpdateConnection(connectionId = parameters.connectionId) }
        }

    @Test
    fun `SSO saml update with callback calls callback method`() {
        coEvery { mockApi.samlUpdateConnection(any()) } returns mockk(relaxed = true)
        val parameters = SSO.SAML.UpdateParameters(connectionId = "connection-id")
        val mockCallback = spyk<(B2BSSOSAMLUpdateConnectionResponse) -> Unit>()
        impl.saml.updateConnection(parameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `SSO saml updateConnectionByUrl delegates to api`() =
        runTest {
            coEvery { mockApi.samlUpdateByUrl(any(), any()) } returns mockk(relaxed = true)
            val parameters =
                SSO.SAML.UpdateByURLParameters(
                    connectionId = "connection-id",
                    metadataUrl = "metadata.url",
                )
            impl.saml.updateConnectionByUrl(parameters)
            coVerify {
                mockApi.samlUpdateByUrl(
                    connectionId = parameters.connectionId,
                    metadataUrl = parameters.metadataUrl,
                )
            }
        }

    @Test
    fun `SSO saml updateByUrl with callback calls callback method`() {
        coEvery { mockApi.samlUpdateByUrl(any(), any()) } returns mockk(relaxed = true)
        val parameters = SSO.SAML.UpdateByURLParameters(connectionId = "connection-id", metadataUrl = "metadata.url")
        val mockCallback = spyk<(B2BSSOSAMLUpdateConnectionByURLResponse) -> Unit>()
        impl.saml.updateConnectionByUrl(parameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `SSO saml deleteVerificationCertificate delegates to api`() =
        runTest {
            coEvery { mockApi.samlDeleteVerificationCertificate(any(), any()) } returns mockk(relaxed = true)
            val parameters =
                SSO.SAML.DeleteVerificationCertificateParameters(
                    connectionId = "connection-id",
                    certificateId = "certificate-id",
                )
            impl.saml.deleteVerificationCertificate(parameters)
            coVerify {
                mockApi.samlDeleteVerificationCertificate(
                    connectionId = parameters.connectionId,
                    certificateId = parameters.certificateId,
                )
            }
        }

    @Test
    fun `SSO saml deleteVerificationCertificate with callback calls callback method`() {
        coEvery { mockApi.samlDeleteVerificationCertificate(any(), any()) } returns mockk(relaxed = true)
        val parameters =
            SSO.SAML.DeleteVerificationCertificateParameters(
                connectionId = "connection-id",
                certificateId = "certificate-id",
            )
        val mockCallback = spyk<(B2BSSOSAMLDeleteVerificationCertificateResponse) -> Unit>()
        impl.saml.deleteVerificationCertificate(parameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `SSO oidc create delegates to api`() =
        runTest {
            coEvery { mockApi.oidcCreateConnection(any()) } returns mockk(relaxed = true)
            val parameters = SSO.OIDC.CreateParameters(displayName = "my cool display name")
            impl.oidc.createConnection(parameters)
            coVerify { mockApi.oidcCreateConnection(displayName = parameters.displayName) }
        }

    @Test
    fun `SSO oidc create with callback calls callback method`() {
        coEvery { mockApi.oidcCreateConnection(any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(B2BSSOOIDCCreateConnectionResponse) -> Unit>()
        impl.oidc.createConnection(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `SSO oidc update delegates to api`() =
        runTest {
            coEvery { mockApi.oidcUpdateConnection(any()) } returns mockk(relaxed = true)
            val parameters = SSO.OIDC.UpdateParameters(connectionId = "connection-id")
            impl.oidc.updateConnection(parameters)
            coVerify { mockApi.oidcUpdateConnection(connectionId = parameters.connectionId) }
        }

    @Test
    fun `SSO oidc update with callback calls callback method`() {
        coEvery { mockApi.oidcUpdateConnection(any()) } returns mockk(relaxed = true)
        val parameters = SSO.OIDC.UpdateParameters(connectionId = "connection-id")
        val mockCallback = spyk<(B2BSSOOIDCUpdateConnectionResponse) -> Unit>()
        impl.oidc.updateConnection(parameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}

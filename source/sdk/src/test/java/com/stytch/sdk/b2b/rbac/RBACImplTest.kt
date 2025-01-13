package com.stytch.sdk.b2b.rbac

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.network.models.RBACPermission
import com.stytch.sdk.common.network.models.RBACPolicy
import com.stytch.sdk.common.network.models.RBACPolicyResource
import com.stytch.sdk.common.network.models.RBACPolicyRole
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
import java.util.Date

private val MOCK_RBAC_POLICY =
    RBACPolicy(
        roles =
            listOf(
                RBACPolicyRole(
                    roleId = "default",
                    description = "",
                    permissions =
                        listOf(
                            RBACPermission(
                                resourceId = "documents",
                                actions = listOf("read"),
                            ),
                        ),
                ),
                RBACPolicyRole(
                    roleId = "organization_admin",
                    description = "",
                    permissions =
                        listOf(
                            RBACPermission(
                                resourceId = "documents",
                                actions = listOf("*"),
                            ),
                        ),
                ),
            ),
        resources =
            listOf(
                RBACPolicyResource(
                    resourceId = "documents",
                    description = "",
                    actions = listOf("create", "read", "write", "delete"),
                ),
            ),
    )

private val MOCK_RBAC_POLICY_WITHOUT_DEFAULT_ROLE =
    RBACPolicy(
        roles =
            listOf(
                RBACPolicyRole(
                    roleId = "organization_admin",
                    description = "",
                    permissions =
                        listOf(
                            RBACPermission(
                                resourceId = "documents",
                                actions = listOf("*"),
                            ),
                        ),
                ),
            ),
        resources =
            listOf(
                RBACPolicyResource(
                    resourceId = "documents",
                    description = "",
                    actions = listOf("create", "read", "write", "delete"),
                ),
            ),
    )

internal class RBACImplTest {
    @MockK
    private lateinit var mockB2BSessionStorage: B2BSessionStorage
    private lateinit var impl: RBACImpl
    private val dispatcher = Dispatchers.Unconfined

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StytchB2BClient)
        MockKAnnotations.init(this, true, true)
        every { mockB2BSessionStorage.lastValidatedAt } returns Date(0)
        coEvery { StytchB2BClient.refreshBootstrapData() } just runs
        impl =
            RBACImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockB2BSessionStorage,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    fun mockLoggedOutMember() {
        every { mockB2BSessionStorage.memberSession } returns null
    }

    fun mockMemberWithRoles(desiredRoles: List<String>) {
        every { mockB2BSessionStorage.memberSession } returns
            mockk(relaxed = true) {
                every { roles } returns desiredRoles
            }
    }

    @Test
    fun `allPermissions refreshes bootstrap data`() =
        runBlocking {
            mockLoggedOutMember()
            every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
            impl.allPermissions()
            coVerify { StytchB2BClient.refreshBootstrapData() }
        }

    @Test
    fun `allPermissions Calculates permissions for a logged-out member`() =
        runBlocking {
            mockLoggedOutMember()
            every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
            val allPerms = impl.allPermissions()
            val expected =
                mapOf(
                    "documents" to
                        mapOf(
                            "create" to false,
                            "delete" to false,
                            "read" to false,
                            "write" to false,
                        ),
                )
            assert(allPerms == expected)
        }

    @Test
    fun `allPermissions Calculates permissions for a default member`() =
        runBlocking {
            mockMemberWithRoles(listOf("default"))
            every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
            val allPerms = impl.allPermissions()
            val expected =
                mapOf(
                    "documents" to
                        mapOf(
                            "create" to false,
                            "delete" to false,
                            "read" to true,
                            "write" to false,
                        ),
                )
            assert(allPerms == expected)
        }

    @Test
    fun `allPermissions Calculates permissions for an admin member`() =
        runBlocking {
            mockMemberWithRoles(listOf("organization_admin"))
            every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
            val allPerms = impl.allPermissions()
            val expected =
                mapOf(
                    "documents" to
                        mapOf(
                            "create" to true,
                            "delete" to true,
                            "read" to true,
                            "write" to true,
                        ),
                )
            assert(allPerms == expected)
        }

    @Test
    fun `allPermissions with a callback invokes callback`() {
        mockLoggedOutMember()
        every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
        val spy = spyk<(Map<String, Map<String, Boolean>>) -> Unit>()
        impl.allPermissions(spy)
        verify { spy.invoke(any()) }
    }

    @Test
    fun `isAuthorizedSync uses cached data and does not update bootstrap`() =
        runBlocking {
            mockMemberWithRoles(listOf("default"))
            every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY_WITHOUT_DEFAULT_ROLE
            val isAuthorized = impl.isAuthorizedSync("documents", "read")
            assert(!isAuthorized)
            coVerify(exactly = 0) { StytchB2BClient.refreshBootstrapData() }
        }

    @Test
    fun `isAuthorizedSync Calculates permissions for a logged-out member`() {
        mockLoggedOutMember()
        val isAuthorized = impl.isAuthorizedSync("documents", "read")
        assert(!isAuthorized)
    }

    @Test
    fun `isAuthorizedSync Calculates permissions for a default member`() {
        every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
        mockMemberWithRoles(listOf("default"))
        val isAuthorizedToRead = impl.isAuthorizedSync("documents", "read")
        val isAuthorizedToDelete = impl.isAuthorizedSync("documents", "delete")
        assert(isAuthorizedToRead)
        assert(!isAuthorizedToDelete)
    }

    @Test
    fun `isAuthorizedSync Calculates permissions for an admin member`() {
        every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
        mockMemberWithRoles(listOf("organization_admin"))
        val isAuthorizedToRead = impl.isAuthorizedSync("documents", "read")
        val isAuthorizedToDelete = impl.isAuthorizedSync("documents", "delete")
        assert(isAuthorizedToRead)
        assert(isAuthorizedToDelete)
    }

    @Test
    fun `isAuthorized always refreshes the bootstrap data`() =
        runBlocking {
            mockLoggedOutMember()
            every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
            impl.isAuthorized("documents", "read")
            coVerify { StytchB2BClient.refreshBootstrapData() }
        }

    @Test
    fun `isAuthorized Calculates permissions for a logged-out member`() =
        runBlocking {
            mockLoggedOutMember()
            val isAuthorized = impl.isAuthorized("documents", "read")
            assert(!isAuthorized)
        }

    @Test
    fun `isAuthorized Calculates permissions for a default member`() =
        runBlocking {
            every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
            mockMemberWithRoles(listOf("default"))
            val isAuthorizedToRead = impl.isAuthorized("documents", "read")
            val isAuthorizedToDelete = impl.isAuthorized("documents", "delete")
            assert(isAuthorizedToRead)
            assert(!isAuthorizedToDelete)
        }

    @Test
    fun `isAuthorized Calculates permissions for an admin member`() =
        runBlocking {
            every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
            mockMemberWithRoles(listOf("organization_admin"))
            val isAuthorizedToRead = impl.isAuthorized("documents", "read")
            val isAuthorizedToDelete = impl.isAuthorized("documents", "delete")
            assert(isAuthorizedToRead)
            assert(isAuthorizedToDelete)
        }

    @Test
    fun `isAuthorized with a callback invokes callback`() {
        mockLoggedOutMember()
        every { StytchB2BClient.bootstrapData.rbacPolicy } returns MOCK_RBAC_POLICY
        val spy = spyk<(Boolean) -> Unit>()
        impl.isAuthorized("documents", "read", spy)
        verify { spy.invoke(any()) }
    }
}

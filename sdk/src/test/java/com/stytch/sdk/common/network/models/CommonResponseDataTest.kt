package com.stytch.sdk.common.network.models

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

private val MOCK_RBAC_POLICY =
    RBACPolicy(
        resources =
            listOf(
                RBACPolicyResource(
                    resourceId = "documents",
                    description = "",
                    actions = listOf("create", "read", "write", "delete"),
                ),
                RBACPolicyResource(
                    resourceId = "images",
                    description = "",
                    actions = listOf("create", "read", "delete"),
                ),
                RBACPolicyResource(
                    resourceId = "secrets",
                    description = "",
                    actions = listOf("read"),
                ),
            ),
        roles =
            listOf(
                RBACPolicyRole(
                    roleId = "default",
                    description = "",
                    permissions = emptyList(),
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
                            RBACPermission(
                                resourceId = "images",
                                actions = listOf("*"),
                            ),
                            RBACPermission(
                                resourceId = "secrets",
                                actions = listOf("*"),
                            ),
                        ),
                ),
                RBACPolicyRole(
                    roleId = "editor",
                    description = "",
                    permissions =
                        listOf(
                            RBACPermission(
                                resourceId = "documents",
                                actions = listOf("read", "write"),
                            ),
                            RBACPermission(
                                resourceId = "images",
                                actions = listOf("create", "read", "delete"),
                            ),
                        ),
                ),
                RBACPolicyRole(
                    roleId = "reader",
                    description = "",
                    permissions =
                        listOf(
                            RBACPermission(
                                resourceId = "documents",
                                actions = listOf("read"),
                            ),
                            RBACPermission(
                                resourceId = "images",
                                actions = listOf("read"),
                            ),
                        ),
                ),
            ),
    )

@RunWith(Parameterized::class)
internal class RBACPolicyCallerIsAuthorizedTest(private val testCase: TestCase) {
    data class TestCase(
        val name: String,
        val subjectRoles: List<String>,
        val resourceId: String,
        val action: String,
        val callerIsAuthorized: Boolean,
    )

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<TestCase> =
            listOf(
                TestCase(
                    name = "Success case - exact match",
                    subjectRoles = listOf("default", "reader"),
                    resourceId = "documents",
                    action = "read",
                    callerIsAuthorized = true,
                ),
                TestCase(
                    name = "Success case - multiple matches",
                    subjectRoles = listOf("default", "reader", "editor", "organization_admin"),
                    resourceId = "documents",
                    action = "read",
                    callerIsAuthorized = true,
                ),
                TestCase(
                    name = "Success case - multiple matches II",
                    subjectRoles = listOf("default", "reader", "editor", "organization_admin"),
                    resourceId = "images",
                    action = "create",
                    callerIsAuthorized = true,
                ),
                TestCase(
                    name = "Failure case - unauthorized action",
                    subjectRoles = listOf("default", "reader"),
                    resourceId = "images",
                    action = "create",
                    callerIsAuthorized = false,
                ),
                TestCase(
                    name = "Failure case - unauthorized resource",
                    subjectRoles = listOf("default", "reader"),
                    resourceId = "secrets",
                    action = "read",
                    callerIsAuthorized = false,
                ),
                TestCase(
                    name = "Failure case - invalid action",
                    subjectRoles = listOf("default", "editor"),
                    resourceId = "documents",
                    action = "burn",
                    callerIsAuthorized = false,
                ),
                TestCase(
                    name = "Failure case - invalid resource",
                    subjectRoles = listOf("default", "editor"),
                    resourceId = "squirrels",
                    action = "write",
                    callerIsAuthorized = false,
                ),
                TestCase(
                    name = "Failure case - invalid role",
                    subjectRoles = listOf("default", "wizard"),
                    resourceId = "documents",
                    action = "write",
                    callerIsAuthorized = false,
                ),
            )
    }

    @Test
    fun test() {
        val actual =
            MOCK_RBAC_POLICY.callerIsAuthorized(
                memberRoles = testCase.subjectRoles,
                resourceId = testCase.resourceId,
                action = testCase.action,
            )
        assert(actual == testCase.callerIsAuthorized)
    }
}

@RunWith(Parameterized::class)
internal class RBACPolicyAllPermissionsForCaller(private val testCase: TestCase) {
    data class TestCase(
        val name: String,
        val subjectRoles: List<String>,
        val expectedPermissions: Map<String, Map<String, Boolean>>,
    )

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<TestCase> =
            listOf(
                TestCase(
                    name = "Returns all false for a caller with no permissions",
                    subjectRoles = listOf("default"),
                    expectedPermissions =
                        mapOf(
                            "documents" to
                                mapOf(
                                    "create" to false,
                                    "read" to false,
                                    "write" to false,
                                    "delete" to false,
                                ),
                            "images" to
                                mapOf(
                                    "create" to false,
                                    "read" to false,
                                    "delete" to false,
                                ),
                            "secrets" to
                                mapOf(
                                    "read" to false,
                                ),
                        ),
                ),
                TestCase(
                    name = "Returns a mix for a caller with some permissions",
                    subjectRoles = listOf("reader"),
                    expectedPermissions =
                        mapOf(
                            "documents" to
                                mapOf(
                                    "create" to false,
                                    "read" to true,
                                    "write" to false,
                                    "delete" to false,
                                ),
                            "images" to
                                mapOf(
                                    "create" to false,
                                    "read" to true,
                                    "delete" to false,
                                ),
                            "secrets" to
                                mapOf(
                                    "read" to false,
                                ),
                        ),
                ),
                TestCase(
                    name = "Returns the union for a caller with multiple roles",
                    subjectRoles = listOf("reader", "editor"),
                    expectedPermissions =
                        mapOf(
                            "documents" to
                                mapOf(
                                    "create" to false,
                                    "read" to true,
                                    "write" to true,
                                    "delete" to false,
                                ),
                            "images" to
                                mapOf(
                                    "create" to true,
                                    "read" to true,
                                    "delete" to true,
                                ),
                            "secrets" to
                                mapOf(
                                    "read" to false,
                                ),
                        ),
                ),
                TestCase(
                    name = "Returns all true for a caller with all permissions",
                    subjectRoles = listOf("organization_admin"),
                    expectedPermissions =
                        mapOf(
                            "documents" to
                                mapOf(
                                    "create" to true,
                                    "read" to true,
                                    "write" to true,
                                    "delete" to true,
                                ),
                            "images" to
                                mapOf(
                                    "create" to true,
                                    "read" to true,
                                    "delete" to true,
                                ),
                            "secrets" to
                                mapOf(
                                    "read" to true,
                                ),
                        ),
                ),
            )
    }

    @Test
    fun test() {
        val actual = MOCK_RBAC_POLICY.allPermissionsForCaller(memberRoles = testCase.subjectRoles)
        println(actual)
        println(testCase.expectedPermissions)
        assert(actual == testCase.expectedPermissions)
    }
}

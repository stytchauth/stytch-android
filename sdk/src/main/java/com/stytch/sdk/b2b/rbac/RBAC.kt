package com.stytch.sdk.b2b.rbac

public interface RBAC {
    /**
     * Determines whether the logged-in member is allowed to perform the specified action on the specified resource.
     * Returns `true` if the member can perform the action, `false` otherwise.
     *
     * This method uses a locally-cached instance of the member and the configured RBAC policy.
     * If the member is not logged in, or the RBAC policy has not been loaded, this method will always return false.
     * If the resource or action provided are not valid for the configured RBAC policy, this method will return false.
     *
     * To check authorization using guaranteed-fresh data, use {@link isAuthorized}.
     * Remember - authorization checks for sensitive actions should always occur on the backend as well.
     */
    public fun isAuthorizedSync(
        resourceId: String,
        action: String,
    ): Boolean

    /**
     * Determines whether the logged-in member is allowed to perform the specified action on the specified resource.
     * Returns `true` if the member can perform the action, `false` otherwise.
     *
     * If the member is not logged in, this method will always return false.
     * If the resource or action provided are not valid for the configured RBAC policy, this method will return false.
     *
     * To check authorization using cached data, use {@link isAuthorizedSync}.
     * Remember - authorization checks for sensitive actions should always occur on the backend as well.
     */
    public suspend fun isAuthorized(
        resourceId: String,
        action: String,
    ): Boolean

    /**
     * Determines whether the logged-in member is allowed to perform the specified action on the specified resource.
     * Returns `true` if the member can perform the action, `false` otherwise.
     *
     * If the member is not logged in, this method will always return false.
     * If the resource or action provided are not valid for the configured RBAC policy, this method will return false.
     *
     * To check authorization using cached data, use {@link isAuthorizedSync}.
     * Remember - authorization checks for sensitive actions should always occur on the backend as well.
     */
    public fun isAuthorized(
        resourceId: String,
        action: String,
        callback: (Boolean) -> Unit,
    )

    /**
     * Evaluates all permissions granted to the logged-in member.
     * Returns a Map<RoleId, Map<Action, Boolean>> response indicating the member's permissions.
     * Each boolean will be `true` if the member can perform the action, `false` otherwise.
     *
     * If the member is not logged in, all values will be false.
     *
     * Remember - authorization checks for sensitive actions should always occur on the backend as well.
     */
    public suspend fun allPermissions(): Map<String, Map<String, Boolean>>

    /**
     * Evaluates all permissions granted to the logged-in member.
     * Returns a Map<RoleId, Map<Action, Boolean>> response indicating the member's permissions.
     * Each boolean will be `true` if the member can perform the action, `false` otherwise.
     *
     * If the member is not logged in, all values will be false.
     *
     * Remember - authorization checks for sensitive actions should always occur on the backend as well.
     */
    public fun allPermissions(callback: (Map<String, Map<String, Boolean>>) -> Unit)
}

package com.stytch.sdk.b2b.scim

import com.stytch.sdk.b2b.SCIMCreateConnectionResponse
import com.stytch.sdk.b2b.SCIMDeleteConnectionResponse
import com.stytch.sdk.b2b.SCIMGetConnectionGroupsResponse
import com.stytch.sdk.b2b.SCIMGetConnectionResponse
import com.stytch.sdk.b2b.SCIMRotateCancelResponse
import com.stytch.sdk.b2b.SCIMRotateCompleteResponse
import com.stytch.sdk.b2b.SCIMRotateStartResponse
import com.stytch.sdk.b2b.SCIMUpdateConnectionResponse
import com.stytch.sdk.b2b.network.models.SCIMGroupImplicitRoleAssignment
import java.util.concurrent.CompletableFuture

/**
 * The SCIM interface provides methods for creating, getting, updating, deleting, and rotating SCIM connections
 */
public interface SCIM {
    /**
     * A data class wrapping the parameters needed to create a SCIM connection
     * @property displayName A human-readable display name for the connection.
     * @property identityProvider The identity provider of this connection.
     */
    public data class CreateConnectionParameters(
        val displayName: String? = null,
        val identityProvider: String? = null,
    )

    /**
     * A data class wrapping the parameters needed to update a SCIM connection
     * @property connectionId Globally unique UUID that identifies a specific SCIM Connection.
     * @property displayName A human-readable display name for the connection.
     * @property identityProvider The identity provider of this connection.
     * @property scimGroupImplicitRoleAssignments An array of implicit role assignments granted to members in this
     * organization who are created via this SCIM connection and belong to the specified group. Before adding any group
     * implicit role assignments, you must first provision groups from your IdP into Stytch
     */
    public data class UpdateConnectionParameters(
        val connectionId: String,
        val displayName: String? = null,
        val identityProvider: String? = null,
        val scimGroupImplicitRoleAssignments: List<SCIMGroupImplicitRoleAssignment>? = null,
    )

    /**
     * A data class wrapping the parameters needed to get connection groups
     * @property limit The maximum number of groups that should be returned by the API.
     * @property cursor The cursor to use to indicate where to start group results.
     */
    public data class GetConnectionGroupsParameters(
        val limit: Int? = null,
        val cursor: String? = null,
    )

    /**
     * Creates a new SCIM connection.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/create-scim-connection create-connection} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param parameters the parameters necessary to create a SCIM connection
     * @return [SCIMCreateConnectionResponse]
     */
    public suspend fun createConnection(parameters: CreateConnectionParameters): SCIMCreateConnectionResponse

    /**
     * Creates a new SCIM connection.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/create-scim-connection create-connection} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param parameters the parameters necessary to create a SCIM connection
     * @param callback a callback that receives a [SCIMCreateConnectionResponse]
     */
    public fun createConnection(
        parameters: CreateConnectionParameters,
        callback: (SCIMCreateConnectionResponse) -> Unit,
    )

    /**
     * Creates a new SCIM connection.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/create-scim-connection create-connection} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param parameters the parameters necessary to create a SCIM connection
     * @return [SCIMCreateConnectionResponse]
     */
    public fun createConnectionCompletable(
        parameters: CreateConnectionParameters,
    ): CompletableFuture<SCIMCreateConnectionResponse>

    /**
     *  Updates an existing SCIM connection.
     *  This method wraps the {@link https://stytch.com/docs/b2b/api/update-scim-connection update-connection} endpoint.
     *  If attempting to modify the `scim_group_implicit_role_assignments` the caller must have the
     *  `update.settings.implicit-roles` permission on the `stytch.organization` resource. For all other fields, the
     *  caller must have the `update` permission on the `stytch.scim` resource. SCIM via the project's RBAC policy &
     *  their role assignments.
     *  @param parameters the parameters necessary to update a SCIM connection
     *  @return [SCIMUpdateConnectionResponse]
     */
    public suspend fun updateConnection(parameters: UpdateConnectionParameters): SCIMUpdateConnectionResponse

    /**
     *  Updates an existing SCIM connection.
     *  This method wraps the {@link https://stytch.com/docs/b2b/api/update-scim-connection update-connection} endpoint.
     *  If attempting to modify the `scim_group_implicit_role_assignments` the caller must have the
     *  `update.settings.implicit-roles` permission on the `stytch.organization` resource. For all other fields, the
     *  caller must have the `update` permission on the `stytch.scim` resource. SCIM via the project's RBAC policy &
     *  their role assignments.
     *  @param parameters the parameters necessary to update a SCIM connection
     *  @param callback a callback that receives a [SCIMUpdateConnectionResponse]
     */
    public fun updateConnection(
        parameters: UpdateConnectionParameters,
        callback: (SCIMUpdateConnectionResponse) -> Unit,
    )

    /**
     *  Updates an existing SCIM connection.
     *  This method wraps the {@link https://stytch.com/docs/b2b/api/update-scim-connection update-connection} endpoint.
     *  If attempting to modify the `scim_group_implicit_role_assignments` the caller must have the
     *  `update.settings.implicit-roles` permission on the `stytch.organization` resource. For all other fields, the
     *  caller must have the `update` permission on the `stytch.scim` resource. SCIM via the project's RBAC policy &
     *  their role assignments.
     *  @param parameters the parameters necessary to update a SCIM connection
     *  @return [SCIMUpdateConnectionResponse]
     */
    public fun updateConnectionCompletable(
        parameters: UpdateConnectionParameters,
    ): CompletableFuture<SCIMUpdateConnectionResponse>

    /**
     * Deletes an existing SCIM connection.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/delete-scim-connection delete-connection} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to be deleted
     * @return [SCIMDeleteConnectionResponse]
     */
    public suspend fun deleteConnection(connectionId: String): SCIMDeleteConnectionResponse

    /**
     * Deletes an existing SCIM connection.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/delete-scim-connection delete-connection} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to be deleted
     * @param callback a callback that receives a [SCIMDeleteConnectionResponse]
     */
    public fun deleteConnection(
        connectionId: String,
        callback: (SCIMDeleteConnectionResponse) -> Unit,
    )

    /**
     * Deletes an existing SCIM connection.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/delete-scim-connection delete-connection} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to be deleted
     * @return [SCIMDeleteConnectionResponse]
     */
    public fun deleteConnectionCompletable(connectionId: String): CompletableFuture<SCIMDeleteConnectionResponse>

    /**
     * Gets the SCIM connection for an organization.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/get-scim-connection get-connection} endpoint. The
     * caller must have permission to view SCIM via the project's RBAC policy & their role assignments.
     * @return [SCIMGetConnectionResponse]
     */
    public suspend fun getConnection(): SCIMGetConnectionResponse

    /**
     * Gets the SCIM connection for an organization.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/get-scim-connection get-connection} endpoint. The
     * caller must have permission to view SCIM via the project's RBAC policy & their role assignments.
     * @param callback a callback that receives a [SCIMGetConnectionResponse]
     */
    public fun getConnection(callback: (SCIMGetConnectionResponse) -> Unit)

    /**
     * Gets the SCIM connection for an organization.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/get-scim-connection get-connection} endpoint. The
     * caller must have permission to view SCIM via the project's RBAC policy & their role assignments.
     * @return [SCIMGetConnectionResponse]
     */
    public fun getConnectionCompletable(): CompletableFuture<SCIMGetConnectionResponse>

    /**
     * Gets all groups associated with an organization's SCIM connection.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/get-scim-connection-groups get-connection-groups}
     * endpoint. The caller must have permission to view SCIM via the project's RBAC policy & their role assignments.
     * @param parameters the parameters necessary to get connection groups
     * @return [SCIMGetConnectionGroupsResponse]
     */
    public suspend fun getConnectionGroups(parameters: GetConnectionGroupsParameters): SCIMGetConnectionGroupsResponse

    /**
     * Gets all groups associated with an organization's SCIM connection.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/get-scim-connection-groups get-connection-groups}
     * endpoint. The caller must have permission to view SCIM via the project's RBAC policy & their role assignments.
     * @param parameters the parameters necessary to get connection groups
     * @param callback a callback that receives a [SCIMGetConnectionGroupsResponse]
     */
    public fun getConnectionGroups(
        parameters: GetConnectionGroupsParameters,
        callback: (SCIMGetConnectionGroupsResponse) -> Unit,
    )

    /**
     * Gets all groups associated with an organization's SCIM connection.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/get-scim-connection-groups get-connection-groups}
     * endpoint. The caller must have permission to view SCIM via the project's RBAC policy & their role assignments.
     * @param parameters the parameters necessary to get connection groups
     * @return [SCIMGetConnectionGroupsResponse]
     */
    public fun getConnectionGroupsCompletable(
        parameters: GetConnectionGroupsParameters,
    ): CompletableFuture<SCIMGetConnectionGroupsResponse>

    /**
     * Starts the SCIM bearer token rotation process.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/scim-rotate-token-start start-rotation} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to start the bearer token rotation process
     * @return [SCIMRotateStartResponse]
     */
    public suspend fun rotateStart(connectionId: String): SCIMRotateStartResponse

    /**
     * Starts the SCIM bearer token rotation process.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/scim-rotate-token-start start-rotation} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to start the bearer token rotation process
     * @param callback a callback that receives a [SCIMRotateStartResponse]
     */
    public fun rotateStart(
        connectionId: String,
        callback: (SCIMRotateStartResponse) -> Unit,
    )

    /**
     * Starts the SCIM bearer token rotation process.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/scim-rotate-token-start start-rotation} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to start the bearer token rotation process
     * @return [SCIMRotateStartResponse]
     */
    public fun rotateStartCompletable(connectionId: String): CompletableFuture<SCIMRotateStartResponse>

    /**
     * Completes the SCIM bearer token rotate, removing the old bearer token from operation.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/scim-rotate-token-complete complete-rotation}
     * endpoint. The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to complete the bearer token rotation process
     * @return [SCIMRotateCompleteResponse]
     */
    public suspend fun rotateComplete(connectionId: String): SCIMRotateCompleteResponse

    /**
     * Completes the SCIM bearer token rotate, removing the old bearer token from operation.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/scim-rotate-token-complete complete-rotation}
     * endpoint. The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to complete the bearer token rotation process
     * @param callback a callback that receives a [SCIMRotateCompleteResponse]
     */
    public fun rotateComplete(
        connectionId: String,
        callback: (SCIMRotateCompleteResponse) -> Unit,
    )

    /**
     * Completes the SCIM bearer token rotate, removing the old bearer token from operation.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/scim-rotate-token-complete complete-rotation}
     * endpoint. The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to complete the bearer token rotation process
     * @return [SCIMRotateCompleteResponse]
     */
    public fun rotateCompleteCompletable(connectionId: String): CompletableFuture<SCIMRotateCompleteResponse>

    /**
     * Cancels the SCIM bearer token rotate, removing the new bearer token issued.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/scim-rotate-token-cancel cancel-rotation} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to cancel a bearer token rotation process
     * @return [SCIMRotateCancelResponse]
     */
    public suspend fun rotateCancel(connectionId: String): SCIMRotateCancelResponse

    /**
     * Cancels the SCIM bearer token rotate, removing the new bearer token issued.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/scim-rotate-token-cancel cancel-rotation} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to cancel a bearer token rotation process
     * @param callback a callback that receives a [SCIMRotateCancelResponse]
     */
    public fun rotateCancel(
        connectionId: String,
        callback: (SCIMRotateCancelResponse) -> Unit,
    )

    /**
     * Cancels the SCIM bearer token rotate, removing the new bearer token issued.
     * This method wraps the {@link https://stytch.com/docs/b2b/api/scim-rotate-token-cancel cancel-rotation} endpoint.
     * The caller must have permission to modify SCIM via the project's RBAC policy & their role assignments.
     * @param connectionId the ID of the connection to cancel a bearer token rotation process
     * @return [SCIMRotateCancelResponse]
     */
    public fun rotateCancelCompletable(connectionId: String): CompletableFuture<SCIMRotateCancelResponse>
}

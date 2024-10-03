package com.stytch.sdk.consumer.userManagement

import com.stytch.sdk.common.network.models.NameData
import com.stytch.sdk.consumer.DeleteFactorResponse
import com.stytch.sdk.consumer.SearchUserResponse
import com.stytch.sdk.consumer.UpdateUserResponse
import com.stytch.sdk.consumer.UserResponse
import com.stytch.sdk.consumer.network.models.UserData
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.CompletableFuture

/**
 * The UserManagement interface provides methods for retrieving an authenticated user and deleting authentication
 * factors from an authenticated user.
 */
public interface UserManagement {
    /**
     * Exposes a flow of user data
     */
    public suspend fun onChange(): StateFlow<StytchUser>

    /**
     * Assign a callback that will be called when the user data changes
     */

    public fun onChange(callback: (StytchUser) -> Unit)

    /**
     * Fetches a user from the current session
     * @return [UserResponse]
     */
    public suspend fun getUser(): UserResponse

    /**
     * Fetches a user from the current session
     * @param callback A callback that receives a [UserResponse]
     */
    public fun getUser(callback: (UserResponse) -> Unit)

    /**
     * Fetches a user from the current session
     * @return [UserResponse]
     */
    public fun getUserCompletable(): CompletableFuture<UserResponse>

    /**
     * Get user from memory without making a network call
     * @return [UserData]
     */
    public fun getSyncUser(): UserData?

    /**
     * Deletes a [UserAuthenticationFactor] from the currently authenticated user
     * @return [DeleteFactorResponse]
     */
    public suspend fun deleteFactor(factor: UserAuthenticationFactor): DeleteFactorResponse

    /**
     * Deletes a [UserAuthenticationFactor] from the currently authenticated user
     * @param callback A callback that receives a [DeleteFactorResponse]
     */
    public fun deleteFactor(
        factor: UserAuthenticationFactor,
        callback: (DeleteFactorResponse) -> Unit,
    )

    /**
     * Deletes a [UserAuthenticationFactor] from the currently authenticated user
     * @return [DeleteFactorResponse]
     */
    public fun deleteFactorCompletable(factor: UserAuthenticationFactor): CompletableFuture<DeleteFactorResponse>

    /**
     * Data class used for wrapping parameters used with User updates
     * @property name the name of the user
     * @property untrustedMetadata a map of untrusted metadata to assign to the user
     */
    public data class UpdateParams
        @JvmOverloads
        constructor(
            val name: NameData? = null,
            val untrustedMetadata: Map<String, Any>? = null,
        )

    /**
     * Updates the currently authenticated user
     * @param params required to udpate the user
     * @return [UpdateUserResponse]
     */
    public suspend fun update(params: UpdateParams): UpdateUserResponse

    /**
     * Updates the currently authenticated user
     * @param params required to udpate the user
     * @param callback a callback that receives an [UpdateUserResponse]
     */
    public fun update(
        params: UpdateParams,
        callback: (UpdateUserResponse) -> Unit,
    )

    /**
     * Updates the currently authenticated user
     * @param params required to udpate the user
     * @return [UpdateUserResponse]
     */
    public fun updateCompletable(params: UpdateParams): CompletableFuture<UpdateUserResponse>

    /**
     * Data class used for wrapping parameters used for searching Users
     * @property email the email address to search for
     */
    public data class SearchParams(
        val email: String,
    )

    /**
     * Searches for the specified user
     * @param params required for searching users
     * @return [SearchUserResponse]
     */
    public suspend fun search(params: SearchParams): SearchUserResponse

    /**
     * Searches for the specified user
     * @param params required for searching users
     * @param callback a callback that receives a [SearchUserResponse]
     */
    public fun search(
        params: SearchParams,
        callback: (SearchUserResponse) -> Unit,
    )

    /**
     * Searches for the specified user
     * @param params required for searching users
     * @return [SearchUserResponse]
     */
    public fun searchCompletable(params: SearchParams): CompletableFuture<SearchUserResponse>
}

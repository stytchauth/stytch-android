package com.stytch.sdk.consumer.userManagement

import com.stytch.sdk.common.network.models.NameData
import com.stytch.sdk.consumer.DeleteFactorResponse
import com.stytch.sdk.consumer.UpdateUserResponse
import com.stytch.sdk.consumer.UserResponse
import com.stytch.sdk.consumer.network.models.UserData

/**
 * The UserManagement interface provides methods for retrieving an authenticated user and deleting authentication
 * factors from an authenticated user.
 */
public interface UserManagement {

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
    public fun deleteFactor(factor: UserAuthenticationFactor, callback: (DeleteFactorResponse) -> Unit)

    /**
     * Data class used for wrapping parameters used with User updates
     * @property name the name of the user
     * @property untrustedMetadata a map of untrusted metadata to assign to the user
     */
    public data class UpdateParams(
        val name: NameData? = null,
        val untrustedMetadata: Map<String, Any>? = null
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
    public fun update(params: UpdateParams, callback: (UpdateUserResponse) -> Unit)
}

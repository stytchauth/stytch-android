package com.stytch.sdk.consumer.userManagement

import com.stytch.sdk.consumer.DeleteFactorResponse
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
}

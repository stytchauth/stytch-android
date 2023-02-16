package com.stytch.sdk.consumer.userManagement

import com.stytch.sdk.consumer.DeleteFactorResponse
import com.stytch.sdk.consumer.UserResponse
import com.stytch.sdk.consumer.network.UserData

public interface UserManagement {

    /**
     * Wraps the getUser API endpoint which return user from current session
     * @return UserResponse response from backend
     */
    public suspend fun getUser(): UserResponse

    /**
     * Wraps the getUser API endpoint which return user from current session
     * @return UserResponse response from backend
     */
    public fun getUser(callback: (UserResponse) -> Unit)

    /**
     * Get user from memory without network call
     * @return locally stored user
     */
    public fun getSyncUser(): UserData?

    /**
     * Wraps the deleteEmailById API endpoint which deletes email from current user
     * @return BaseResponse response from backend
     */
    public suspend fun deleteFactor(factor: UserAuthenticationFactor): DeleteFactorResponse

    /**
     * Wraps the deleteEmailById API endpoint which deletes email from current user
     * @return BaseResponse response from backend
     */
    public fun deleteFactor(factor: UserAuthenticationFactor, callback: (DeleteFactorResponse) -> Unit)
}

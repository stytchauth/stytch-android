package com.stytch.sdk

import com.stytch.sdk.network.responseData.UserData

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
    public suspend fun deleteEmailById(id: String): BaseResponse

    /**
     * Wraps the deleteEmailById API endpoint which deletes email from current user
     * @return BaseResponse response from backend
     */
    public fun deleteEmailById(id: String, callback: (BaseResponse) -> Unit)

    /**
     * Wraps the deletePhoneNumberById API endpoint which deletes phone number from current user
     * @return BaseResponse response from backend
     */
    public suspend fun deletePhoneNumberById(id: String): BaseResponse

    /**
     * Wraps the deletePhoneNumberById API endpoint which deletes phone number from current user
     * @return BaseResponse response from backend
     */
    public fun deletePhoneNumberById(id: String, callback: (BaseResponse) -> Unit)

    /**
     * Wraps the deleteBiometricRegistrationById API endpoint which deletes biometric registration from current user
     * @return BaseResponse response from backend
     */
    public suspend fun deleteBiometricRegistrationById(id: String): BaseResponse

    /**
     * Wraps the deleteBiometricRegistrationById API endpoint which deletes biometric registration from current user
     * @return BaseResponse response from backend
     */
    public fun deleteBiometricRegistrationById(id: String, callback: (BaseResponse) -> Unit)
}
